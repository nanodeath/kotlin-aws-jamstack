package org.jamstack.engine

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

/**
 * Interceptors modify the response sent back from controllers.
 *
 * Each one returns a [Result] that determines the next step to take, potentially aborting further processing.
 */
interface Interceptor {
    fun handle(response: APIGatewayProxyResponseEvent): Result

    /**
     * Determine what action should be taken after this interceptor.
     */
    sealed class Result {
        /** Continue to the next interceptor. */
        object Continue : Result()

        /**
         * Continue to the next interceptor, but proceed using the given [response] instead of the response passed in
         * to the interceptor.
         */
        class ReplaceAndContinue(val response: APIGatewayProxyResponseEvent) : Result()

        /**
         * Don't invoke any other interceptors after this one.
         */
        object Stop : Result()

        /**
         * Don't invoke any other interceptors after this one, and use the returned [response] instead of the response
         * passed in to the interceptor.
         */
        class ReplaceAndStop(val response: APIGatewayProxyResponseEvent) : Result()
    }
}

class CorsAllowOriginInterceptor(val origin: String) : Interceptor {
    override fun handle(response: APIGatewayProxyResponseEvent): Interceptor.Result {
        response.initializeHeaders()
        response.headers["Access-Control-Allow-Origin"] = origin
        return Interceptor.Result.Continue
    }

    companion object {
        /**
         * Use this to allow any site to hit your Lambda endpoint from any origin. You should evaluate whether this is
         * a security concern, as it allows any site to AJAX this endpoint.
         * For details, see:
         * * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Origin
         * * https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
         */
        val AnyOrigin = CorsAllowOriginInterceptor("*")
    }
}