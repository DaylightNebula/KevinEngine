package io.github.daylightnebula.kevinengine.renderer.tests

import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.app.App
import io.github.daylightnebula.kevinengine.app.AppInfo
import io.github.daylightnebula.kevinengine.app.app
import io.github.daylightnebula.kevinengine.app.keyboard.Key
import io.github.daylightnebula.kevinengine.app.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.app.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.app.mouse.addMouseListener
import io.github.daylightnebula.kevinengine.app.stopApp
import io.github.daylightnebula.kevinengine.renderer.drawing
import io.github.daylightnebula.kevinengine.renderer.setupRenderer

class EmptyTest {
    private val info = AppInfo("Blank Test", Float4(0f, 0f, 0f, 1f))
    fun main() = app(info, object: App {
        override fun start() {
            addKeyListener("esc_close") { key, event ->
                if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
            }

            setupRenderer(info)
        }
        override fun update(delta: Float) = drawing {}
        override fun stop() {}
    })
}