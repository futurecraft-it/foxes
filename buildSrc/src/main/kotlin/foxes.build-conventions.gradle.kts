plugins {
    id("java-library")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper")
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
    archiveClassifier.set("")
    archiveVersion.set(rootProject.version as String)
    archiveBaseName.set(rootProject.name)

    mergeServiceFiles()
    // Needed for mergeServiceFiles to work properly in Shadow 9+
    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

tasks {
    runServer {
        minecraftVersion("1.21.10")
    }
}