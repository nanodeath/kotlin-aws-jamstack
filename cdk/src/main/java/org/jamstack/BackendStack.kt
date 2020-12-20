package org.jamstack

import org.jamstack.engine.BackendLambda
import org.jamstack.engine.BackendRestApi
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.core.Stack
import software.amazon.awscdk.services.apigateway.StageOptions
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Runtime

class BackendStack(scope: Construct, id: String) : Stack(scope, id) {
    init {
        val backendApplication = BackendLambda(this, "backend-application", BackendLambda.Opts { builder ->
            builder.functionName("${scope.node.id}-jamstack-lambdahandler")
                .handler("org.jamstack.engine.LambdaHandler")
                .code(Code.fromAsset("../backend/build/libs/backend-0.1-all.jar"))
                .runtime(Runtime.JAVA_11)
                .timeout(Duration.seconds(15L))
                .memorySize(1024L)
        })

        BackendRestApi(
            this, "backend-rest", BackendRestApi.Opts(backendApplication.function) { apiProps ->
                apiProps
                    .deployOptions(StageOptions.builder().variables(mapOf("RETURN_STACKTRACES" to "true")).build())
                    .minimumCompressionSize(2048)
            }
        )
    }
}