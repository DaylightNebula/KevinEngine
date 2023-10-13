pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("multiplatform") version "1.9.10"
        kotlin("plugin.serialization") version "1.9.10"
    }
}

rootProject.name = "kevinengine"
include(":kevinengine-app")
include(":kevinengine-assets")
include(":kevinengine-ui")
include(":kevinengine-math")
include(":kevinengine-renderer")