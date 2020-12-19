package org.jamstack

import software.amazon.awscdk.core.*

class JamStack(scope: Construct, id: String, props: StackProps? = null) : Stack(scope, id, props) {
    init {
        /*
         * These could be NestedStacks, but aren't -- primarily because debugging errors with nested stacks is
         * somewhat challenging due to obfuscated errors. See https://github.com/aws/aws-cdk/issues/5974 .
         */
        BackendStack(this, "backend-stack")
        FrontendStack(this, "frontend-stack")
    }
}
