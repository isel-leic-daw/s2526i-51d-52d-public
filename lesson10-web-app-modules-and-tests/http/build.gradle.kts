plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":lesson10-web-app-modules-and-tests:service"))

    // To use Spring MVC
    implementation("org.springframework:spring-webmvc:6.2.11")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // To mock services
    testImplementation("io.mockk:mockk:1.14.3")

    testImplementation("org.springframework:spring-test:6.2.11")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
