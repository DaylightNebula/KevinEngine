package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.AppInfo
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system

fun renderer(info: AppInfo) = module(
    startSystems = listOf(system { setupRenderer(info) }),
    updateSystems = arrayOf(system{ endRender(); startRender() })
)

expect fun setupRenderer(info: AppInfo)
expect fun startRender()
expect fun endRender()

enum class RenderShapeType { TRIANGLES, QUADS }

expect fun attachBuffer(index: Int, metadata: BufferMetadata, buffer: Buffer)
expect fun drawAttachedRaw(shader: ShaderProgram, count: Int, type: RenderShapeType)
expect fun detachBufferIndex(index: Int)