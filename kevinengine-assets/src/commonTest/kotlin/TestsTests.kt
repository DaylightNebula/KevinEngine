import kotlin.test.Test

class TestsTests {
    @Test
    fun triangleGenTest() {
        val size = 6
        repeat(size - 2) { idx ->
            val defaultA = (idx / 3) * 3        // hacky integer division to step by 3s
            val defaultB = (idx / 3) * 3 + 1
            val defaultC = (idx / 3) * 3 + 2

            when(idx % 3) {
                0 -> addIndices(defaultA, defaultB, defaultC)
                1 -> addIndices(defaultA, defaultB + 1, defaultC + 1)
                2 -> addIndices(defaultA + 3, defaultB + 1, defaultC + 2)
            }
        }
    }

    fun addIndices(a: Int, b: Int, c: Int) {
        println("$a -> $b -> $c")
    }
}