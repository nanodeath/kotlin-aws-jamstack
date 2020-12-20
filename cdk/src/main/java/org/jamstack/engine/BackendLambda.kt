package org.jamstack.engine

import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.FunctionProps

class BackendLambda(
    scope: Construct,
    id: String,
    opts: Opts
) : Construct(scope, id) {
    val function = Function(this, "application-function", builder(opts).build())

    private fun builder(opts: Opts) = FunctionProps
        .builder()
        .apply(opts.builderOverride)

    data class Opts(val builderOverride: (FunctionProps.Builder) -> Unit = {})
}
