package org.jamstack.engine

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

val APIGatewayProxyRequestEvent.cookies: Map<String, String>
    get() {
        val map = mutableMapOf<String, String>()
        headers?.get("Cookie")?.split(", ")?.map { pair ->
            val k = pair.substringBefore('=')
            val v = pair.substringAfter('=')
            map[k] = v
        }
        return map
    }

fun APIGatewayProxyResponseEvent.initializeHeaders() {
    if (this.headers == null) {
        this.headers = mutableMapOf()
    }
}

fun Map<String, String>.augmentWithError(request: APIGatewayProxyRequestEvent, e: Throwable): Map<String, String> {
    e.printStackTrace()
    return if (request.stageVariables?.get("RETURN_STACKTRACES")?.toBoolean() == true) {
        this + mapOf(
            "error" to "${e.javaClass}: ${e.message}",
            "stacktrace" to e.stackTraceToString()
        )
    } else this
}
