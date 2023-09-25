package io.github.daylightnebula.kevinengine.renderer

import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4

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

    // uniform stuff
    fun getUniforms(): Map<String, Int>
    fun getUniform(name: String): Int
    fun setUniformFloat(name: String, value: Float)
    fun setUniformVec2(name: String, value: Float2)
    fun setUniformVec3(name: String, value: Float3)
    fun setUniformVec4(name: String, value: Float4)
    fun setUniformMat4(name: String, value: Mat4)
    fun setUniformTex(name: String, value: Texture)
}