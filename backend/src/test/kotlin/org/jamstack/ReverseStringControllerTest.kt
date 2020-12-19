package org.jamstack

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.jamstack.engine.ResponseGenerator
import org.jamstack.engine.hasResponse
import org.junit.jupiter.api.Test
import software.amazon.awssdk.http.HttpStatusCode

class ReverseStringControllerTest {
    @Test
    fun works() {
        val request = APIGatewayProxyRequestEvent().withBody("""{"payload": "Hello world!"}""")
        val response = ReverseStringController(request, ResponseGenerator(ObjectMapper())).invoke()
        assertThat(response).hasResponse(HttpStatusCode.OK, """{"payload":"!dlrow olleH"}""")
    }

    @Test
    fun `handles errors`() {
        val request = APIGatewayProxyRequestEvent().withBody("""{"payload": "error:true"}""")
        val response = ReverseStringController(request, ResponseGenerator(ObjectMapper())).invoke()
        assertThat(response).hasResponse(HttpStatusCode.BAD_REQUEST, """{"message":"Not a valid payload?"}""")
    }
}