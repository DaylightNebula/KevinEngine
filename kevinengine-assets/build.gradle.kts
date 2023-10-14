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
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
//                implementation(platform("org.lwjgl:lwjgl-bom:3.3.2"))
//                implementation("org.lwjgl:lwjgl:3.3.2")
                implementation("org.lwjgl:lwjgl-assimp:3.3.2")
                runtimeOnly("org.lwjgl:lwjgl-assimp:3.3.2:natives-windows")
            }
        }
        val jsMain by getting
    }
}