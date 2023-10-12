package io.github.daylightnebula.kevinengine.flexui

import kotlin.math.roundToInt

data class Box(val top: Val, val bottom: Val, val left: Val, val right: Val) {
    companion object {
        fun all(all: Val) = Box(all, all, all, all)
        fun axis(topBottom: Val, leftRight: Val) = Box(topBottom, topBottom, leftRight, leftRight)
    }

    fun length(dimensions: FlexboxDimensions) = top.calculate(dimensions, Axis.VERTICAL) + bottom.calculate(dimensions, Axis.VERTICAL) + left.calculate(dimensions, Axis.HORIZONTAL) + right.calculate(dimensions, Axis.HORIZONTAL)
    fun isNotEmpty() = length(FlexboxDimensions(0, 0, 1000, 1000)) > 0
}

// some supporting classes for dynamic values
enum class Axis {
    VERTICAL,
    HORIZONTAL
}

// interface for a dynamic value
interface Val {
    fun calculate(context: FlexboxDimensions, axis: Axis): Int
}

// returns the static value given
class PxVal(private val value: Int): Val {
    override fun calculate(context: FlexboxDimensions, axis: Axis) = value
}

// returns the percent value of the window dimension on the axis this value is given too
class PercentVal(private val value: Float): Val {
    override fun calculate(context: FlexboxDimensions, axis: Axis) = when(axis) {
        Axis.HORIZONTAL -> (context.width * value).roundToInt()
        Axis.VERTICAL -> (context.height * value).roundToInt()
    }
}