import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.0.1")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization") version "1.4.21"
    id("org.jetbrains.kotlin.kapt")
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

version = "0.1"
group = "org.jamstack"

// These comes from gradle.properties
val jacksonVersion: String by project
val koinVersion: String by project
val log4jVersion: String by project

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.15.50"))
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("com.amazonaws:aws-lambda-java-events:3.7.0")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")

    // Serialization
    // Use kotlinx.serialization when you can; Jackson when you must
    implementation("com.fasterxml.jackson.jr:jackson-jr-objects:$jacksonVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    implementation("org.koin:koin-core:$koinVersion")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.4")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j18-impl:$log4jVersion")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.2.0")

    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.10.3")
    testImplementation("org.assertj:assertj-core:3.18.1")
    testImplementation("org.koin:koin-test:$koinVersion")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

// Using Proguard shaves off less size than you might expect, but you can use it if you like.
tasks.register<proguard.gradle.ProGuardTask>("proguard") {
    dependsOn("shadowJar")
    val packageNamespace = "org.jamstack"
    // At least on Fedora, I had to run this: sudo yum install java-11-openjdk-jmods
    libraryjars("/usr/lib/jvm/java-11/jmods")

    injars(buildDir.resolve("libs/backend-0.1-all.jar"))
    outjars(buildDir.resolve("libs/backend-0.1-all-pro.jar"))

    keep("class $packageNamespace.**")
    keep(mapOf("includedescriptorclasses" to true), "class $packageNamespace.**\$\$serializer { *; }")
    keepclassmembers("""
        class $packageNamespace.** {
            *** Companion;
        }
    """.trimIndent())
    keepclasseswithmembers("""
        class $packageNamespace.** {
            kotlinx.serialization.KSerializer serializer(...);
        }
    """.trimIndent())
    keepattributes("Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod")
    keepclassmembers("""
        class kotlinx.serialization.json.** {
            *** Companion;
        }
    """.trimIndent())
    keepclasseswithmembernames("""
        class kotlinx.serialization.json.** {
            kotlinx.serialization.KSerializer serializer(...);
        }
    """.trimIndent())
    keepclassmembers("""
        enum * {
            public static **[] values();
            public static ** valueOf(java.lang.String);
        }
    """.trimIndent())
    keep("class software.amazon.awssdk.core.** {*;}")
    keep("class com.amazonaws.services.lambda.** {*;}")
    keep("""
        class com.fasterxml.jackson.databind.ObjectMapper {
            public <methods>;
            protected <methods>;
        }
    """.trimIndent())
    keep("""
        class com.fasterxml.jackson.databind.ObjectWriter {
            public ** writeValueAsString(**);
        }
    """.trimIndent())
    keepnames("class com.fasterxml.jackson.** { *; }")
    dontnote("kotlinx.serialization.AnnotationsKt")
    dontwarn("org.joda.**")
    dontwarn("org.apache.commons.logging.**")
    dontwarn("io.netty.**")
    dontwarn("org.slf4j.**")

    dontobfuscate()
    dontoptimize()
}