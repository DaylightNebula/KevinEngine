package io.github.daylightnebula.kevinengine.components

import io.github.daylightnebula.kevinengine.ecs.Component
import io.github.daylightnebula.kevinengine.math.*
import io.github.daylightnebula.kevinengine.math.abs
import kotlin.math.*

data class TransformComponent(
    val position: Float3 = Float3(0f),
    val rotation: Quaternion = Quaternion(),
    val scale: Float3 = Float3(1f)
): Component {
    fun toMatrix(): Mat4 = scale(scale) * rotation(rotation) * translation(position)
    fun lookAt(lookAt: Float3, up: Float3 = Float3(0f, 1f, 0f)) = rotation.lookAlong(lookAt - position, up)
}