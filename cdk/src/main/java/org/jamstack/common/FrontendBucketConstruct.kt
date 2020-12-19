package org.jamstack.common

import software.amazon.awscdk.core.CfnOutput
import software.amazon.awscdk.core.CfnOutputProps
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.services.s3.*

class FrontendBucketConstruct(scope: Construct, id: String, opts: Opts = Opts()) : Construct(scope, id) {
    val bucket: Bucket = run {
        val bucketProps = BucketProps.builder()
            .websiteIndexDocument("index.html")
            .publicReadAccess(true)
            .accessControl(BucketAccessControl.PUBLIC_READ)
            .bucketName(opts.bucketName)
            .cors(opts.cors)
        Bucket(this, "bucket", bucketProps.apply(opts.bucketBuilder).build())
    }

    data class Opts(
        /**
         * Corresponds to [BucketProps.Builder.bucketName]. By default, CloudFormation will set one for you, which is
         * recommended.
         */
        val bucketName: String? = null,
        val bucketBuilder: (BucketProps.Builder) -> Unit = {},
        val cors: List<CorsRule> = emptyList()
    )
}

fun FrontendBucketConstruct.generateCdkOutputForUrl(id: String = "${this.node.id}-output", cb: (CfnOutputProps.Builder) -> Unit = {}) {
    CfnOutput(this, id, CfnOutputProps.builder()
            .value(bucket.bucketWebsiteUrl)
            .exportName("bucket-url-$id")
            .description("The location of the static website: $id")
            .apply(cb)
            .build(),
    )
}