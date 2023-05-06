val kotlinVersion: String = "1.8.21"
val ktorVersion: String = "2.3.0"
val exposedVersion: String = "0.41.1"
val postgresqlDriverVersion: String = "42.6.0"
val logbackVersion: String = "1.4.7"

plugins {
    application
    kotlin("jvm") version "1.8.21"
    id("com.github.ben-manes.versions") version "0.46.0"
}

group = "chronotraxx"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresqlDriverVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.hubspot.jinjava:jinjava:2.7.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(19)
}

application {
    mainClass.set("ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}