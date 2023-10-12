plugins {
    kotlin("multiplatform") version "1.9.10"
}

repositories {
    mavenCentral()
}

dependencies {
    commonMainImplementation(project(":kevinengine-app"))
    commonMainImplementation(project(":kevinengine-math"))
    commonMainImplementation(project(":kevinengine-renderer"))
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