plugins {
    `maven-publish`
}

group = "io.github.daylightnebula"
version = "0.0.1"

subprojects {
    apply(plugin = "maven-publish")
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
