package io.github.daylightnebula.kevinengine.flexui

import dev.romainguy.kotlin.math.Float4
import io.github.daylightnebula.kevinengine.app.App
import io.github.daylightnebula.kevinengine.app.AppInfo
import io.github.daylightnebula.kevinengine.app.app
import io.github.daylightnebula.kevinengine.app.keyboard.Key
import io.github.daylightnebula.kevinengine.app.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.app.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.app.stopApp
import io.github.daylightnebula.kevinengine.renderer.drawing
import io.github.daylightnebula.kevinengine.renderer.setupRenderer

lateinit var windowDimensions: FlexboxDimensions

fun renderFlexbox(box: Flexbox, info: AppInfo) {
    windowDimensions = FlexboxDimensions(0, 0, info.width, info.height)
    box.render(windowDimensions)
}

val root = Flexbox(
    width = PxVal(100),
    height = PxVal(100),
    horizontalAlignment = Alignment.CENTER,
    verticalAlignment = Alignment.CENTER,
    children = mutableListOf(
        Flexbox(
            width = PercentVal(0.5f),
            height = PercentVal(0.5f),
            border = Box.all(PxVal(2)),
            horizontalAlignment = Alignment.CENTER,
            verticalAlignment = Alignment.CENTER,
            backgroundColor = Float4(1f, 0f, 0f, 1f)
        )
    )
)
val info = AppInfo("FlexUI - Test", Float4(0f, 0f, 0f, 1f))
fun main() = app(info, object: App {
    override fun start() {
        setupRenderer(info)

        addKeyListener("esc_close") { key, event ->
            if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
        }
    }
    override fun update(delta: Float) = drawing { renderFlexbox(root, info) }
    override fun stop() {}
})