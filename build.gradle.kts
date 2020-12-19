import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    // run `./gradlew dependencyUpdates -Drevision=release` to find newer versions of dependencies.
    id("com.github.ben-manes.versions") version "0.36.0"
}

group = "org.jamstack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.getByName<Delete>("clean") {
    doFirst {
        exec(object : Action<ExecSpec> {
            override fun execute(spec: ExecSpec) {
                spec.workingDir = projectDir.resolve("cdk")
                spec.commandLine = listOf("mvn", "clean")
            }
        })
    }
}

tasks.register<Exec>("deploy") {
    dependsOn(":backend:shadowJar", ":frontend:assemble")
    description = "This is the main target. Run this to build and deploy everything."
    group = "deploy"
    workingDir = projectDir.resolve("cdk")
    commandLine = listOf("cdk", "deploy", "--require-approval", "never", "--all")
}
