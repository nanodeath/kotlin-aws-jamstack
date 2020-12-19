package org.jamstack.engine

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert

fun ObjectAssert<APIGatewayProxyResponseEvent>.hasResponse(statusCode: Int, body: String) {
    this.extracting { response ->
        assertThat(response.statusCode).describedAs("status code").isEqualTo(statusCode)
        assertThat(response.body).describedAs("body").isEqualTo(body)
    }
}