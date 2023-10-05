package io.github.daylightnebula.kevinengine.ecms

data class Node(val parents: Pair<Node, Node>?, val components: List<String>, val children: MutableList<Node>, val entities: MutableList<Entity>) {
    override fun toString(): String = "$components: { ${children.joinToString { it.toString() }} }"
}

data class World(val surfaceNodes: HashMap<String, Node> = hashMapOf()) {
    fun insert(entity: Entity, debug: Boolean = false) =
        getOrCreateNodes(entity.components.map { it::class.qualifiedName!! }, debug).entities.add(entity)

    // todo product is backwards somehow
    private fun getOrCreateNodes(list: List<String>, debug: Boolean = false): Node {
        var index = list.size - 1
        debug(debug, "Starting getOrCreateNodes with list $list")

        // get or create root node as the current node
        val rootName = list[index]
        var curNode = surfaceNodes[rootName] ?: insertSurfaceNode(rootName, debug)

        debug(debug, "Found root $rootName")
        // loop until the current node components matches the input components
        while(!curNode.components.containsAll(list)) {
            index -= 1

            // attempt to find a child that matches the next component
            debug(debug, "Sub start getOrCreateNodes with list ${list.joinToString { "\"${it}\"" }}")
            val targetComponent = list[index]
            var next = curNode.children.firstOrNull { it.components.contains(targetComponent) }

            if (next == null) {
                // get new sub node (same layer as cur node)
                val subNode = getOrCreateNodes(list.subList(0, index + 1), debug)

                // create new node
                next = Node(Pair(curNode, subNode), list, mutableListOf(), mutableListOf())

                // add new node to sub and cur node
                curNode.children.add(next)
                subNode.children.add(next)
                debug(debug, "Created sub node $targetComponent")
            } else debug(debug, "Found sub node $targetComponent")

            // update current node with next
            curNode = next
        }
        debug(debug, "Done $list ${curNode.components}")

        return curNode
    }

    private fun insertSurfaceNode(name: String, debug: Boolean): Node {
        val node = Node(null, listOf(name), mutableListOf(), mutableListOf())
        surfaceNodes[name] = node
        debug(debug, "Created surface node $name")
        return node
    }

    fun clear() = surfaceNodes.clear()
    fun debug(flag: Boolean, text: String) { if (flag) println(text) }
}