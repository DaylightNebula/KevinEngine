package io.github.daylightnebula.kevinengine.app

import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Mat4

fun Mat4.scale(vec: Float3)= this.apply {
    this[0, 0] *= vec.x
    this[1, 1] *= vec.y
    this[2, 2] *= vec.z
}
fun Mat4.scale(value: Float) = scale(Float3(value, value, value))

fun Mat4.translate(vec: Float3) = this.apply {
    this[3, 0] = vec.x
    this[3, 1] = vec.x
    this[3, 2] = vec.x
}