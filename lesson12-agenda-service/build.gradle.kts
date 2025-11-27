plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":lesson12-agenda-repository"))

    // For dependency injection
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    // To get password encode
    implementation("org.springframework.security:spring-security-core:6.5.5")

    // To use PreDestroy annotation
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    // To use SLF4J
    implementation("org.slf4j:slf4j-api:2.0.16")

    testImplementation(platform("org.junit:junit-bom:5.12.0"))
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
