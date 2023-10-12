plugins {
    `maven-publish`
}

group = "io.github.daylightnebula"
version = "0.0.1"

subprojects {
    apply(plugin = "maven-publish")
    publishing {
        repositories {
            maven {}
        }
    }
}

//fun String.dasherize() = fold("") {acc, value ->
//    if (value.isUpperCase()) {
//        "$acc-${value.toLowerCase()}"
//    } else {
//        "$acc$value"
//    }
//}
//
//fun makeArtifactId(name: String) =
//    if ("kotlinMultiplatform" in name) {
//        mvnArtifactId
//    } else {
//        "$mvnArtifactId-${name.dasherize()}"
//    }

//publishing {
//    publications {
//        repositories {
//            maven {
//
//            }
//        }
//    }
//        maven {
//            groupId = project.group.toString()
//            artifactId = project.name
//            version = project.version.toString()
//            println("Artifact name $name")
//
//            pom {
//                url.set("https://github.com/DaylightNebula/KevinEngine")
//                licenses {
//                    license {
//                        name.set("The Apache License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    }
//                }
//                developers {
//                    developer {
//                        id.set("DaylightNebula")
//                        name.set("Noah Shaw")
//                        email.set("noah.w.shaw@gmail.com")
//                    }
//                }
//            }
//        }
//    }
//    repositories {
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/DaylightNebula/KevinEngine")
//            credentials {
//                username = project.findProperty("github.username") as? String ?: System.getenv("GITHUB_ACTOR")
//                password = project.findProperty("github.token") as? String ?: System.getenv("GITHUB_TOKEN")
//            }
//        }
//    }
//}
