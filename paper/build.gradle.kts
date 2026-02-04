plugins {
    id("foxes.build-conventions")
    id("foxes.common-conventions")
    id("foxes.modrinth-conventions")
}

dependencies {
    implementation("org.bstats:bstats-bukkit:3.1.0")
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    implementation(project(":api"))

    implementation(project(":v1_21_10"))
    implementation(project(":v1_21_11"))
}
