import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.bokhko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://packages.confluent.io/maven/")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.nuclio:nuclio-sdk-java:1.1.0")
    implementation("org.apache.kafka:kafka-clients:3.2.1")
    implementation("io.confluent:kafka-json-serializer:5.0.1")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    shadowJar {
        archiveBaseName.set("user-handler")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}