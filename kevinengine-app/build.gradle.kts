plugins {
    kotlin("multiplatform") version "1.9.10"
}

repositories {
    mavenCentral()
}

dependencies {
    commonMainImplementation(project(":kevinengine-math"))
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
        val jvmMain by getting {
            dependencies {
                implementation(platform("org.lwjgl:lwjgl-bom:3.3.2"))
                implementation("org.lwjgl:lwjgl:3.3.2")
                implementation("org.lwjgl:lwjgl-glfw:3.3.2")
                runtimeOnly("org.lwjgl:lwjgl:3.3.2:natives-windows")
                runtimeOnly("org.lwjgl:lwjgl-glfw:3.3.2:natives-windows")
                implementation("org.joml:joml:1.10.5")
            }
        }
    }
}