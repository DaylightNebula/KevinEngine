package io.github.daylightnebula.kevinengine.math

data class Int2(var x: Int, var y: Int) {
    constructor(i: Int): this(i, i)
    constructor(): this(0, 0)
    operator fun get(index: Int) = array()[index]
    operator fun set(index: Int, int: Int) = when(index) {
        0 -> x = int
        1 -> y = int
        else -> throw IndexOutOfBoundsException()
    }
    fun array() = intArrayOf(x, y)
    override fun toString(): String = "[$x, $y]"
}

data class Int3(var x: Int, var y: Int, var z: Int) {
    constructor(i: Int): this(i, i, i)
    constructor(): this(0, 0, 0)
    operator fun get(index: Int) = array()[index]
    operator fun set(index: Int, int: Int) = when(index) {
        0 -> x = int
        1 -> y = int
        2 -> z = int
        else -> throw IndexOutOfBoundsException()
    }
    fun array() = intArrayOf(x, y, z)
    override fun toString(): String = "[$x, $y, $z]"
}

data class Int4(var x: Int, var y: Int, var z: Int, var w: Int) {
    constructor(i: Int): this(i, i, i, i)
    constructor(): this(0, 0, 0, 0)
    operator fun get(index: Int) = array()[index]
    operator fun set(index: Int, int: Int) = when(index) {
        0 -> x = int
        1 -> y = int
        2 -> z = int
        3 -> w = int
        else -> throw IndexOutOfBoundsException()
    }
    fun array() = intArrayOf(x, y, z, w)
    override fun toString(): String = "[$x, $y, $z, $w]"
}