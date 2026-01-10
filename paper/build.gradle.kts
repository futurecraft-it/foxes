plugins {
    id("foxes.build-conventions")
    id("foxes.common-conventions")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    implementation(project(":api"))

    implementation(project(":v1_21_R6"))
}
