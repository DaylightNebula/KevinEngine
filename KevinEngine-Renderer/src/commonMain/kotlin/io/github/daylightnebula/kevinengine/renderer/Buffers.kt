package io.github.daylightnebula.kevinengine.renderer

expect class Buffer(inputs: FloatArray) {
    val length: Int
    val inputs: FloatArray
    val isInitialized: Boolean
    fun load()
    fun get(): Int
}
expect fun genBuffer(vararg floats: Float): Buffer