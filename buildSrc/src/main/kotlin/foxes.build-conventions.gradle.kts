plugins {
    id("java-library")
    id("com.gradleup.shadow")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    manifest.attributes(
        "paperweight-mappings-namespace" to "mojang",
    )
}

tasks.shadowJar {
    mergeServiceFiles()
    // Needed for mergeServiceFiles to work properly in Shadow 9+
    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}