plugins {
    alias(libs.plugins.kotlin.jvm)
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
}

group = "pt.isel"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":lesson12-agenda-http"))
    implementation(project(":lesson13-agenda-repository-jdbi"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // for JDBI and Postgres
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")

    // To use WebTestClient in integration tests with real HTTP server
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")

    // To automatically run the Spring MVC web server in coordination with unit tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.bootRun {
    environment("DB_URL", "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
}

tasks.test {
    useJUnitPlatform()
    environment("DB_URL", "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
}
kotlin {
    jvmToolchain(21)
}

/**
 * Docker related tasks
 */
val dockerImageJvm = "agenda-jvm"
val dockerImagePostgres = "agenda-postgres"
val dockerImageUbuntu = "agenda-ubuntu"
val dockerExe =
    when (
        org.gradle.internal.os.OperatingSystem
            .current()
    ) {
        org.gradle.internal.os.OperatingSystem.MAC_OS -> "/usr/local/bin/docker"
        org.gradle.internal.os.OperatingSystem.WINDOWS -> "docker"
        else -> "docker" // Linux and others
    }

tasks.register<Copy>("extractUberJar") {
    dependsOn("assemble")
    // opens the JAR containing everything...
    from(
        zipTree(
            layout.buildDirectory
                .file("libs/lesson12-agenda-app-$version.jar")
                .get()
                .toString(),
        ),
    )
    // ... into the 'build/dependency' folder
    into("build/dependency")
}

tasks.register<Exec>("buildImageJvm") {
    dependsOn("extractUberJar")
    commandLine(dockerExe, "build", "-t", dockerImageJvm, "-f", "docker/Dockerfile-jvm", ".")
}

tasks.register<Exec>("buildImagePostgres") {
    commandLine(
        dockerExe,
        "build",
        "-t", // Flag to assign a tag to the image
        dockerImagePostgres, // Name:tag of the image to be built (e.g., "my-postgres:test")
        "-f", // Flag to specify a custom Dockerfile
        "docker/Dockerfile-postgres", // Path to the Dockerfile used to build the image
        "../lesson13-agenda-repository-jdbi", // Build context directory containing files referenced by the Dockerfile
    )
}

tasks.register<Exec>("buildImageUbuntu") {
    commandLine(dockerExe, "build", "-t", dockerImageUbuntu, "-f", "docker/Dockerfile-ubuntu", ".")
}

tasks.register("buildImageAll") {
    dependsOn("buildImageJvm")
    dependsOn("buildImagePostgres")
    dependsOn("buildImageUbuntu")
}

tasks.register<Exec>("allUp") {
    commandLine(dockerExe, "compose", "up", "--force-recreate", "-d")
}

tasks.register<Exec>("allDown") {
    commandLine(dockerExe, "compose", "down")
}
