package io.github.daylightnebula.kevinengine

import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.callKeyListeners
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

var lastTimeStamp: Double = 0.0

actual fun window(info: AppInfo) = module(
    startSystems = listOf(system {
        document.addEventListener("keyup", { it ->
            val event = it as? KeyboardEvent ?: throw IllegalArgumentException("Keydown event was not a keyboard event!")
            val key = Key.entries[convertStringCodeToKeyCode(event.code)]
            callKeyListeners(key, KeyEvent.Released)
        })
        document.addEventListener("keydown", { it ->
            val event = it as? KeyboardEvent ?: throw IllegalArgumentException("Keydown event was not a keyboard event!")
            val key = Key.entries[convertStringCodeToKeyCode(event.code)]
            callKeyListeners(key, KeyEvent.Pressed)
        })
    })
)

actual fun app(start: () -> Unit, loop: (delta: Float) -> Unit, stop: () -> Unit) {
    start()
    window.requestAnimationFrame { loop(loop, stop, it) }
}

fun loop(callback: (delta: Float) -> Unit, stop: () -> Unit, timeStamp: Double) {
    callback((timeStamp - lastTimeStamp).toFloat())
    lastTimeStamp = timeStamp
    if (keepRunning) window.requestAnimationFrame { loop(callback, stop, it) }
    else stop()
}