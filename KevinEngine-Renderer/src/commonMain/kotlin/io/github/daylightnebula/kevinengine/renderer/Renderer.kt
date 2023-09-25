package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.app.AppInfo

expect fun setupRenderer(info: AppInfo)
expect fun setShader(shader: ShaderProgram?)
expect fun drawing(internal: () -> Unit)

enum class RenderShapeType { TRIANGLES, QUADS }

expect fun attachBuffer(name: String, index: Int, buffer: Buffer)
expect fun drawAttachedRaw(count: Int, type: RenderShapeType)
expect fun detachBufferIndex(index: Int)