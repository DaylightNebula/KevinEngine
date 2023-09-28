package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.app.AppInfo

expect fun setupRenderer(info: AppInfo)
expect fun drawing(internal: () -> Unit)

enum class RenderShapeType { TRIANGLES, QUADS }

expect fun attachBuffer(index: Int, metadata: BufferMetadata, buffer: Buffer)
expect fun drawAttachedRaw(shader: ShaderProgram, count: Int, type: RenderShapeType)
expect fun detachBufferIndex(index: Int)