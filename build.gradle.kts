plugins {
    kotlin("jvm") version "1.5.31"
    // run `./gradlew dependencyUpdates` to find newer versions of dependencies.
    id("com.github.ben-manes.versions") version "0.39.0"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.10"
}

group = "org.jamstack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
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
