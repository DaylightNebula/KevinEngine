package io.github.daylightnebula.kevinengine.renderer

import org.khronos.webgl.*
import org.khronos.webgl.WebGLRenderingContext as GL

var bufferRefs = mutableListOf<WebGLBuffer>()

actual fun createBuffer(): Int {
    bufferRefs.add(gl.createBuffer() ?: throw IllegalStateException("Could not create buffer!"))
    return bufferRefs.size - 1
}

actual fun bindBuffer(id: Int, type: Int) = gl.bindBuffer(type, bufferRefs[id])
actual fun enableBuffer(id: Int) = gl.enableVertexAttribArray(id)
actual fun disableBuffer(id: Int) = gl.disableVertexAttribArray(id)
actual fun clearBuffer(type: Int) = gl.bindBuffer(type, null)
actual fun uploadData(id: Int, type: Int, array: FloatArray) = gl.bufferData(type, Float32Array(array.toTypedArray()), GL.STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: IntArray) = gl.bufferData(type, Int32Array(array.toTypedArray()), GL.STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: ShortArray) = gl.bufferData(type, Int16Array(array.toTypedArray()), GL.STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: ByteArray) = gl.bufferData(type, Int8Array(array.toTypedArray()), GL.STATIC_DRAW)

actual fun attachBuffer(index: Int, entrySize: Int, buffer: Buffer) =
    gl.vertexAttribPointer(
        index, entrySize, when(buffer) {
            is FloatBuffer -> GL.FLOAT
            is IntBuffer -> GL.INT
            is ShortBuffer -> GL.SHORT
            is ByteBuffer -> GL.BYTE
            else -> throw IllegalStateException("Unknown buffer type $buffer")
        }, false, 0, 0
    )
