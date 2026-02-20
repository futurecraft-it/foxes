group = "it.futurecraft.foxes"
version = "2.0-SNAPSHOT"

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
