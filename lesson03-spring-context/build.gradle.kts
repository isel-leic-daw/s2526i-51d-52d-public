plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    // The movies domain
    implementation(project(":lesson02-movies"))

    implementation("org.springframework:spring-context:6.1.12")

    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.slf4j:slf4j-simple:2.0.16")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
