package io.github.daylightnebula.kevinengine.ecms

import kotlin.reflect.KClass

// nodes in a world tree, contains other nodes and entities
fun node(name: String, parent: Node? = null, connection: Node? = null, vararg nodes: Pair<String, Node>) = Node(name, parent, connection, hashMapOf(*nodes))
data class Node(val name: String, val parent: Node?, val connection: Node?, val nodes: HashMap<String, Node>, val entities: MutableList<Entity> = mutableListOf()) {
    override fun toString(): String = "{ ${nodes.entries.joinToString { "${it.key}: ${it.value}" }} }"
}

// essentially a container for the world tree
data class World(internal val root: Node = node("root")) {
    // finds a node for the given entity, creating new nodes if necessary, and adds the entity
    fun insert(vararg entities: Entity) = entities.forEach { insert(it) }
    fun insert(entity: Entity) {
        val node = queryNode(entity.components.map { it::class.qualifiedName!! }, createIfNotFound = true)
            ?: throw IllegalStateException("Failed to insert entity $entity")
        node.entities.add(entity)
        entity.parentNode = node
    }

    // removes the given entity from
    fun remove(entity: Entity) =
        queryNode(entity.components.map { it::class.qualifiedName!! }, createIfNotFound = false)
            ?.entities?.remove(entity)
            ?: false

    private fun queryNode(list: List<String>, createIfNotFound: Boolean = false): Node? {
        // if list is empty, then default to root
        if (list.isEmpty()) return root

        // find a node in the root, create if necessary
        var curNode = findInRoot(list.first(), createIfNotFound = createIfNotFound) ?: return null

        // loop until the node is found
        repeat(list.size) { index ->
            if (index == 0) return@repeat
            val target = list[index]

            // attempt to find a new node with the current target in the current node
            var next = curNode.nodes[target]

            if (next == null) {
                if (!createIfNotFound) return null
                else {
                    // back track through parents until a parent contains target or the root is found
                    var trackingNode = curNode
                    while(!trackingNode.nodes.containsKey(target) && trackingNode.name != "root")
                        trackingNode = trackingNode.parent!!

                    // if tracking node is root, make new node
                    if (trackingNode.name == "root") {
                        trackingNode = node(target, parent = root)
                        root.nodes[target] = trackingNode
                    }

                    // create a new node that connects what we just found and cur node
                    next = trackingNode
                    curNode.nodes[target] = next
                }
            }

            // if next is not null, make it the cur node, otherwise, return nothing
            curNode = next
        }

        return curNode
    }

    private fun findInRoot(lookingFor: String, createIfNotFound: Boolean): Node? {
        var output = root.nodes[lookingFor]

        if (createIfNotFound && output == null) {
            output = node(lookingFor, parent = root)
            root.nodes[lookingFor] = output
        }

        return output
    }

    private fun collectDescendentEntities(node: Node): List<Entity> {
        val list = node.nodes.values.flatMap { collectDescendentEntities(it) }.toMutableList()
        list.addAll(node.entities)
        return list
    }

    // function to quickly clear the world
    fun clear() = root.nodes.clear()

    // exposed query functions to interface with internal query
    fun query(vararg list: KClass<*>) = query(list.toList())
    fun query(list: List<KClass<*>>) = queryRaw(list.map { it.qualifiedName!! })
    fun queryRaw(vararg list: String) = queryRaw(list.toList())
    fun queryRaw(list: List<String>): List<Entity>  = queryNode(list.sorted(), createIfNotFound = false)?.let { collectDescendentEntities(it) } ?: listOf()
}