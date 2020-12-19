package org.jamstack

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jamstack.engine.Controller
import org.jamstack.engine.ResponseGenerator
import software.amazon.awssdk.http.HttpStatusCode

@Serializable
data class ReverseStringRequest(val payload: String)

@Serializable
data class ReverseStringResponse(val payload: String)

/**
 * Sample controller that reverses the string passed in the body of a AJAX POST request.
 *
 * It can return errors.
 */
class ReverseStringController(
    private val request: APIGatewayProxyRequestEvent,
    private val responseGenerator: ResponseGenerator
) : Controller {
    override fun invoke(): APIGatewayProxyResponseEvent =
        try {
            val decodedRequest: ReverseStringRequest = Json.decodeFromString(request.body)
            val reversed = decodedRequest.payload.reversed()

            if (":" in reversed) {
                // Completely arbitrary, but demonstrates error handling.
                throw IllegalArgumentException("Can't reverse strings containing :!")
            }

            APIGatewayProxyResponseEvent()
                .withBody(Json.encodeToString(ReverseStringResponse(reversed)))
                .withStatusCode(HttpStatusCode.OK)
        } catch (e: Exception) {
            with(responseGenerator) {
                e.intoResponse(HttpStatusCode.BAD_REQUEST, "Not a valid payload?", request)
            }
        }
}