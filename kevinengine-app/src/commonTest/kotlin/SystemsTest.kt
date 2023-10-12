import io.github.daylightnebula.kevinengine.ecs.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SystemsTest {

    class TestA: Component
    class TestB: Component

    @Test
    fun systemsTest1() {
        var counter = 0
        quickCycle(module(system { counter = 10 }))

        assertEquals(counter, 10)
    }

    @Test
    fun testSystemsQuery() {
        val world = World()
        val entity = entity(TestA(), TestB())
        world.insert(entity)
    }

    fun quickCycle(module: Module, cycleCount: Int = 1) {
        SystemsController.register(module)
        SystemsController.start()
        repeat(cycleCount) { SystemsController.update() }
        SystemsController.stop()
    }
}