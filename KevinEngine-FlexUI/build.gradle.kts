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
    commonMainImplementation(project(":KevinEngine-App"))
    commonMainImplementation(project(":KevinEngine-Renderer"))
}

kotlin {
    js {
        browser {}
        binaries.executable()
    }
    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(platform("org.lwjgl:lwjgl-bom:3.3.2"))
                implementation("org.lwjgl:lwjgl:3.3.2")
                implementation("org.lwjgl:lwjgl-glfw:3.3.2")
                implementation("org.lwjgl:lwjgl-opengl:3.3.2")
                implementation("org.lwjgl:lwjgl-openal:3.3.2")
                implementation("org.lwjgl:lwjgl-stb:3.3.2")
                runtimeOnly("org.lwjgl:lwjgl:3.3.2:natives-windows")
                runtimeOnly("org.lwjgl:lwjgl-glfw:3.3.2:natives-windows")
                runtimeOnly("org.lwjgl:lwjgl-opengl:3.3.2:natives-windows")
                runtimeOnly("org.lwjgl:lwjgl-openal:3.3.2:natives-windows")
                runtimeOnly("org.lwjgl:lwjgl-stb:3.3.2:natives-windows")
                implementation("org.joml:joml:1.10.5")
            }
        }
    }
}