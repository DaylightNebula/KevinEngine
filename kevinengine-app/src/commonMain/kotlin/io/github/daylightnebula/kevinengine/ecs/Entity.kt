package io.github.daylightnebula.kevinengine.ecs

import io.github.daylightnebula.kevinengine.mainWorld
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.Mat4
import io.github.daylightnebula.kevinengine.math.scale
import io.github.daylightnebula.kevinengine.math.translation
import kotlin.reflect.KClass

interface Component
fun entity(vararg components: Component) = Entity(null, components.toMutableList())

class Entity(parentNode: Node?, val internalComponents: MutableList<Component>) {
    val components: List<Component> get() = internalComponents
    var parentNode: Node? = parentNode
        internal set

    init {
        internalComponents.sortBy { it::class.simpleName }
    }

    fun modComponents(world: World = mainWorld, callback: (components: MutableList<Component>) -> Unit) {
        world.remove(this)
        callback(internalComponents)
        internalComponents.sortBy { it::class.simpleName }
        world.insert(this)
    }

    override fun toString(): String = components.map { it::class.simpleName!! }.toString()
    fun spawn(world: World = mainWorld) = world.insert(this)
    fun despawn(world: World = mainWorld) = world.remove(this)
    fun insert(vararg components: Component, world: World = mainWorld) =
        modComponents(world) { it.addAll(components) }
    fun remove(vararg components: KClass<*>, world: World = mainWorld) =
        modComponents(world) { it.removeAll { c -> components.any { ck -> ck.isInstance(c) }}}
}
