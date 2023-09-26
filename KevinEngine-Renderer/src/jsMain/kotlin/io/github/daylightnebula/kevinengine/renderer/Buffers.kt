package io.github.daylightnebula.kevinengine.renderer

import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLRenderingContext as GL

var bufferRefs = mutableListOf<WebGLBuffer>()

actual class Buffer actual constructor(actual val inputs: FloatArray) {
    var id = -1

    actual val length = inputs.size
    actual val isInitialized: Boolean
        get() = id != -1

    actual fun load() {
        val buffer = gl.createBuffer()
            ?: throw IllegalStateException("Could not create buffer!")
        gl.bindBuffer(GL.ARRAY_BUFFER, buffer)
        gl.bufferData(GL.ARRAY_BUFFER, Float32Array(inputs.toTypedArray()), GL.STATIC_DRAW)
        id = bufferRefs.size
        bufferRefs.add(buffer)
    }

    actual fun get(): Int {
        if (id == -1) load()
        return id
    }
}