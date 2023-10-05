import io.github.daylightnebula.kevinengine.ecms.*
import kotlin.test.Test
import kotlin.test.assertEquals

class WorldTests {
    // set up a world
    val world = World()

    // set up some components
    class A: Component
    class B: Component
    class C: Component
    class D: Component
    class E: Component

    fun recursivePrintNodeTree(nodes: Collection<Node>, tabs: Int) {
        nodes.forEach {
            println("${" - ".repeat(tabs)} ${it.components.joinToString { "$it " }}")
            if (it.entities.isNotEmpty())
                println("${" - ".repeat(tabs)} E: ${it.entities.size}")
            recursivePrintNodeTree(it.children, tabs + 1)
        }
    }
    fun recursiveCheckNodes(a: List<Node>, b: List<Node>): Boolean {
        if (a.size != b.size) {
            println("Failed on size check, A: $a, B: $b")
            return false
        }

        repeat(a.size) { idx ->
            // make sure components are equal
            if (a[idx].components != b[idx].components) {
                println("Failed on components check, A: $a, B: $b")
                return false
            }

            // make sure entities are equal
            if (a[idx].entities != b[idx].entities) {
                println("Failed on entities check, A: $a, B: $b")
                return false
            }

            // run checks of children
            if (!recursiveCheckNodes(a[idx].children, b[idx].children)) return false
        }

        return true
    }
    fun assertNodeTreeEquals(calculated: HashMap<String, Node>, expected: HashMap<String, Node>) =
        if (!recursiveCheckNodes(calculated.values.toList(), expected.values.toList())) throw AssertionError("Failed recursive check!")
        else true

    @Test
    fun worldTestInsert1() {
        val entity = entity(C(), B(), A())
        world.insert(entity)

        val a = Node(null, listOf(A::class.qualifiedName!!), mutableListOf(), mutableListOf())
        val b = Node(null, listOf(B::class.qualifiedName!!), mutableListOf(), mutableListOf())
        val c = Node(null, listOf(C::class.qualifiedName!!), mutableListOf(), mutableListOf())

        val dual = Node(Pair(a, b), listOf(A::class.qualifiedName!!, B::class.qualifiedName!!), mutableListOf(), mutableListOf())
        a.children.add(dual)
        b.children.add(dual)

        val triple = Node(Pair(dual, c), listOf(A::class.qualifiedName!!, B::class.qualifiedName!!, C::class.qualifiedName!!), mutableListOf(), mutableListOf())
        dual.children.add(triple)
        c.children.add(triple)
        triple.entities.add(entity)

        val tree = hashMapOf(
            A::class.qualifiedName!! to a,
            B::class.qualifiedName!! to b,
            C::class.qualifiedName!! to c,
        )

//        println("Generated: ")
//        recursivePrintNodeTree(world.surfaceNodes.values, 0)
//
//        println("\nExpected: ")
//        recursivePrintNodeTree(tree.values, 0)

        assertNodeTreeEquals(world.surfaceNodes, tree)
    }
}