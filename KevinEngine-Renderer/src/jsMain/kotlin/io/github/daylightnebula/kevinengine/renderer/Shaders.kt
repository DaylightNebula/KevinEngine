package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.math.Float2
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.math.Mat4
import kotlinx.browser.window
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLShader
import org.khronos.webgl.WebGLUniformLocation
import org.khronos.webgl.WebGLRenderingContext as GL

val shaderPrograms = mutableListOf<WebGLProgram>()
val loadedShaders = mutableListOf<WebGLShader>()

actual class Shader actual constructor(actual val path: String, actual val type: ShaderType) {
    var id = -1
    var loading = false
    actual val isInitialized: Boolean
        get() = id != -1

    actual fun load() {
        // if already loading, stop
        if (loading || id != -1) return
        loading = true

        // fetch shader source
        window.fetch(path).then { res ->
            res.text().then { text ->
                // create shader
                val shader = gl.createShader(when(type) {
                    ShaderType.FRAGMENT -> GL.FRAGMENT_SHADER
                    ShaderType.VERTEX -> GL.VERTEX_SHADER
                }) ?: throw IllegalArgumentException("Failed to create shader for $path with type $type")

                // attach source and compile
                val text2 = convertShaderWebGL(text, type)
                println(text2)
                gl.shaderSource(shader, text2)
                gl.compileShader(shader)

                loading = false

                // make sure compiled properly
                val status = gl.getShaderParameter(shader, GL.COMPILE_STATUS) as? Boolean ?: false
                if (!status) {
                    println("Shader $path failed with error")
                    println(gl.getShaderInfoLog(shader))
                    throw IllegalArgumentException("Shader $path failed to compile")
                }

                // save shader and id
                id = loadedShaders.size
                loadedShaders.add(shader)
            }
        }
    }

    actual fun get(): Int {
        if (id != -1) load()
        return id
    }
}

actual class ShaderProgram actual constructor(actual val name: String, vertexPath: String, fragmentPath: String) {
    private var id = -1
    private val vertexShader = Shader(vertexPath, ShaderType.VERTEX)
    private val fragmentShader = Shader(fragmentPath, ShaderType.FRAGMENT)
    private val uniforms = hashMapOf<String, Int>()
    private val uniformLocations = mutableListOf<WebGLUniformLocation>()
    private val attributes = hashMapOf<String, Int>()
//    private val attributeLocations = mutableListOf<>()

    actual val isInitialized: Boolean
        get() = id != -1


    actual fun use() { if (isInitialized) gl.useProgram(shaderPrograms[id]) }

    actual fun load() {
        if (!vertexShader.isInitialized || !fragmentShader.isInitialized) {
            vertexShader.load()
            fragmentShader.load()
            return
        }

        // create program
        val program = gl.createProgram()
            ?: throw IllegalStateException("Failed to create shader program $name")
        gl.attachShader(program, loadedShaders[vertexShader.get()])
        gl.attachShader(program, loadedShaders[fragmentShader.get()])
        gl.linkProgram(program)

        // check if successful
        val status = gl.getProgramParameter(program, GL.LINK_STATUS) as? Boolean ?: false
        if (!status) {
            println("Failed to load shader program $name")
            println(gl.getProgramInfoLog(program))
            throw IllegalArgumentException("Failed to link shader program $name")
        }

        // save program
        id = shaderPrograms.size
        shaderPrograms.add(program)

        // load uniforms
        val uniformCount = gl.getProgramParameter(program, GL.ACTIVE_UNIFORMS) as Int
        repeat(uniformCount) { idx ->
            val info = gl.getActiveUniform(program, idx) ?: return@repeat
            val uniformID = gl.getUniformLocation(program, info.name)
                ?: throw IllegalArgumentException("Failed to get uniform ${info.name}")
            uniforms[info.name] = uniformLocations.size
            uniformLocations.add(uniformID)
            println("Uniform $uniformID $name")
        }

        // load attributes
        val attribCount = gl.getProgramParameter(program, GL.ACTIVE_ATTRIBUTES) as Int
        repeat(attribCount) { idx ->
            val info = gl.getActiveUniform(program, idx) ?: return@repeat
            val attribID = gl.getAttribLocation(program, info.name)
            attributes[info.name] = attribID
        }
    }

    actual fun get(): Int {
        if (id == -1) load()
        return id
    }

    // uniform stuff
    actual fun getUniforms(): Map<String, Int> = uniforms
    actual fun getUniform(name: String): Int = uniforms[name] ?: throw IllegalArgumentException("Not uniform named $name")
    fun getUniformLocation(name: String): WebGLUniformLocation = uniformLocations[uniforms[name] ?: throw IllegalArgumentException("Not uniform named $name")]
    actual fun setUniformFloat(name: String, value: Float) {
        if (isInitialized) gl.uniform1f(getUniformLocation(name), value)
    }

    actual fun setUniformVec2(name: String, value: Float2) {
        if (isInitialized) gl.uniform2f(getUniformLocation(name), value.x, value.y)
    }

    actual fun setUniformVec3(name: String, value: Float3) {
        if (isInitialized) gl.uniform3f(getUniformLocation(name), value.x, value.y, value.z)
    }

    actual fun setUniformVec4(name: String, value: Float4) {
        if (isInitialized) gl.uniform4f(getUniformLocation(name), value.x, value.y, value.z, value.w)
    }

    actual fun setUniformMat4(name: String, value: Mat4) {
        if (isInitialized) gl.uniformMatrix4fv(getUniformLocation(name), false, value.toFloatArray().toTypedArray())
    }
    actual fun setUniformMat4Array(name: String, value: FloatArray) {
        if (isInitialized) gl.uniformMatrix4fv(getUniformLocation(name), false, Float32Array(value.toTypedArray()))
    }

    actual fun setUniformTex(name: String, value: Texture) {
        if (!isInitialized) return
        gl.activeTexture(GL.TEXTURE0)                // use texture unit 0
        gl.bindTexture(GL.TEXTURE_2D, textures[value.get()])
        gl.uniform1i(getUniformLocation(name), 0)        // assign texture to texture unit 0
    }

    actual fun getAttributes(): Map<String, Int> {
        if (!isInitialized) load()
        return attributes
    }
    actual fun getAttribute(name: String) = attributes[name]
        ?: throw IllegalArgumentException("No attribute with name $name registered!")
}
