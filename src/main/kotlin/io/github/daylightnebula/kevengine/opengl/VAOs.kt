package io.github.daylightnebula.kevengine.opengl

import org.lwjgl.opengl.GL15.*
import kotlin.properties.Delegates

class VAO(val input: FloatArray) {
    private var id = -1

    fun get(): Int {
        // make sure id is initialized, if it's not, generate a new vao with input
        if (id == -1) generate()

        // return the id
        return id
    }

    fun generate() {
        glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, id)
        glBufferData(GL_ARRAY_BUFFER, input, GL_STATIC_DRAW)
    }
}

fun genVAO(vararg floats: Float) = VAO(floats)