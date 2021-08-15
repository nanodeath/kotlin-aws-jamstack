group = "org.jamstack"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm")
    idea
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

val kotlinxHtmlVersion: String by extra

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
    implementation("org.eclipse.jetty:jetty-server:11.0.6")
}

idea {
    module {
        sourceDirs.add(projectDir.resolve("src/sass"))
        excludeDirs.add(projectDir.resolve("static/vendor"))
    }
}

val staticOutputDirectory = buildDir.resolve("static")

fun File.relativeToProject() = relativeTo(projectDir)

val compileCss = tasks.register<Exec>("compileCss") {
    group = "build"
    val inputDir = projectDir.resolve("src/sass")
    val outputDir = staticOutputDirectory
    description = "Compiles the Sass in ${inputDir.relativeToProject()} into ${outputDir.relativeToProject()}"
    inputs.dir(inputDir)
    outputs.dir(outputDir)
    commandLine = listOf("sass", "${inputDir}:${outputDir}")
}

val generateHtml = tasks.register<JavaExec>("generateHtml") {
    group = "build"
    mainClass.set("ApplicationKt")
    description = "Generate HTML by invoking $mainClass"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(staticOutputDirectory.toString())
    outputs.dir(staticOutputDirectory)
}

val static = tasks.register<Copy>("static") {
    group = "build"
    val src = projectDir.resolve("src/static")
    from(src)
    description = "Copies files directly from ${src.relativeToProject()} to ${staticOutputDirectory.relativeToProject()}"
    into(staticOutputDirectory)
}

val assets = tasks.register("assets") {
    group = "build"
    description = "Generates and copies all static site targets"
    dependsOn(static, compileCss, generateHtml)
}
tasks.getByName("assemble").dependsOn(assets)

tasks.register<JavaExec>("server") {
    dependsOn.add(assets)
    group = "Execution"
    description = "Start DevServer, for local development."
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf(staticOutputDirectory.toString())
    mainClass.set("engine.DevServerKt")
}

fun File.withExtension(ext: String): File = resolveSibling("$nameWithoutExtension.$ext")
