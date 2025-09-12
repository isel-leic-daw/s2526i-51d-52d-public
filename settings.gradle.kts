plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "daw2025"
include("lesson01-intro-DAW")
include("lesson02-movies")
include("lesson03-spring-context")
include("lesson03-ioc-and-di-container")
include("lesson03-movies-in-ioc-container")
