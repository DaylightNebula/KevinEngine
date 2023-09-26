package io.github.daylightnebula.kevinengine.renderer

actual class Texture actual constructor (path: String) {
    var id = -1
    actual val width: Int = 0
    actual val height: Int = 0
    actual val isInitialized: Boolean
        get() = id != -1

    actual fun load() { TODO("Texture load") }
    actual fun get(): Int { TODO("Texture get") }
}