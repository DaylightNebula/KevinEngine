plugins {
    kotlin("multiplatform") version "1.9.10"
}

group = "io.github.daylightnebula"
version = "0.0.0"

repositories {
    mavenCentral()
}

dependencies {
    commonMainImplementation(project(":KevinEngine-Math"))
}

kotlin {
    js {
        browser {}
        binaries.executable()
    }
    jvm()

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}