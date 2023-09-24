package io.github.daylightnebula.kevinengine.renderer

expect class VBO(inputs: FloatArray) {
    val inputs: FloatArray
    val isInitialized: Boolean
    fun load()
    fun get(): Int
}
expect fun genVBO(vararg floats: Float): VBO