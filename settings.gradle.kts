pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("multiplatform") version "1.9.10"
    }
}

rootProject.name = "KevinEngine"
include(":KevinEngine-Math")
include(":KevinEngine-App")
include(":KevinEngine-Renderer")
include(":KevinEngine-FlexUI")
