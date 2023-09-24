package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.app.AppInfo
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*

actual fun setupRenderer(info: AppInfo) {
    GL.createCapabilities()
    glClearColor(info.clearColor.x, info.clearColor.y, info.clearColor.z, info.clearColor.w)
}

var currentShader: ShaderProgram? = null
actual fun setShader(shader: ShaderProgram?) { currentShader = shader }

actual fun drawing(internal: () -> Unit) {
    // get an immutable reference to current shader, and make sure it is loaded
    val shader = currentShader
    if (shader != null && !shader.isInitialized) shader.load()

    // start render
    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    // do render
    if (shader != null) glUseProgram(shader.get())
    internal()

    // end render
    glFlush()
}

actual fun drawVBO(vbo: VBO) {
    // make sure vbo is initialized
    if (!vbo.isInitialized) vbo.load()

    // render vbo
    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, vbo.get())
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
    glDrawArrays(GL_QUADS, 0, 4)
    glDisableVertexAttribArray(0)
}