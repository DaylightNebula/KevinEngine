package io.github.daylightnebula.kevinengine.renderer

import dev.romainguy.kotlin.math.Float4
import io.github.daylightnebula.kevinengine.app.App
import io.github.daylightnebula.kevinengine.app.AppInfo
import io.github.daylightnebula.kevinengine.app.NativeInfo
import io.github.daylightnebula.kevinengine.app.app

val quad_position_buffer = genVBO(*floatArrayOf(-1f, -1f, 1f, -1f, 1f, 1f, -1f, 1f).map { it * 0.5f }.toFloatArray())
val shader = ShaderProgram(
    "base",
    "/vert.glsl",
    "/frag.glsl",
    listOf()
)

val info = AppInfo(
    "KevinEngine-RendererTester",
    Float4(0f, 0f, 0f, 1f)
)

fun main() = app(info, object: App {
    override fun start() {
        setupRenderer(info)
        setShader(shader)
    }
    override fun update(delta: Float) = drawing {
        drawVBO(quad_position_buffer)
    }

    override fun stop() {}
})