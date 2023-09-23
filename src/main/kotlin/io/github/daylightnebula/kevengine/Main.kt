package io.github.daylightnebula.kevengine

import io.github.daylightnebula.kevengine.glfw.GLFWApp
import io.github.daylightnebula.kevengine.glfw.glfwApp
import io.github.daylightnebula.kevengine.opengl.*
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*

fun main() = glfwApp("KevEngine", object: GLFWApp() {
    val baseShader = ShaderProgram(
        "base",
        "/vert.glsl",
        "/frag.glsl",
        listOf("matrix")
    )

    override fun start() {
        setupOpenGL(clearColor = Vector4f().zero())
        println("Start complete!")
    }

    override fun update(delta: Float) = drawing(shader = baseShader) {
        baseShader.setUniformMat4("matrix", Matrix4f().scale(0.5f))
        drawVAO(quad_position_buffer.get())
    }

    override fun stop() {}
}, decorated = true, maximized = false)
