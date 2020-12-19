package org.jamstack.config

import org.jamstack.ClockController
import org.jamstack.ReverseStringController
import org.koin.core.module.Module
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Clock

/**
 * These bindings are initialized once, on startup. They can't access request state.
 *
 * You can inject anything from the following modules:
 * * this module
 * * the engine [singleton bindings][org.jamstack.engine.LambdaHandler.singletonModule]
 */
fun Module.configureSingletonBindings() {
    single { DynamoDbClient.builder().httpClientBuilder(UrlConnectionHttpClient.builder()).build() }
    single { Clock.systemUTC() }
}

/**
 * These bindings are initialized on every request. They can access request state.
 *
 * You can inject anything from the following modules:
 * * this module
 * * the engine [request bindings][org.jamstack.engine.LambdaHandler.requestModule]
 * * your [singleton bindings][configureSingletonBindings]
 * * the engine [singleton bindings][org.jamstack.engine.LambdaHandler.singletonModule]
 */
fun Module.configureRequestBindings() {
    single { ClockController(clock = get()) }
    single { ReverseStringController(request = get(), responseGenerator = get()) }
}