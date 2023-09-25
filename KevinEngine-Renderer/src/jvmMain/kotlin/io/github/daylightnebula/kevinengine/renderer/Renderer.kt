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

// attaches the given buffer to be rendered
actual fun attachBuffer(name: String, index: Int, buffer: Buffer) {
    // make sure vbo is initialized
    if (!buffer.isInitialized) buffer.load()

    println("Start $name $index ${buffer.get()}")
    // render vbo
    glEnableVertexAttribArray(index)
    glBindBuffer(GL_ARRAY_BUFFER, buffer.get())
    glVertexAttribPointer(index, 3, GL_FLOAT, false, 0, 0)
    println("Stop $name $index")
}

// draws the given buffer to the screen if it has been attached
actual fun drawAttachedRaw(count: Int, type: RenderShapeType) = glDrawArrays(
    when(type) {
        RenderShapeType.QUADS -> GL_QUADS
        RenderShapeType.TRIANGLES -> GL_TRIANGLES
    }, 0, count
)

// detaches a given buffer index from the renderer
actual fun detachBufferIndex(index: Int) = glDisableVertexAttribArray(index)