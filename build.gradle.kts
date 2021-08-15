import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    // run `./gradlew dependencyUpdates` to find newer versions of dependencies.
    id("com.github.ben-manes.versions") version "0.39.0"
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

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}
