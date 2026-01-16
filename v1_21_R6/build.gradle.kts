plugins {
    id("foxes.common-conventions")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

dependencies {
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")

    implementation(project(":api"))
}