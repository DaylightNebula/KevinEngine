package io.github.daylightnebula.kevinengine.app

import kotlinx.browser.window

actual interface App {
    actual fun start()
    actual fun update(delta: Float)
    actual fun stop()
}

var shouldStop = false

actual fun stopApp() {}
actual fun app(info: AppInfo, app: App) {
    app.start()
    window.requestAnimationFrame { loop(app, info, it) }
}

fun loop(app: App, info: AppInfo, delta: Double) {
    app.update(delta.toFloat())
    if (!shouldStop) window.requestAnimationFrame { loop(app, info, it) }
    else stop(app, info)
}

fun stop(app: App, info: AppInfo) {
    app.stop()
}