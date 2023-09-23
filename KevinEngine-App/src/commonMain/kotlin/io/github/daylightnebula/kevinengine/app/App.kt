package io.github.daylightnebula.kevinengine.app

expect interface App {
    fun start()
    fun update(delta: Float)
    fun stop()
}

expect fun app(info: AppInfo, app: App)
expect fun stopApp()