package io.github.daylightnebula.kevinengine.renderer

enum class ShaderType { VERTEX, FRAGMENT }

expect class Shader(path: String, type: ShaderType) {
    val path: String
    val type: ShaderType
    val isInitialized: Boolean
    fun load()
    fun get(): Int
}

expect class ShaderProgram(name: String, vertexPath: String, fragmentPath: String, uniformsList: List<String>) {
    val name: String
    val uniformsList: List<String>
    val isInitialized: Boolean
    fun load()
    fun get(): Int
}