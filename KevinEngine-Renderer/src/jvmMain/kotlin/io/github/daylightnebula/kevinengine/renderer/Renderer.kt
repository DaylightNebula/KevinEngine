package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.app.AppInfo
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*

actual fun setupRenderer(info: AppInfo) {
    GL.createCapabilities()
    glClearColor(info.clearColor.x, info.clearColor.y, info.clearColor.z, info.clearColor.w)
}

actual fun drawing(internal: () -> Unit) {
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    // start render
    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    // do render
    internal()

    // end render
    glFlush()
}

// attaches the given buffer to be rendered
actual fun attachBuffer(index: Int, metadata: BufferMetadata, buffer: Buffer) {
    // make sure vbo is initialized
    if (!buffer.isInitialized) buffer.load()

    // render vbo
    glEnableVertexAttribArray(index)
    glBindBuffer(GL_ARRAY_BUFFER, buffer.get())
    glVertexAttribPointer(metadata.layoutIndex, metadata.infoCount, GL_FLOAT, false, 0, 0)
}

// draws the given buffer to the screen if it has been attached
actual fun drawAttachedRaw(shader: ShaderProgram, count: Int, type: RenderShapeType) {
    // if shader not initialized, load, otherwise, enable and draw
    if (!shader.isInitialized) shader.load()
    else {
        // enable shader
        glUseProgram(shader.get())

        // draw the array
        glDrawArrays(
            when (type) {
                RenderShapeType.QUADS -> GL_QUADS
                RenderShapeType.TRIANGLES -> GL_TRIANGLES
            }, 0, count
        )
    }
}

// detaches a given buffer index from the renderer
actual fun detachBufferIndex(index: Int) = glDisableVertexAttribArray(index)