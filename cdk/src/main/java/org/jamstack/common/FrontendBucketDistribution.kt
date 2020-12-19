package org.jamstack.common

import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.services.cloudfront.*
import software.amazon.awscdk.services.cloudfront.origins.S3Origin
import software.amazon.awscdk.services.s3.Bucket

class FrontendBucketDistribution(scope: Construct, id: String, opts: Opts) : Construct(scope, id) {
    val distribution = run {
        Distribution(
            this, "distribution", DistributionProps.builder()
                .defaultBehavior(
                    BehaviorOptions.builder()
                        .origin(S3Origin(opts.bucket))
                        .allowedMethods(AllowedMethods.ALLOW_GET_HEAD_OPTIONS)
                        .viewerProtocolPolicy(ViewerProtocolPolicy.REDIRECT_TO_HTTPS)
                        .cachePolicy(CachePolicy.CACHING_OPTIMIZED)
                        .compress(true)
                        .apply(opts.behavior)
                        .build()
                )
                .apply(opts.distributionProps)
                .build()
        )
    }

    data class Opts(val bucket: Bucket, val behavior: (BehaviorOptions.Builder) -> Unit = {}, val distributionProps: (DistributionProps.Builder) -> Unit = {})
}
