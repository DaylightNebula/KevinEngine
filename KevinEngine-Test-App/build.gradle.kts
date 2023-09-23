plugins {
    kotlin("jvm") version "1.8.21"
}

group = "io.github.daylightnebula"
version = "0.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":KevinEngine-App"))
    implementation(project(":KevinEngine-Renderer-OpenGL"))
    implementation("org.joml:joml:1.10.5")
}