package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.AppInfo
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*

actual fun setupRenderer(info: AppInfo) {
    GL.createCapabilities()
    glClearColor(info.clearColor.x, info.clearColor.y, info.clearColor.z, info.clearColor.w)

    // enabling alpha blending
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glEnable(GL_DEPTH_TEST)
    glDepthFunc(GL_LESS)
}

actual fun startRender() = glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
actual fun endRender() = glFlush()
actual fun drawArrays(type: RenderShapeType, count: Int) = glDrawArrays(
    when (type) {
        RenderShapeType.QUADS -> GL_TRIANGLE_FAN
        RenderShapeType.TRIANGLES -> GL_TRIANGLES
    }, 0, count / 3
)
actual fun drawIndexed(type: RenderShapeType, count: Int) = glDrawElements(
    when (type) {
        RenderShapeType.QUADS -> GL_TRIANGLE_FAN
        RenderShapeType.TRIANGLES -> GL_TRIANGLES
    }, count, GL_UNSIGNED_SHORT, 0
)