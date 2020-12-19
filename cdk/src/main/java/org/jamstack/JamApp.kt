@file:JvmName("JamApp")

package org.jamstack

import software.amazon.awscdk.core.App

fun main() {
    val app = App()
    JamStack(app, "KotlinAwsJamStack")
    app.synth()
}