package io.github.daylightnebula.kevinengine.renderer

import dev.romainguy.kotlin.math.perspective
import org.lwjgl.opengl.GL15.*

actual class Buffer actual constructor(actual val inputs: FloatArray) {
    private var id = -1

    actual val length = inputs.size

    actual val isInitialized: Boolean
        get() = id != -1

    actual fun get(): Int {
        // make sure id is initialized, if it's not, generate a new vao with input
        if (id == -1) load()

        // return the id
        return id
    }

    actual fun load() {
        id = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, id)
        glBufferData(GL_ARRAY_BUFFER, inputs, GL_STATIC_DRAW)
    }
}