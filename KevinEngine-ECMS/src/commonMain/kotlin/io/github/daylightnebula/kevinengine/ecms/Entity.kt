package io.github.daylightnebula.kevinengine.ecms

interface Component
fun entity(vararg components: Component) = Entity(null, components.toMutableList())

data class Entity(private val parentNode: Node?, private val internalComponents: MutableList<Component>) {
    val components: List<Component> = internalComponents

    init {
        internalComponents.sortBy { it::class.qualifiedName }
    }
}