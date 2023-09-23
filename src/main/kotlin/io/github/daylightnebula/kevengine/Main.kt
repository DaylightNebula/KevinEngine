package io.github.daylightnebula.kevengine

import io.github.daylightnebula.kevengine.glfw.GLFWApp
import io.github.daylightnebula.kevengine.glfw.glfwApp
import io.github.daylightnebula.kevengine.opengl.*
import org.joml.Vector4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.glUseProgram

fun main() = glfwApp("KevEngine", object: GLFWApp() {
    val baseShader = ShaderProgram(
        "base",
        "/vert.glsl",
        "/frag.glsl"
    )

    override fun start() {
        setupOpenGL(clearColor = Vector4f().zero())
        println("Start complete!")
    }

    override fun update(delta: Float) = drawing(shader = baseShader) {
//        glBegin(GL_QUADS)
//        glColor3f(1f, 0f, 0f)
//        glVertex2f(-0.5f, -0.5f)
//        glVertex2f( 0.5f, -0.5f)
//        glVertex2f( 0.5f,  0.5f)
//        glVertex2f(-0.5f,  0.5f)
//        glEnd()
//        glUseProgram(baseShader.get())
        drawVAO(quad_position_buffer.get())
    }

    override fun stop() {}
}, decorated = true, maximized = false)
