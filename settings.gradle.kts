dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://api.modrinth.com/maven") }
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "foxes"

include("paper")
include("api")

include("v1_21_R6")
