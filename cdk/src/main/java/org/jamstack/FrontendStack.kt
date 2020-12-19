package org.jamstack

import org.jamstack.common.FrontendBucketConstruct
import org.jamstack.common.FrontendBucketDeployment
import org.jamstack.common.FrontendBucketDistribution
import org.jamstack.common.generateCdkOutputForUrl
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.NestedStack
import software.amazon.awscdk.core.Stack
import software.amazon.awscdk.services.s3.CorsRule
import software.amazon.awscdk.services.s3.HttpMethods

class FrontendStack(scope: Construct, id: String) : Stack(scope, id) {
    init {
        val bucket =
            FrontendBucketConstruct(this, "frontend-bucket", FrontendBucketConstruct.Opts())
        bucket.generateCdkOutputForUrl(scope.node.id + "-frontend-bucket")
        FrontendBucketDeployment(this, "frontend-deployment", FrontendBucketDeployment.Opts(bucket.bucket))
        FrontendBucketDistribution(this, "frontend-distribution", FrontendBucketDistribution.Opts(bucket.bucket))
    }
}