plugins {
    `maven-publish`
    kotlin("plugin.serialization") version "1.9.10"
}

group = "io.github.daylightnebula"
version = "0.0.1"

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/DaylightNebula/KevinEngine")
                credentials {
                    username = project.findProperty("github.username") as? String ?: System.getenv("GITHUB_ACTOR")
                    password = project.findProperty("github.token") as? String ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
