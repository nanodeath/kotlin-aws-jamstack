package org.jamstack

import org.jamstack.engine.FrontendBucketConstruct
import org.jamstack.engine.FrontendBucketDeployment
import org.jamstack.engine.FrontendBucketDistribution
import org.jamstack.engine.generateCdkOutputForUrl
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Stack

class FrontendStack(scope: Construct, id: String) : Stack(scope, id) {
    init {
        val bucket =
            FrontendBucketConstruct(this, "frontend-bucket", FrontendBucketConstruct.Opts())
        bucket.generateCdkOutputForUrl(scope.node.id + "-frontend-bucket")
        FrontendBucketDeployment(this, "frontend-deployment", FrontendBucketDeployment.Opts(bucket.bucket))
        FrontendBucketDistribution(this, "frontend-distribution", FrontendBucketDistribution.Opts(bucket.bucket))
    }
}