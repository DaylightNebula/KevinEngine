import org.lwjgl.Lwjgl
import org.lwjgl.Release
import org.lwjgl.lwjgl
import org.lwjgl.sonatype

plugins {
    kotlin("jvm") version "1.8.21"
    id("org.lwjgl.plugin") version "0.0.34"
}

group = "io.github.daylightnebula"
version = "0.0.0"

repositories {
    mavenCentral()
    sonatype()
}

dependencies {
    lwjgl {
        version = Release.`3_3_2`
        implementation(Lwjgl.Preset.minimalOpenGL)
    }
    implementation("org.joml:joml:1.10.5")
}