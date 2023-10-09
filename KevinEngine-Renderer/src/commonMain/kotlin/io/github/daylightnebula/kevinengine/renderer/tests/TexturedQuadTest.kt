package io.github.daylightnebula.kevinengine.renderer.tests

import io.github.daylightnebula.kevinengine.AppInfo
import io.github.daylightnebula.kevinengine.app
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.math.scale
import io.github.daylightnebula.kevinengine.renderer.*
import io.github.daylightnebula.kevinengine.stopApp
import io.github.daylightnebula.kevinengine.window

class TexturedQuadTest {
    val scaleMatrix = scale(0.5f)
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

    fun main() = io.github.daylightnebula.kevinengine.run(
        window(info),
        renderer(info),
        module(
            system {
                shader.setUniformMat4("matrix", scaleMatrix)
                shader.setUniformTex("tex0", texture)
                buffers.render()
            },
            startSystems = listOf(system {
                addKeyListener("esc_close") { key, event ->
                    if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
                }
            })
        )
    )
}