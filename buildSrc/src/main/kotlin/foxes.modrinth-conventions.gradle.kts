import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
    id("com.modrinth.minotaur")
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set("foxes")
    versionType.set("release")

    loaders.addAll("paper", "purpur")

    uploadFile.set(tasks.shadowJar)
}
