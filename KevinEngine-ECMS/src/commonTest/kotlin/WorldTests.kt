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

    @Test
    fun worldTestInsert1() {
        world.insert(entity(C(), B(), A()))
        println("${world.root}")
        println("Query 1 ${world.queryRaw("WorldTests.A", "WorldTests.B", "WorldTests.C")}")
        println("Query 2 ${world.query(A::class, B::class, C::class)}")
        println("Query 3 ${world.query(B::class)}")
    }

    @Test
    fun worldTestInsert2() {
        world.clear()
        world.insert(
            entity(A(), B(), C()),
            entity(B(), C(), D()),
            entity(C(), D(), E())
        )
        println(world.root)
        println("Query 1 ${world.query(C::class, D::class)}")
    }
}