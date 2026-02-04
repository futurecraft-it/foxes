plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.3.0")
    implementation("xyz.jpenilla.run-paper:xyz.jpenilla.run-paper.gradle.plugin:3.0.2")
    implementation("com.modrinth.minotaur:Minotaur:2.8.10")
    implementation("io.papermc.hangar-publish-plugin:io.papermc.hangar-publish-plugin.gradle.plugin:0.1.4")
}
