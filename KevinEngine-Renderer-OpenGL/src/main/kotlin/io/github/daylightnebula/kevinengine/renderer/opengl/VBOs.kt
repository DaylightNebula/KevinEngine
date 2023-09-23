package io.github.daylightnebula.kevinengine.renderer.opengl

import org.lwjgl.opengl.GL15.*

class VBO(val input: FloatArray) {
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

fun genVBO(vararg floats: Float) = VBO(floats)