package io.github.daylightnebula.kevinengine.renderer

expect class Texture(path: String) {
    val width: Int
    val height: Int
    val isInitialized: Boolean
    fun load()
    fun get(): Int
}