package org.jamstack.engine

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

interface Controller {
    operator fun invoke(): APIGatewayProxyResponseEvent
}