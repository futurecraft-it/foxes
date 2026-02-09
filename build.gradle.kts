group = "it.futurecraft.foxes"
version = "1.2-SNAPSHOT"

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
