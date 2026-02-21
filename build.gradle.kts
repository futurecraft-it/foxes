group = "it.futurecraft.foxes"
version = "2.1-SNAPSHOT"

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
