import io.github.daylightnebula.kevinengine.ecms.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
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

    @Test
    fun worldTestInsert1() {
        val entity = entity(C(), B(), A())
        val entity2 = entity(B())
        world.insert(entity, entity2)

        assertEqualsIgnoreOrder(world.queryRaw("WorldTests.A", "WorldTests.B", "WorldTests.C"), listOf(entity))
        assertEqualsIgnoreOrder(world.query(A::class, B::class, C::class), listOf(entity))
        assertEqualsIgnoreOrder(world.query(B::class), listOf(entity, entity2))
    }

    @Test
    fun worldTestInsert2() {
        world.clear()

        val a = entity(A(), B(), C(), D())
        val b = entity(B(), C(), D())
        val c = entity(C(), D(), E())
        world.insert(a, b, c)

        assertEqualsIgnoreOrder(world.query(A::class, B::class), listOf(a))
        assertEqualsIgnoreOrder(world.query(B::class), listOf(a, b))
        assertEqualsIgnoreOrder(world.query(D::class), listOf(a, b, c))
    }

    @Test
    fun worldTestInsert3() {
        world.clear()

        val a = entity(A(), B(), D(), E())
        val b = entity(A(), C(), D(), E())
        world.insert(a, b)

        assertEqualsIgnoreOrder(world.query(A::class), listOf(a, b))
        assertEqualsIgnoreOrder(world.query(D::class, E::class), listOf(a, b))
    }
}

fun recursivelyPrintTree(node: Node, tabs: Int = 0) {
    node.nodes.forEach { (name, node) ->
        println("${" - ".repeat(tabs)} $name, C: ${node.subconnections.size}, E: ${node.entities.size}")
        recursivelyPrintTree(node, tabs + 1)
    }
}

fun equalsIgnoreOrder(a: List<*>, b: List<*>) = a.size == b.size && a.toSet() == b.toSet()
fun assertEqualsIgnoreOrder(a: List<*>, b: List<*>) =
    if (!equalsIgnoreOrder(a, b)) throw AssertionError("List $a is not $b")
    else true