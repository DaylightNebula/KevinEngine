package io.github.daylightnebula.kevinengine.renderer.tests

import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import io.github.daylightnebula.kevinengine.app.*
import io.github.daylightnebula.kevinengine.app.keyboard.Key
import io.github.daylightnebula.kevinengine.app.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.app.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.renderer.*

class TexturedQuadTest {
    val scaleMatrix = Mat4.identity().scale(0.5f)
    val shader = ShaderProgram(
        "base",
        "/texquad_vert.glsl",
        "/texquad_frag.glsl",
        listOf("matrix", "tex0")
    )
    val texture = Texture("/flowers.jpg")
    val buffers = bufferCollection(
        shader,
        RenderShapeType.QUADS,
        metadata("positions", 0, 3) to genBuffer(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
        ),
        metadata("colors", 1, 2) to genBuffer(
            0f, 1f,
            1f, 1f,
            1f, 0f,
            0f, 0f
        )
    )

    val info = AppInfo(
        "KevinEngine-RendererTesterTextured",
        Float4(0f, 0f, 0f, 1f)
    )

    fun main() = app(info, object: App {
        override fun start() {
            setupRenderer(info)

            addKeyListener("esc_close") { key, event ->
                if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
            }
        }

        override fun update(delta: Float) = drawing {
            shader.setUniformMat4("matrix", scaleMatrix)
            shader.setUniformTex("tex0", texture)
            buffers.render()
        }

        override fun stop() {}
    })
}