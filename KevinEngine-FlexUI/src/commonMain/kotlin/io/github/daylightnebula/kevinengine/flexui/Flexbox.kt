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
    val id: String? = null,
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
    open var defaultWidth: Val = PxVal(0)
    open var defaultHeight: Val = PxVal(0)

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
                dimensions.x.toFloat() / (windowDimensions.width / 2),
                -dimensions.y.toFloat() / (windowDimensions.height / 2),
                0f
            )

        // call render
        flexboxQuad.shader.setUniformMat4("matrix", matrix)
        flexboxQuad.shader.setUniformVec4("color", backgroundColor)
        flexboxQuad.shader.setUniformVec4("borderColor", borderColor)
        flexboxQuad.shader.setUniformVec4("border", Float4(
            border.top.calculate(dimensions, Axis.VERTICAL).toFloat() / dimensions.height,
            border.bottom.calculate(dimensions, Axis.VERTICAL).toFloat() / dimensions.height,
            border.left.calculate(dimensions, Axis.HORIZONTAL).toFloat() / dimensions.width,
            border.right.calculate(dimensions, Axis.HORIZONTAL).toFloat() / dimensions.width,
        ))
        flexboxQuad.render()

        // call children render
        val childDimensions = FlexboxDimensions(
            dimensions.x,// + padding.left.calculate(dimensions, Axis.HORIZONTAL),
            dimensions.y,// + padding.top.calculate(dimensions, Axis.VERTICAL),
            dimensions.width - padding.left.calculate(dimensions, Axis.HORIZONTAL) - padding.right.calculate(dimensions, Axis.VERTICAL),
            dimensions.height - padding.top.calculate(dimensions, Axis.VERTICAL) - padding.bottom.calculate(dimensions, Axis.VERTICAL)
        )
        children.forEach { child ->
            child.render(childDimensions)
        }
    }

    open fun recalculate(dimensions: FlexboxDimensions): FlexboxDimensions {
        // get target width and height
        val targetWidth = ((width?.calculate(dimensions, Axis.HORIZONTAL) ?: defaultWidth.calculate(dimensions, Axis.HORIZONTAL))
                + border.left.calculate(dimensions, Axis.HORIZONTAL) + border.right.calculate(dimensions, Axis.HORIZONTAL))
            .coerceAtLeast(minWidth.calculate(dimensions, Axis.HORIZONTAL))
            .coerceAtMost(maxWidth.calculate(dimensions, Axis.HORIZONTAL))
        val targetHeight = ((height?.calculate(dimensions, Axis.VERTICAL) ?: defaultHeight.calculate(dimensions, Axis.VERTICAL))
                + border.top.calculate(dimensions, Axis.VERTICAL) + border.bottom.calculate(dimensions, Axis.VERTICAL))
            .coerceAtLeast(minHeight.calculate(dimensions, Axis.VERTICAL))
            .coerceAtMost(maxHeight.calculate(dimensions, Axis.VERTICAL))

        // get target x and y based on alignment and above target dimensions
        val xOffset = when(horizontalAlignment) {
            Alignment.START -> -dimensions.width / 2 + (targetWidth / 2)
            Alignment.END -> dimensions.width / 2 - (targetWidth / 2)
            Alignment.CENTER -> 0
        }
        val yOffset = when(verticalAlignment) {
            Alignment.START -> -dimensions.height / 2 + (targetHeight / 2)
            Alignment.END -> dimensions.height / 2 - (targetHeight / 2)
            Alignment.CENTER -> 0
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
        listOf("matrix", "color", "border", "borderColor")
    ),
    RenderShapeType.QUADS,
    metadata("positions", 0, 3) to genBuffer(
        -1f, -1f, 0f,
        1f, -1f, 0f,
        1f, 1f, 0f,
        -1f, 1f, 0f
    ),
    metadata("colors", 1, 2) to genBuffer(
        0f, 1f,
        1f, 1f,
        1f, 0f,
        0f, 0f
    )
)
