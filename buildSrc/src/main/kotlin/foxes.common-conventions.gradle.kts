plugins {
    `java-library`
}

group = "it.futurecraft.foxes"
version = "1.0-SNAPSHOT"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    withType<Javadoc>().configureEach {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}
