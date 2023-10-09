package io.github.daylightnebula.kevinengine.renderer.tests

import io.github.daylightnebula.kevinengine.*
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.math.scale
import io.github.daylightnebula.kevinengine.renderer.*
import kotlin.run

class QuadTest {
    val scaleMatrix = scale(0.5f)
    val shader = ShaderProgram(
        "base",
        "/quad_vert.glsl",
        "/quad_frag.glsl",
        listOf("matrix")
    )
    val buffers = bufferCollection(
        shader,
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

    val info = AppInfo(
        "KevinEngine-RendererTester",
        Float4(0f, 0f, 0f, 1f)
    )

    fun main() = run(
        window(info),
        renderer(info),
        module(
            updateSystems = listOf(system {
                shader.setUniformMat4("matrix", scaleMatrix)
                buffers.render()
            }),
            startSystems = listOf(system {
                addKeyListener("esc_close") { key, event ->
                    if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
                }
            })
        )
    )
//    fun main() = app(info, object : App {
//        override fun start() {
//            setupRenderer(info)
//
//            addKeyListener("esc_close") { key, event ->
//                if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
//            }
//        }
//
//        override fun update(delta: Float) = drawing {
//            shader.setUniformMat4("matrix", scaleMatrix)
//            buffers.render()
//        }
//
//        override fun stop() {}
//    })
}