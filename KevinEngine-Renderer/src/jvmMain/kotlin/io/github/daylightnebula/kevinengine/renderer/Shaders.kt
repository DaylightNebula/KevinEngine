package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.math.Float2
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.math.Mat4
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.charset.Charset
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
    fragmentPath: String
) {
    // id of this shader program
    private var id = -1

    // individual shaders
    private val vertexShader = Shader(vertexPath, ShaderType.VERTEX)
    private val fragmentShader = Shader(fragmentPath, ShaderType.FRAGMENT)

    // uniforms and attribs
    private val uniforms = hashMapOf<String, Int>()
    private val attributes = hashMapOf<String, Int>()

    actual val isInitialized: Boolean
        get() = id != -1

    // function that returns the above id, if the id is -1 (uninitialized), the generate function is called first
    actual fun get(): Int {
        if (id == -1) load()
        return id
    }

    actual fun use() { if (isInitialized) glUseProgram(id) }

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

        // get uniform metadata
        val uniformCount = glGetProgrami(id, GL_ACTIVE_UNIFORMS)
        val uniformStrLength = glGetProgrami(id, GL_ACTIVE_UNIFORM_MAX_LENGTH)

        // create buffers
        val nameBuf = BufferUtils.createByteBuffer(uniformStrLength)
        val lengthBuf = BufferUtils.createIntBuffer(1)
        val sizeBuf = BufferUtils.createIntBuffer(1)
        val typeBuf = BufferUtils.createIntBuffer(1)

        // load all uniforms
        repeat(uniformCount) { idx -> // length size type name
            // if not first uniform, clear buffers
            if (idx != 0) {
                nameBuf.clear()
                lengthBuf.clear()
                sizeBuf.clear()
                typeBuf.clear()
            }

            // read name and its length from the shader
            glGetActiveUniform(id, idx, lengthBuf, sizeBuf, typeBuf, nameBuf)

            // save the uniform
            val name = Charset.defaultCharset().decode(nameBuf).subSequence(0, lengthBuf.get()).toString()
            val uniformID = glGetUniformLocation(id, name)
            uniforms[name] = uniformID
            println("Uniform $uniformID $name")
        }

        // load attributes
        val attribCount = glGetProgrami(id, GL_ACTIVE_ATTRIBUTES)
        val attribStrLength = glGetProgrami(id, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH)
        val attribNameBuf = BufferUtils.createByteBuffer(attribStrLength)
        repeat(attribCount) { idx ->
            // clear buffers
            attribNameBuf.clear()
            lengthBuf.clear()
            sizeBuf.clear()
            typeBuf.clear()

            // read name and length
            glGetActiveAttrib(id, idx, lengthBuf, sizeBuf, typeBuf, attribNameBuf)

            // save attrib
            val name = Charset.defaultCharset().decode(attribNameBuf).subSequence(0, lengthBuf.get()).toString()
            val attribLocation = glGetAttribLocation(id, name)
            attributes[name] = attribLocation
            println("Attribute $attribLocation $name")
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
        glUniformMatrix4fv(getUniform(name), false, value.toFloatArrayColumnAligned())
    actual fun setUniformTex(name: String, value: Texture) {
        glActiveTexture(GL_TEXTURE0)                // use texture unit 0
        glBindTexture(GL_TEXTURE_2D, value.get())   // bind texture
        glUniform1i(getUniform(name), 0)        // assign texture to texture unit 0
    }

    actual fun getAttributes(): Map<String, Int> {
        if (!isInitialized) load()
        return attributes
    }
    actual fun getAttribute(name: String) = attributes[name]
        ?: throw IllegalArgumentException("No attribute with name $name registered!")
}