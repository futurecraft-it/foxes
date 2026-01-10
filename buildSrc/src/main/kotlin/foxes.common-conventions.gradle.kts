plugins {
    `java-library`
}

group = rootProject.group
version = rootProject.version

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    withType<Javadoc>().configureEach {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}
