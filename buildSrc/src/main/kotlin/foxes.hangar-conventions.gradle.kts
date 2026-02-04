import io.papermc.hangarpublishplugin.model.Platforms
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
    id("io.papermc.hangar-publish-plugin")
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        channel.set("Release")
        id.set("foxes")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                // Set the JAR file to upload
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })

                // Set platform versions from gradle.properties file
                val versions: List<String> = listOf("1.21.10-1.21.11")
                platformVersions.set(versions)
            }
        }
    }
}