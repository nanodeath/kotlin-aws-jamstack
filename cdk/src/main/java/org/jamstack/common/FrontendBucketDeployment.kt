package org.jamstack.common

import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.IgnoreMode
import software.amazon.awscdk.services.s3.Bucket
import software.amazon.awscdk.services.s3.assets.AssetOptions
import software.amazon.awscdk.services.s3.deployment.BucketDeployment
import software.amazon.awscdk.services.s3.deployment.BucketDeploymentProps
import software.amazon.awscdk.services.s3.deployment.Source

class FrontendBucketDeployment(scope: Construct, id: String, opts: Opts) : Construct(scope, id) {
    init {
        BucketDeployment(
            this, "deploy-html  ", BucketDeploymentProps.builder()
                .sources(
                    listOf(
                        Source.asset(
                            "../frontend/build/static",
                            AssetOptions.builder().exclude(listOf("*.*", "!*.html")).build()
                        )
                    )
                )
                .destinationBucket(opts.bucket)
                .contentType("text/html;charset=utf-8")
                .prune(false)
                .build()
        )
        BucketDeployment(
            this, "deploy-js-css", BucketDeploymentProps.builder()
                .sources(
                    listOf(
                        Source.asset(
                            "../frontend/build/static",
                            AssetOptions.builder()
                                .exclude(listOf("*.*", "!*.js", "!*.css"))
                                .ignoreMode(IgnoreMode.GIT)
                                .build()
                        )
                    )
                )
                .destinationBucket(opts.bucket)
                .prune(false)
                .build()
        )
    }

    data class Opts(val bucket: Bucket)
}
