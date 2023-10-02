package io.github.daylightnebula.kevinengine.app

import dev.romainguy.kotlin.math.*
import kotlin.math.tan

// functions for creating a scaled matrix
fun scaleMatrix(vec: Float3)= Mat4.identity().apply {
    this[0, 0] *= vec.x
    this[1, 1] *= vec.y
    this[2, 2] *= vec.z
}
fun scaleMatrix(value: Float) = scaleMatrix(Float3(value, value, value))
fun scaleMatrix(x: Float, y: Float, z: Float) = scaleMatrix(Float3(x, y, z))

// functions for creating a translation matrix
fun translationMatrix(vec: Float3) = Mat4.identity().apply {
    this[0, 3] = vec.x
    this[1, 3] = vec.y
    this[2, 3] = vec.z
}
fun translationMatrix(x: Float, y: Float, z: Float) = translationMatrix(Float3(x, y, z))

fun getFloatArrayFromMat(mat: Mat4): FloatArray = floatArrayOf(
    mat[0, 0], mat[0, 1], mat[0, 2], mat[0, 3],
    mat[1, 0], mat[1, 1], mat[1, 2], mat[1, 3],
    mat[2, 0], mat[2, 1], mat[2, 2], mat[2, 3],
    mat[3, 0], mat[3, 1], mat[3, 2], mat[3, 3]
)