package io.github.daylightnebula.kevengine

import io.github.daylightnebula.kevengine.glfw.GLFWApp
import io.github.daylightnebula.kevengine.glfw.glfwApp
import io.github.daylightnebula.kevengine.glfw.keyboard.addKeyListener
import io.github.daylightnebula.kevengine.glfw.keyboard.removeKeyListener
import io.github.daylightnebula.kevengine.glfw.mouse.addMouseListener

fun main() = glfwApp("KevEngine", object: GLFWApp() {
    override fun start() {
        addKeyListener("TEST") { key, event ->
            println("Key $key Event $event")
        }
        addMouseListener("TEST") {
            println("Mouse $it")
        }
    }
    override fun update(delta: Float) {}
    override fun stop() {}
})
