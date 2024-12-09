import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    kotlin("jvm") version "2.1.0"
    id("io.papermc.paperweight.userdev") version "1.7.6" apply false
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    id("io.github.goooler.shadow") version "8.1.8"
}

val minecraft = "1.21.4"

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    group = "kr.toxicity.traffic"
    version = "1.0"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        testImplementation(kotlin("test"))
        implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")
    }

    tasks {
        test {
            useJUnitPlatform()
        }
        compileJava {
            options.encoding = Charsets.UTF_8.name()
        }
    }
    kotlin {
        jvmToolchain(21)
    }
}

fun Project.dependency(any: Any) = also {
    it.dependencies {
        fun inject(a: Any) {
            compileOnly(a)
            testImplementation(a)
        }
        if (any is Collection<*>) {
            any.forEach { a ->
                inject(a ?: return@forEach)
            }
        } else inject(any)
    }
}
fun Project.paper() = dependency("io.papermc.paper:paper-api:$minecraft-R0.1-SNAPSHOT")

val api = project("api").paper()

fun Project.api() = dependency(api)

val nms: Set<Project> = project("nms").subprojects.onEach {
    it.apply(plugin = "io.papermc.paperweight.userdev")
    it.api()
}
val core = project("core")
    .api()
    .paper()
    .dependency(nms)

dependencies {
    implementation(api)
    implementation(core)
    nms.forEach {
        implementation(project("nms:${it.name}", configuration = "reobf"))
    }
}

tasks {
    jar {
        finalizedBy(shadowJar)
    }
    shadowJar {
        archiveClassifier = ""
        exclude("LICENSE")
        manifest {
            attributes["paperweight-mappings-namespace"] = "spigot"
        }
        fun prefix(pattern: String) {
            relocate(pattern, "${project.group}.shaded.$pattern")
        }
        prefix("kotlin")
        prefix("dev.jorel.commandapi")
    }
    runServer {
        version(minecraft)
    }
}

bukkitPluginYaml {
    main = "${project.group}.TrafficProfilerImpl"
    apiVersion = "1.20"
    name = rootProject.name

    author = "toxicity"
    description = "Profile server traffic."

    permissions.create("traffic.generate") {
        default = Permission.Default.OP
        description = "Generates profile result."
    }
    permissions.create("traffic.reload") {
        default = Permission.Default.OP
        description = "Reloads plugin."
    }
}
