package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.app.AppInfo

expect fun setupRenderer(info: AppInfo)
expect fun setShader(shader: ShaderProgram?)
expect fun drawing(internal: () -> Unit)

enum class RenderShapeType { TRIANGLES, QUADS }

expect fun attachBuffer(index: Int, buffer: Buffer)
expect fun drawBufferRaw(buffer: Buffer, type: RenderShapeType)
expect fun detachBufferIndex(index: Int)