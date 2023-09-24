package io.github.daylightnebula.kevinengine.app

import io.github.daylightnebula.kevinengine.app.keyboard.Key
import io.github.daylightnebula.kevinengine.app.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.app.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.app.mouse.addMouseListener

val info = AppInfo(
    "KevinEngine",
    NativeInfo()
)

fun main() = app(info, object: App {
    override fun start() {
        addKeyListener("TEST") { key, event ->
            println("Key $key Event $event")
            if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
        }
        addMouseListener("TEST") { event ->
            println("Mouse event $event")
        }
        println("Started!")
    }
    override fun update(delta: Float) {}
    override fun stop() { println("Stopped!") }
})