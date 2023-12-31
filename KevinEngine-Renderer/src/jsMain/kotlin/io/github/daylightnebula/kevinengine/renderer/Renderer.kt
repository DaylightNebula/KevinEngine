package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.AppInfo
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL

val canvas: HTMLCanvasElement = document.getElementById("glcanvas") as HTMLCanvasElement
val gl: GL = canvas.getContext("webgl") as GL

actual fun setupRenderer(info: AppInfo) {
    gl.clearColor(info.clearColor.x, info.clearColor.y, info.clearColor.z, info.clearColor.w)
}

actual fun startRender() = gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)
actual fun endRender() = gl.flush()
actual fun drawArrays(type: RenderShapeType, count: Int) = gl.drawArrays(
            when (type) {
                RenderShapeType.QUADS -> GL.TRIANGLE_FAN
                RenderShapeType.TRIANGLES -> GL.TRIANGLES
            }, 0, count / 3
        )

actual fun drawIndexed(type: RenderShapeType, count: Int) = gl.drawElements(
    when (type) {
        RenderShapeType.QUADS -> GL.TRIANGLE_FAN
        RenderShapeType.TRIANGLES -> GL.TRIANGLES
    }, count / 3, GL.SHORT, 0
)

//actual fun attachBuffer(index: Int, metadata: BufferMetadataID, buffer: Buffer) {
//    // make sure buffer is initialize
//    if (!buffer.isInitialized) buffer.load()
//
//    // render buffer
//    gl.enableVertexAttribArray(index)
//    gl.bindBuffer(GL.ARRAY_BUFFER, bufferRefs[buffer.get()])
//    gl.vertexAttribPointer(metadata.layoutIndex, metadata.infoCount, GL.FLOAT, false, 0, 0)
//}
//
//actual fun drawAttachedRaw(shader: ShaderProgram, count: Int, type: RenderShapeType) {
//    // if shader not initialized, load, otherwise, enable and draw
//    if (!shader.isInitialized) shader.load()
//    else {
//        // enable shader
//        gl.useProgram(shaderPrograms[shader.get()])
//
//        // draw arrays
//        gl.drawArrays(
//            when (type) {
//                RenderShapeType.QUADS -> GL.TRIANGLE_FAN
//                RenderShapeType.TRIANGLES -> GL.TRIANGLES
//            }, 0, count / 3
//        )
//    }
//}
//
//actual fun detachBufferIndex(index: Int) = gl.disableVertexAttribArray(index)
