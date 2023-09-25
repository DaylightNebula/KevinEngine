package io.github.daylightnebula.kevinengine.renderer.tests

import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import io.github.daylightnebula.kevinengine.app.App
import io.github.daylightnebula.kevinengine.app.AppInfo
import io.github.daylightnebula.kevinengine.app.app
import io.github.daylightnebula.kevinengine.app.scale
import io.github.daylightnebula.kevinengine.renderer.*

class QuadTest {
    val buffers = bufferCollection(
        RenderShapeType.QUADS,
        metadata("positions", 0, 3) to genBuffer(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
        ),
        metadata("colors", 1, 3) to genBuffer(
            0.393f,  0.621f,  0.362f,
            0.673f,  0.211f,  0.457f,
            0.820f,  0.883f,  0.371f,
            0.982f,  0.099f,  0.879f
        )
    )
    val scaleMatrix = Mat4.identity().scale(0.5f)
    val shader = ShaderProgram(
        "base",
        "/quad_vert.glsl",
        "/quad_frag.glsl",
        listOf("matrix")
    )

    val info = AppInfo(
        "KevinEngine-RendererTester",
        Float4(0f, 0f, 0f, 1f)
    )

    fun main() = app(info, object : App {
        override fun start() {
            setupRenderer(info)
            setShader(shader)
        }

        override fun update(delta: Float) = drawing {
            shader.setUniformMat4("matrix", scaleMatrix)
            buffers.render()
        }

        override fun stop() {}
    })
}