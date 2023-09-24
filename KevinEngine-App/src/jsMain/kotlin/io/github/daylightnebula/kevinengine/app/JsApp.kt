package io.github.daylightnebula.kevinengine.app

import io.github.daylightnebula.kevinengine.app.keyboard.Key
import io.github.daylightnebula.kevinengine.app.keyboard.callKeyListeners
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

actual interface App {
    actual fun start()
    actual fun update(delta: Float)
    actual fun stop()
}

var lastTimeStamp: Double = 0.0
var shouldStop = false

// when stop is called for, mark should stop
actual fun stopApp() { shouldStop = true }

// function for starting the app
actual fun app(info: AppInfo, app: App) {
    // add the key listeners
    document.addEventListener("keydown", { it ->
        val event = it as? KeyboardEvent ?: throw IllegalArgumentException("Keydown event was not a keyboard event!")
        event.key
        val keyString = when(event.key) {
            " " -> "KEY_SPACE"
            "Shift" -> "KEY_LSHIFT"
            "Alt" -> "KEY_LMENU"
            "Control" -> "KEY_LCONTROL"
            "PageUp" -> "KEY_NEXT"
            "PageDown" -> "KEY_BACK"
            "Os" -> "KEY_SYSRQ"
            "ArrowUp" -> "KEY_UP"
            "ArrowDown" -> "KEY_DOWN"
            "ArrowLeft" -> "KEY_LEFT"
            "ArrowRight" -> "KEY_RIGHT"
            "\\" -> "KEY_BACKSLASH"
            "/" -> "KEY_SLASH"
            "," -> "KEY_COMMA"
            "." -> "KEY_PERIOD"
            "`" -> "KEY_GRAVE"
            else -> "KEY_${event.key.uppercase()}"
        }
        println("Key down ${event.key} = $keyString")
        val key = Key.valueOf(keyString)
        println("Found key $key")
    })

    // start the app
    app.start()

    // call for first looping frame
    window.requestAnimationFrame { loop(app, info, it) }
}

// loop function using window animation frames
fun loop(app: App, info: AppInfo, timeStamp: Double) {
    // call app update and update time tracker
    app.update((timeStamp - lastTimeStamp).toFloat())
    lastTimeStamp = timeStamp

    // if we should not stop, request next frame, otherwise, stop
    if (!shouldStop) window.requestAnimationFrame { loop(app, info, it) }
    else stop(app, info)
}

// stop function
fun stop(app: App, info: AppInfo) {
    app.stop()
}