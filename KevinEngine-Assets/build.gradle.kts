plugins {
    kotlin("multiplatform") version "1.9.10"
}

group = "io.github.daylightnebula"
version = "0.0.0"

repositories {
    mavenCentral()
}

dependencies {
    commonMainImplementation(project(":KevinEngine-App"))
    commonMainImplementation(project(":KevinEngine-Math"))
    commonMainImplementation(project(":KevinEngine-Renderer"))
}

kotlin {
    js {
        browser {}
        binaries.executable()
    }
    jvm()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}