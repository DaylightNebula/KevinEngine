package io.github.daylightnebula.kevinengine

import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.mouse.addMouseListener

val info = AppInfo("KevinEngine")

//fun main() = app(info, object: App {
//    override fun start() {
//        addKeyListener("TEST") { key, event ->
//            println("Key $key Event $event")
//            if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
//        }
//        addMouseListener("TEST") { event ->
//            println("Mouse event $event")
//        }
//        println("Started!")
//    }
//    override fun update(delta: Float) {}
//    override fun stop() { println("Stopped!") }
//})

fun main() = run(window(info))