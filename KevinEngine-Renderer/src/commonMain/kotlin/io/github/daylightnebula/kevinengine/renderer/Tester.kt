package io.github.daylightnebula.kevinengine.renderer

import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import io.github.daylightnebula.kevinengine.app.*

val quad_position_buffer = genVBO(-1f, -1f, 0f, 1f, -1f, 0f, 1f, 1f, 0f, -1f, 1f, 0f)
val scaleMatrix = Mat4.identity().scale(0.5f)
val shader = ShaderProgram(
    "base",
    "/vert.glsl",
    "/frag.glsl",
    listOf("matrix")
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
        shader.setUniformMat4("matrix", scaleMatrix)
        drawVBO(quad_position_buffer)
    }

    override fun stop() {}
})