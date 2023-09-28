package io.github.daylightnebula.kevinengine.flexui

import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import io.github.daylightnebula.kevinengine.app.scale
import io.github.daylightnebula.kevinengine.app.translate
import io.github.daylightnebula.kevinengine.renderer.*

data class FlexboxDimensions(val x: Int, val y: Int, val width: Int, val height: Int)
enum class Alignment { START, CENTER, END }

open class Flexbox(
    val width: Val? = null,
    val height: Val? = null,
    val minWidth: Val = PercentVal(0f),
    val minHeight: Val = PercentVal(0f),
    val maxWidth: Val = PercentVal(1f),
    val maxHeight: Val = PercentVal(1f),
    val horizontalAlignment: Alignment = Alignment.START,
    val verticalAlignment: Alignment = Alignment.START,
    val backgroundColor: Float4 = Float4(1f, 1f, 1f, 1f),
    val borderColor: Float4 = Float4(0f, 0f, 0f, 1f),
    val margin: Box = Box.all(PxVal(0)),
    val border: Box = Box.all(PxVal(0)),
    val padding: Box = Box.all(PxVal(0)),
    val children: MutableList<Flexbox> = mutableListOf()
) {
    // abstract functions
    private lateinit var dimensions: FlexboxDimensions
    open var defaultWidth: Int = 0
    open var defaultHeight: Int = 0

    // render
    open fun render(parent: FlexboxDimensions) {
        // make sure dimensions is up-to-date
        if (!this::dimensions.isInitialized) dimensions = recalculate(parent)

        // start building matrix
        println("Dimensions $dimensions")
        val matrix = Mat4.identity()
            .scale(
                dimensions.width.toFloat() / windowDimensions.width,
                dimensions.height.toFloat() / windowDimensions.height,
                1f
            ).translate(
                (dimensions.x.toFloat() - (windowDimensions.width / 2)) / (windowDimensions.width / 2),
                -((dimensions.y.toFloat() - (windowDimensions.height / 2)) / (windowDimensions.height / 2)),
                0f
            )

        // call render
        flexboxQuad.shader.setUniformMat4("matrix", matrix)
        flexboxQuad.shader.setUniformVec4("color", backgroundColor)
        flexboxQuad.render()
    }

    open fun recalculate(dimensions: FlexboxDimensions): FlexboxDimensions {
        // get target width and height
        val targetWidth = (width?.calculate(dimensions, Axis.HORIZONTAL) ?: defaultWidth)
            .coerceAtLeast(minWidth.calculate(dimensions, Axis.HORIZONTAL))
            .coerceAtMost(maxWidth.calculate(dimensions, Axis.HORIZONTAL))
        val targetHeight = (height?.calculate(dimensions, Axis.VERTICAL) ?: defaultHeight)
            .coerceAtLeast(minHeight.calculate(dimensions, Axis.VERTICAL))
            .coerceAtMost(maxHeight.calculate(dimensions, Axis.VERTICAL))

        // get target x and y based on alignment and above target dimensions
        val xOffset = when(horizontalAlignment) {
            Alignment.START -> (targetWidth / 2) - if (targetWidth > dimensions.width) targetWidth - dimensions.width else 0
            Alignment.END -> dimensions.width + (targetWidth / 2) - targetWidth
            Alignment.CENTER -> dimensions.width / 2
        }
        val yOffset = when(verticalAlignment) {
            Alignment.START -> (targetHeight / 2) - if (targetHeight > dimensions.height) targetHeight - dimensions.height else 0
            Alignment.END -> dimensions.height + (targetHeight / 2) - targetHeight
            Alignment.CENTER -> dimensions.height / 2
        }

        return FlexboxDimensions(dimensions.x + xOffset, dimensions.y + yOffset, targetWidth, targetHeight)
    }
}

// flexbox quad buffer collection
val flexboxQuad = bufferCollection(
    ShaderProgram(
        "flexbox",
        "/flexbox_vert.glsl",
        "/flexbox_frag.glsl",
        listOf("matrix", "color")
    ),
    RenderShapeType.QUADS,
    metadata("positions", 0, 3) to genBuffer(
        -1f, -1f, 0f,
        1f, -1f, 0f,
        1f, 1f, 0f,
        -1f, 1f, 0f
    )
)
