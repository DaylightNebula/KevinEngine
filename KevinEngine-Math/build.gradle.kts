plugins {
    kotlin("multiplatform") version "1.9.10"
}

group = "io.github.daylightnebula"
version = "0.0.0"

repositories {
    mavenCentral()
}

kotlin {
    js {
        browser {}
        binaries.executable()
    }
    jvm()
}