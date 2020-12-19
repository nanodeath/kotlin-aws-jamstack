package org.jamstack.engine

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.jamstack.config.configure
import org.jamstack.config.configureRequestBindings
import org.jamstack.config.configureSingletonBindings
import org.koin.core.context.startKoin
import org.koin.dsl.module
import software.amazon.awssdk.http.HttpStatusCode
import java.io.InputStream
import java.io.OutputStream

private val logger = KotlinLogging.logger {}
class LambdaHandler : RequestStreamHandler {
    private val objectMapper: ObjectMapper = ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    private val responseGenerator = ResponseGenerator(objectMapper)
    private val router = Router().also { it.configure() }

    /**
     * This module is constructed once, when the lambda is initialized.
     *
     * Additions should typically be added to [org.jamstack.config.configureSingletonBindings].
     */
    private val singletonModule = module(createdAtStart = true) {
        single { objectMapper }
        single { responseGenerator }
    }

    private val userSingletonModule = module {
        this.configureSingletonBindings()
    }

    /**
     * This module is constructed on every request.
     *
     * Additions should typically be added to [org.jamstack.config.configureRequestBindings].
     */
    private fun requestModule(request: APIGatewayProxyRequestEvent) = module {
        single { request }
    }

    /**
     * See [org.jamstack.config.configureRequestBindings].
     */
    private val userRequestModule = module {
        this.configureRequestBindings()
    }

    private val koinApp = startKoin {
        printLogger()
        modules(singletonModule, userSingletonModule)
    }

    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val request = input.bufferedReader().use { br -> objectMapper.readValue(br, APIGatewayProxyRequestEvent::class.java) }
        val pathParameters: Map<String, String>? = request.pathParameters

        val requestModule = requestModule(request)
        koinApp.modules(requestModule, userRequestModule)
        try {
            val route = router.resolve(pathParameters?.get("proxy").orEmpty())
            if (route != null) {
                val controller = koinApp.koin.get(route.controller)
                var response = controller.invoke()
                response = processInterceptors(route, request, response)
                output.bufferedWriter().use { objectMapper.writeValue(it, response) }
            } else {
                handleNotFound(output)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            koinApp.unloadModules(listOf(requestModule, userRequestModule))
        }
    }

    private fun processInterceptors(
        route: RouteConfiguration,
        request: APIGatewayProxyRequestEvent,
        response: APIGatewayProxyResponseEvent
    ): APIGatewayProxyResponseEvent {
        var returnedResponse = response
        for (interceptor: Container<Interceptor> in route.interceptors) {
            try {
                when (val chain = interceptor.getInstance(koinApp.koin).handle(returnedResponse)) {
                    Interceptor.Result.Continue -> {
                        // no handling required
                    }
                    is Interceptor.Result.ReplaceAndContinue -> returnedResponse = chain.response
                    Interceptor.Result.Stop -> break
                    is Interceptor.Result.ReplaceAndStop -> return chain.response
                }
            } catch (e: RuntimeException) {
                logger.error(e) { "Error in interceptor: $interceptor" }
                with(responseGenerator) {
                    return e.intoResponse(HttpStatusCode.INTERNAL_SERVER_ERROR, "internal error", request)
                }
            }
        }
        return returnedResponse
    }

    private fun handleNotFound(output: OutputStream) {
        output.bufferedWriter().use { bw ->
            objectMapper.writeValue(bw, APIGatewayProxyResponseEvent().apply {
                statusCode = HttpStatusCode.NOT_FOUND
            })
        }
    }
}
