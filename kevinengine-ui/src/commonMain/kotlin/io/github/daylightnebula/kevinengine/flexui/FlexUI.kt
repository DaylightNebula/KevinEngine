package io.github.daylightnebula.kevinengine.flexui

import io.github.daylightnebula.kevinengine.AppInfo
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.isKeyPressed
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.renderer.renderer
import io.github.daylightnebula.kevinengine.run
import io.github.daylightnebula.kevinengine.stopApp
import io.github.daylightnebula.kevinengine.window

lateinit var windowDimensions: FlexboxDimensions

fun renderFlexbox(box: Flexbox, info: AppInfo) {
    windowDimensions = FlexboxDimensions(0, 0, info.width, info.height)
    box.render(windowDimensions)
}

val root = Flexbox(
    width = PercentVal(0.25f),
    height = PercentVal(0.25f),
    horizontalAlignment = Alignment.CENTER,
    verticalAlignment = Alignment.CENTER,
    childrenDirection = FlexDirection.COLUMN,
    children = mutableListOf(
        Flexbox(
            width = PxVal(60),
            height = PxVal(60),
            border = Box.all(PxVal(3)),
            borderRadius = Box.all(PxVal(20)),
            backgroundColor = Float4(1f, 0f, 0f, 1f)
        ),
        Flexbox(
            width = PxVal(60),
            height = PxVal(60),
            border = Box.all(PxVal(3)),
            borderRadius = Box.all(PxVal(20)),
            backgroundColor = Float4(0f, 0f, 1f, 1f)
        )
    )
)
val info = AppInfo("FlexUI - Test", Float4(0f, 0f, 0f, 1f))
//fun main() = app(info, object: App {
//    override fun start() {
//        setupRenderer(info)
//
//        addKeyListener("esc_close") { key, event ->
//            if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
//        }
//    }
//    override fun update(delta: Float) = drawing { renderFlexbox(root, info) }
//    override fun stop() {}
//})
fun main() = run(
    window(info),
    renderer(info),
    module(system {
        if (isKeyPressed(Key.KEY_ESCAPE)) stopApp()
        renderFlexbox(root, info)
    })
)