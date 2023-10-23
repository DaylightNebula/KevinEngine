package io.github.daylightnebula.kevinengine.renderer

import org.lwjgl.opengl.GL15.*
import java.nio.ByteBuffer

//actual class Buffer actual constructor(actual val inputs: FloatArray) {
//    private var id = -1
//
//    actual val length = inputs.size
//
//    actual val isInitialized: Boolean
//        get() = id != -1
//
//    actual fun get(): Int {
//        // make sure id is initialized, if it's not, generate a new vao with input
//        if (id == -1) load()
//
//        // return the id
//        return id
//    }
//
//    actual fun load() {
//        id = glGenBuffers()
//        glBindBuffer(GL_ARRAY_BUFFER, id)
//        glBufferData(GL_ARRAY_BUFFER, inputs, GL_STATIC_DRAW)
//    }
//}

actual fun createBuffer() = glGenBuffers()
actual fun bindBuffer(id: Int, type: Int) = glBindBuffer(type, id)
actual fun uploadData(id: Int, type: Int, array: FloatArray) = glBufferData(type, array, GL_STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: IntArray) = glBufferData(type, array, GL_STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: ShortArray) = glBufferData(type, array, GL_STATIC_DRAW)
actual fun uploadData(id: Int, type: Int, array: ByteArray) = glBufferData(type, ByteBuffer.wrap(array), GL_STATIC_DRAW)
