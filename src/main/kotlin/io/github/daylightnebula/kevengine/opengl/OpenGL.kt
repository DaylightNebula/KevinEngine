package io.github.daylightnebula.kevengine.opengl

import org.joml.Vector4f
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*
import kotlin.properties.Delegates

val quad_position_buffer = genVAO(-1f, -1f, 1f, -1f, 1f, 1f, -1f, 1f)

// function to quickly setup basics for opengl
fun setupOpenGL(clearColor: Vector4f = Vector4f(1f, 1f, 1f, 1f)) {
    GL.createCapabilities()
    glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)
}

// function to run internal callback in-between essential opengl rendering functions
fun drawing(shader: ShaderProgram? = null, internal: () -> Unit) {
    if (shader != null && !shader.isInitialized) shader.generate()

    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    shader?.let { glUseProgram(shader.get()) }
    internal()

    glFlush()
}

// drawing helper functions
fun drawVAO(vao: Int) {
    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, vao)
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
    glDrawArrays(GL_QUADS, 0, 4)
    glDisableVertexAttribArray(0)
}
