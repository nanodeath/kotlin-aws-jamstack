package org.jamstack.engine

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper

class ResponseGenerator(private val objectMapper: ObjectMapper) {
    fun Throwable.intoResponse(
        statusCode: Int,
        reason: String,
        request: APIGatewayProxyRequestEvent,
        response: APIGatewayProxyResponseEvent = APIGatewayProxyResponseEvent()
    ): APIGatewayProxyResponseEvent {
        return response.apply {
            isBase64Encoded = false
            headers = mutableMapOf("Content-Type" to "application/json")
            this.statusCode = statusCode
            response.body = objectMapper.writeValueAsString(
                mapOf(
                    "message" to reason
                ).augmentWithError(request, this@intoResponse)
            )
        }
    }
}
