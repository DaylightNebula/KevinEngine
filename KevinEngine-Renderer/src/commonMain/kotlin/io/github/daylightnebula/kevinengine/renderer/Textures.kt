package io.github.daylightnebula.kevinengine.renderer

expect fun provideFileBytes(path: String): ByteArray // todo remove and replace with load png/jpg

expect class Texture(path: String) {
    val width: Int
    val height: Int
    val isInitialized: Boolean
    fun load()
    fun get(): Int
}