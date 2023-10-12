plugins {
    `maven-publish`
}

group = "io.github.daylightnebula"
version = "0.0.2"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                url.set("https://github.com/DaylightNebula/KevinEngine")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("DaylightNebula")
                        name.set("Noah Shaw")
                        email.set("noah.w.shaw@gmail.com")
                    }
                }
            }
        }
    }
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
