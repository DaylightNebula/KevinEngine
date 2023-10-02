package io.github.daylightnebula.kevinengine.math.tests

import io.github.daylightnebula.kevinengine.math.*

class MatrixTest {
    val matrices = listOf(
        "perspective" to (Mat4(
            Float4(1.0083325f, 0f, 0f, 0f),
            Float4(0f, 1.792591f, 0f, 0f),
            Float4(0f, 0f, -1.002002f, -1f),
            Float4(0f, 0f, -0.2002002f, 0f)
        ) to perspective(45f, 1280/720f, 0.1f, 100f)),
        "view" to (Mat4(
            Float4(0.59999996f, -0.4115966f, 0.6859944f, 0f),
            Float4(0f, 0.8574929f, 0.5144958f, 0f),
            Float4(-0.79999995f, -0.30869746f, 0.5144958f, 0f),
            Float4(-0f, -0f, -5.8309526f, 1.0f)
        ) to lookAt(Float3(4f, 3f, 3f), Float3(0f, 0f, 0f), Float3(0f, 1f, 0f)))
    )

    fun main() {
        matrices.forEach { input ->
            val name = input.first
            val good = input.second.first
            val test = input.second.second

            println("Matrix Test : $name")
            println("Known Good  : ${good.toFloatArrayColumnAligned().toList()}")
            println("Test        : ${test.toFloatArrayColumnAligned().toList()}")
            println()
        }
    }
}