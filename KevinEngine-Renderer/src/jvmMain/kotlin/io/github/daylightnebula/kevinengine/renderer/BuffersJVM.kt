package io.github.daylightnebula.kevinengine.renderer

import org.lwjgl.opengl.GL20.*
import java.lang.IllegalStateException

actual fun createBuffer() = glGenBuffers()
actual fun enableBuffer(id: Int) = glEnableVertexAttribArray(id)
actual fun disableBuffer(id: Int) = glDisableVertexAttribArray(id)
actual fun clearBuffer(type: Int) = glBindBuffer(type, 0)
actual fun bindBuffer(id: Int, type: Int) = glBindBuffer(type, id)
actual fun uploadData(id: Int, type: Int, array: FloatArray) = glBufferData(type, array, GL_STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: IntArray) = glBufferData(type, array, GL_STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: ShortArray) = glBufferData(type, array, GL_STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: ByteArray) = glBufferData(type, java.nio.ByteBuffer.wrap(array), GL_STATIC_DRAW)

actual fun attachBuffer(index: Int, entrySize: Int, buffer: Buffer) =
    glVertexAttribPointer(
        index, entrySize, when(buffer) {
            is FloatBuffer -> GL_FLOAT
            is IntBuffer -> GL_INT
            is ShortBuffer -> GL_SHORT
            is ByteBuffer -> GL_BYTE
            else -> throw IllegalStateException("Unknown buffer type $buffer")
        }, false, 0, 0
    )