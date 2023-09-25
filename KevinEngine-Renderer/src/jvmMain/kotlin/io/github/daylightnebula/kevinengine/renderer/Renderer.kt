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

actual fun attachBuffer(index: Int, buffer: Buffer) {
    // make sure vbo is initialized
    if (!buffer.isInitialized) buffer.load()

    // render vbo
    glEnableVertexAttribArray(index)
    glBindBuffer(GL_ARRAY_BUFFER, buffer.get())
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
}
actual fun drawBufferRaw(buffer: Buffer, type: RenderShapeType) = glDrawArrays(
    when(type) {
        RenderShapeType.QUADS -> GL_QUADS
        RenderShapeType.TRIANGLES -> GL_TRIANGLES
    }, 0, buffer.length
)
actual fun detachBufferIndex(index: Int) = glDisableVertexAttribArray(index)