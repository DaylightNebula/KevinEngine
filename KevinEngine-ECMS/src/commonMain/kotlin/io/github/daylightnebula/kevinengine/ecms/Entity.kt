package io.github.daylightnebula.kevinengine.ecms

interface Component
fun entity(vararg components: Component) = Entity(null, components.toMutableList())

class Entity(parentNode: Node?, internalComponents: MutableList<Component>) {
    val components: List<Component> = internalComponents
    var parentNode: Node? = parentNode
        internal set

    init {
        internalComponents.sortBy { it::class.qualifiedName }
    }

    override fun toString(): String = components.map { it::class.qualifiedName!! }.toString()
}