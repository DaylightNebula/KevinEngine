package io.github.daylightnebula.kevinengine.app

import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Mat4

fun Mat4.scale(vec: Float3)= this.apply {
    this[0, 0] *= vec.x
    this[1, 1] *= vec.y
    this[2, 2] *= vec.z
}
fun Mat4.scale(value: Float) = scale(Float3(value, value, value))
fun Mat4.scale(x: Float, y: Float, z: Float) = scale(Float3(x, y, z))

fun Mat4.translate(vec: Float3) = this.apply {
    this[0, 3] = vec.x
    this[1, 3] = vec.y
    this[2, 3] = vec.z
}
fun Mat4.translate(x: Float, y: Float, z: Float) = translate(Float3(x, y, z))