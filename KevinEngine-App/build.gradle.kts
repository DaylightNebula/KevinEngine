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
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.2"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-windows")
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-windows")
    implementation("org.joml:joml:1.10.5")
}