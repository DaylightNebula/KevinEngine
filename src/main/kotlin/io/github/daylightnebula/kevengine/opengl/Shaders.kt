package io.github.daylightnebula.kevengine.opengl

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL20.*
import java.lang.IllegalArgumentException
import java.nio.FloatBuffer
import kotlin.system.exitProcess

class Shader(val path: String, val type: Int) {
    private var id = -1

    // get above id, however, if id is -1 (uninitialized), call generate
    fun get(): Int {
        if (id == -1) generate()
        return id
    }

    // function generates shader from resource path above
    fun generate() {
        // create new shader
        id = glCreateShader(type)

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

class ShaderProgram(
    val name: String,
    vertexShaderPath: String,
    fragmentShaderPath: String,
    private val uniformsList: List<String> = listOf()
) {
    // id of this shader program
    private var id = -1

    // individual shaders
    private val vertexShader = Shader(vertexShaderPath, GL_VERTEX_SHADER)
    private val fragmentShader = Shader(fragmentShaderPath, GL_FRAGMENT_SHADER)

    // uniforms
    private val uniforms = hashMapOf<String, Int>()

    val isInitialized: Boolean
        get() = id != -1

    // function that returns the above id, if the id is -1 (uninitialized), the generate function is called first
    fun get(): Int {
        if (id == -1) generate()
        return id
    }

    // function to generate this shader program
    fun generate() {
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
    fun getUniforms(): HashMap<String, Int> {
        if (!isInitialized) generate()
        return uniforms
    }
    fun getUniform(name: String): Int = getUniforms()[name]
        ?: throw IllegalArgumentException("No uniform with name $name registered!")
    fun setUniformFloat(name: String, value: Float) =
        glUniform1f(getUniform(name), value)
    fun setUniformVec2(name: String, value: Vector2f) =
        glUniform2f(getUniform(name), value.x, value.y)
    fun setUniformVec3(name: String, value: Vector3f) =
        glUniform3f(getUniform(name), value.x, value.y, value.z)
    fun setUniformVec4(name: String, value: Vector4f) =
        glUniform4f(getUniform(name), value.x, value.y, value.z, value.w)
    fun setUniformMat4(name: String, value: Matrix4f) =
        glUniformMatrix4fv(getUniform(name), false, value.get(FloatArray(16)))
}
