package org.jamstack

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jamstack.engine.Controller
import software.amazon.awssdk.http.HttpStatusCode
import java.time.Clock
import java.time.format.DateTimeFormatter

@Serializable
data class ClockResponse(val human: String, val unixTimeMs: Long)

/**
 * Sample controller that returns the current time. It does not use the request object.
 */
class ClockController(
    private val clock: Clock
) : Controller {
    override fun invoke(): APIGatewayProxyResponseEvent {
        val time = clock.instant()
        val response = ClockResponse(DateTimeFormatter.ISO_INSTANT.format(time), time.toEpochMilli())
        return APIGatewayProxyResponseEvent()
            .withBody(Json.encodeToString(response))
            .withStatusCode(HttpStatusCode.OK)
    }
}