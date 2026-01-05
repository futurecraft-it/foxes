plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
    // id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "it.futurecraft.foxes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://www.jitpack.io") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    implementation("com.github.datatags:MobChipLite:47310a6c7f")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.shadowJar {
    archiveBaseName.set(rootProject.name)
    archiveVersion.set(rootProject.version.toString())
    archiveClassifier.set("")

//    manifest {
//        attributes["paperweight-mappings-namespace"] = "spigot"
//    }
}
