package io.github.daylightnebula.kevinengine.ecs

import io.github.daylightnebula.kevinengine.mainWorld
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.Mat4
import io.github.daylightnebula.kevinengine.math.scale
import io.github.daylightnebula.kevinengine.math.translation

interface Component
fun entity(vararg components: Component) = Entity(null, components.toMutableList())

class Entity(parentNode: Node?, internalComponents: MutableList<Component>) {
    val components: List<Component> = internalComponents
    var parentNode: Node? = parentNode
        internal set

    init {
        internalComponents.sortBy { it::class.simpleName }
    }

    override fun toString(): String = components.map { it::class.simpleName!! }.toString()
    fun spawn(world: World = mainWorld) = world.insert(this)
    fun despawn(world: World = mainWorld) = world.remove(this)
}
