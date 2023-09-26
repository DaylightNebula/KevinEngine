package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.app.AppInfo
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL

val canvas: HTMLCanvasElement = document.getElementById("glcanvas") as HTMLCanvasElement
val gl: GL = canvas.getContext("webgl") as GL
var currentShader: ShaderProgram? = null

actual fun setupRenderer(info: AppInfo) {
    gl.clearColor(info.clearColor.x, info.clearColor.y, info.clearColor.z, info.clearColor.w)
}

actual fun setShader(shader: ShaderProgram?) {
    currentShader = shader
}

actual fun drawing(internal: () -> Unit) {
    // get an immutable reference to current shader, and make sure it is loaded
    val shader = currentShader
    if (shader != null && !shader.isInitialized) shader.load()

    // start render
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

    // do render
    if (shader != null && shader.isInitialized) gl.useProgram(shaderPrograms[shader.get()])
    internal()

    // end render
    gl.flush()
}

actual fun attachBuffer(index: Int, metadata: BufferMetadata, buffer: Buffer) {
    // make sure buffer is initialize
    if (!buffer.isInitialized) buffer.load()

    // render buffer
    gl.enableVertexAttribArray(index)
    gl.bindBuffer(GL.ARRAY_BUFFER, bufferRefs[buffer.get()])
    gl.vertexAttribPointer(metadata.layoutIndex, metadata.infoCount, GL.FLOAT, false, 0, 0)
}

actual fun drawAttachedRaw(count: Int, type: RenderShapeType) = gl.drawArrays(
    when(type) {
        RenderShapeType.QUADS -> GL.TRIANGLE_FAN
        RenderShapeType.TRIANGLES -> GL.TRIANGLES
    }, 0, count / 3
)

actual fun detachBufferIndex(index: Int) = gl.disableVertexAttribArray(index)
