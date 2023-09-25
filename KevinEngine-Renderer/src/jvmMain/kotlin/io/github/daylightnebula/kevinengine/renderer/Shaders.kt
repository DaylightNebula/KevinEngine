package io.github.daylightnebula.kevinengine.renderer

import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import io.github.daylightnebula.kevinengine.renderer.ShaderType
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL20.*
import java.lang.IllegalArgumentException
import kotlin.system.exitProcess

actual class Shader actual constructor(actual val path: String, actual val type: ShaderType) {
    private var id = -1

    actual val isInitialized: Boolean
        get() = id != -1

    // get above id, however, if id is -1 (uninitialized), call generate
    actual fun get(): Int {
        if (id == -1) load()
        return id
    }

    // function generates shader from resource path above
    actual fun load() {
        // create new shader
        id = glCreateShader(when (type) {
            ShaderType.VERTEX -> GL_VERTEX_SHADER
            ShaderType.FRAGMENT -> GL_FRAGMENT_SHADER
        })

        // load shader
        val code = Shader::class.java.getResource(path)?.readText()
            ?: throw IllegalStateException("No shader resource could be loaded from path: $path")
        glShaderSource(id, code)
        glCompileShader(id)

        // check shader
        val statusBuf = IntArray(1)
        val logLengthBuf = IntArray(1)
        glGetShaderiv(id, GL_COMPILE_STATUS, statusBuf)
        glGetShaderiv(id, GL_INFO_LOG_LENGTH, logLengthBuf)
        println("Shader $path status: ${statusBuf[0]}")
        if (logLengthBuf[0] > 0) {
            val log = glGetShaderInfoLog(id)
            System.err.println("Shader failed to load from path: $path")
            System.err.println(log)
            exitProcess(-1)
        }
    }
}

actual class ShaderProgram actual constructor(
    actual val name: String,
    vertexPath: String,
    fragmentPath: String,
    actual val uniformsList: List<String>
) {
    // id of this shader program
    private var id = -1

    // individual shaders
    private val vertexShader = Shader(vertexPath, ShaderType.VERTEX)
    private val fragmentShader = Shader(fragmentPath, ShaderType.FRAGMENT)

    // uniforms
    private val uniforms = hashMapOf<String, Int>()

    actual val isInitialized: Boolean
        get() = id != -1

    // function that returns the above id, if the id is -1 (uninitialized), the generate function is called first
    actual fun get(): Int {
        if (id == -1) load()
        return id
    }

    // function to generate this shader program
    actual fun load() {
        println("Generating shader program!")

        // get shader ids
        val vertID = vertexShader.get()
        val fragID = fragmentShader.get()

        // create program
        id = glCreateProgram()
        glAttachShader(id, vertID)
        glAttachShader(id, fragID)
        glLinkProgram(id)

        // check program
        val statusBuf = IntArray(1)
        val logLengthBuf = IntArray(1)
        glGetProgramiv(id, GL_LINK_STATUS, statusBuf)
        glGetProgramiv(id, GL_INFO_LOG_LENGTH, logLengthBuf)
        println("Shader program status ${statusBuf[0]}")
        if (logLengthBuf[0] > 0) {
            val log = glGetProgramInfoLog(id)
            System.err.println("Shader program $name failed to load ${logLengthBuf[0]}")
            System.err.println(log)
            System.err.println("OpenGL version ${glGetString(GL_VERSION)}")
            exitProcess(-1)
        }

        // detach and destroy individual shaders
        glDetachShader(id, vertID)
        glDetachShader(id, fragID)
        glDeleteShader(vertID)
        glDeleteShader(fragID)

        // load uniforms
        uniformsList.forEach { uniform ->
            uniforms[uniform] = glGetUniformLocation(id, uniform)
        }
    }

    // functions for uniforms
    actual fun getUniforms(): Map<String, Int> {
        if (!isInitialized) load()
        return uniforms
    }
    actual fun getUniform(name: String): Int = getUniforms()[name]
        ?: throw IllegalArgumentException("No uniform with name $name registered!")
    actual fun setUniformFloat(name: String, value: Float) =
        glUniform1f(getUniform(name), value)
    actual fun setUniformVec2(name: String, value: Float2) =
        glUniform2f(getUniform(name), value.x, value.y)
    actual fun setUniformVec3(name: String, value: Float3) =
        glUniform3f(getUniform(name), value.x, value.y, value.z)
    actual fun setUniformVec4(name: String, value: Float4) =
        glUniform4f(getUniform(name), value.x, value.y, value.z, value.w)
    actual fun setUniformMat4(name: String, value: Mat4) =
        glUniformMatrix4fv(getUniform(name), false, value.toFloatArray())
}