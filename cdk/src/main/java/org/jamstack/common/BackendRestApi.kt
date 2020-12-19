package org.jamstack.common

import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.services.apigateway.LambdaRestApi
import software.amazon.awscdk.services.apigateway.LambdaRestApiProps
import software.amazon.awscdk.services.lambda.Function

class BackendRestApi(scope: Construct, id: String, opts: Opts) : Construct(scope, id) {
    init {
        LambdaRestApi(
            this, "lambda-rest-api", LambdaRestApiProps.builder()
                .handler(opts.function)
                .apply(opts.apiProps)
                .build()
        )
    }

    data class Opts(val function: Function, val apiProps: (LambdaRestApiProps.Builder) -> Unit)
}
