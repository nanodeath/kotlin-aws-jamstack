package org.jamstack.config

import org.jamstack.ClockController
import org.jamstack.ReverseStringController
import org.jamstack.engine.*

fun Router.configure() {
    add<ClockController>("time") {
        // Sample route that routes /time to ClockController
        addCommonInterceptors()
    }
    add<ReverseStringController>("reverse") {
        // Sample route that routes /reverse to ReverseStringController
        addCommonInterceptors()
    }
}

private fun MutableRouteConfiguration.addCommonInterceptors() {
    addInterceptor(CorsAllowOriginInterceptor.AnyOrigin)
}