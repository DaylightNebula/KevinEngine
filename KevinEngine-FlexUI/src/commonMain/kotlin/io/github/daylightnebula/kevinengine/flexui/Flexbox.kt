package io.github.daylightnebula.kevinengine.flexui

import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import io.github.daylightnebula.kevinengine.app.scale
import io.github.daylightnebula.kevinengine.app.translate
import io.github.daylightnebula.kevinengine.renderer.*

data class FlexboxDimensions(val x: Int, val y: Int, val width: Int, val height: Int)
enum class Alignment { START, CENTER, END }
enum class FlexDirection { COLUMN_REVERSE, COLUMN, ROW, ROW_REVERSE }

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
    val borderRadius: Box = Box.all(PxVal(0)),
    val padding: Box = Box.all(PxVal(0)),
    val borderFactor: Float = if (border.isNotEmpty()) 0.02f else 0.0000001f,
    val childrenDirection: FlexDirection = FlexDirection.COLUMN,
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
        flexboxQuad.shader.setUniformVec4("borderRadius", Float4(
            borderRadius.top.calculate(dimensions, Axis.VERTICAL).toFloat() / dimensions.height,
            borderRadius.bottom.calculate(dimensions, Axis.VERTICAL).toFloat() / dimensions.height,
            borderRadius.left.calculate(dimensions, Axis.HORIZONTAL).toFloat() / dimensions.width,
            borderRadius.right.calculate(dimensions, Axis.HORIZONTAL).toFloat() / dimensions.width,
        ))
        flexboxQuad.shader.setUniformFloat("borderFactor", borderFactor)
        flexboxQuad.render()

        // render children only if necessary
        if (children.isNotEmpty()) {
            // build info for building children quickly
            // format: child x start, child y start, target width, target height
            val childOffsets = intArrayOf(0, 0, 0, 0)
            val totalChildDimensions = when (childrenDirection) {
                FlexDirection.COLUMN -> {
                    // get total child dimensions based on direction
                    val totalChildDimensions = intArrayOf(
                        children.maxOf { box -> box.getTargetWidth(dimensions) },
                        children.sumOf { box -> box.getTargetHeight(dimensions) }
                    )

                    // setup x and width offsets
                    childOffsets[0] = 0
                    childOffsets[2] = totalChildDimensions[0]

                    // setup y, y step, and height offsets
                    childOffsets[1] = ((dimensions.height - totalChildDimensions[1]) - dimensions.height) / 4
                    childOffsets[3] = -1

                    totalChildDimensions
                }

                FlexDirection.COLUMN_REVERSE -> {
                    // get total child dimensions based on direction
                    val totalChildDimensions = intArrayOf(
                        children.maxOf { box -> box.getTargetWidth(dimensions) },
                        children.sumOf { box -> box.getTargetHeight(dimensions) }
                    )

                    // setup x and width offsets
                    childOffsets[0] = 0
                    childOffsets[2] = totalChildDimensions[0]

                    // setup y, y step, and height offsets
                    childOffsets[1] = ((dimensions.height - totalChildDimensions[1]) - dimensions.height) / -4
                    childOffsets[3] = -2

                    totalChildDimensions
                }

                FlexDirection.ROW_REVERSE -> {
                    // get total child dimensions based on direction
                    val totalChildDimensions = intArrayOf(
                        children.sumOf { box -> box.getTargetWidth(dimensions) },
                        children.maxOf { box -> box.getTargetHeight(dimensions) }
                    )

                    // setup x and width offsets
                    childOffsets[0] = ((dimensions.width - totalChildDimensions[0]) - dimensions.width) / 4
                    childOffsets[2] = -1

                    // setup y, y step, and height offsets
                    childOffsets[1] = 0
                    childOffsets[3] = totalChildDimensions[1]

                    totalChildDimensions
                }

                FlexDirection.ROW -> {
                    // get total child dimensions based on direction
                    val totalChildDimensions = intArrayOf(
                        children.sumOf { box -> box.getTargetWidth(dimensions) },
                        children.maxOf { box -> box.getTargetHeight(dimensions) }
                    )

                    // setup x and width offsets
                    childOffsets[0] = ((dimensions.width - totalChildDimensions[0]) - dimensions.width) / -4
                    childOffsets[2] = -2

                    // setup y, y step, and height offsets
                    childOffsets[1] = 0
                    childOffsets[3] = totalChildDimensions[1]

                    totalChildDimensions
                }
            }

            // modify child offsets based on alignments
            when (horizontalAlignment) {
                Alignment.START -> when (childrenDirection) {
                    FlexDirection.COLUMN_REVERSE, FlexDirection.COLUMN -> childOffsets[0] = (totalChildDimensions[0] - dimensions.width) / 2
                    FlexDirection.ROW, FlexDirection.ROW_REVERSE -> childOffsets[0] = (dimensions.width - totalChildDimensions[0]) / -2 + (totalChildDimensions[0] / 2 - ((children.firstOrNull()?.getTargetWidth(dimensions) ?: 0) / 2))
                }
                Alignment.END -> when(childrenDirection) {
                    FlexDirection.COLUMN_REVERSE, FlexDirection.COLUMN -> childOffsets[0] = (totalChildDimensions[0] - dimensions.width) / -2
                    FlexDirection.ROW, FlexDirection.ROW_REVERSE -> childOffsets[0] = (dimensions.width - totalChildDimensions[0]) / 2 + (totalChildDimensions[0] / 2 - ((children.firstOrNull()?.getTargetWidth(dimensions) ?: 0) / 2))
                }
                Alignment.CENTER -> {}
            }
            when (verticalAlignment) {
                Alignment.START -> when(childrenDirection) {
                    FlexDirection.COLUMN_REVERSE, FlexDirection.COLUMN -> childOffsets[1] = (dimensions.height - totalChildDimensions[1]) / -2 + (totalChildDimensions[1] / 2 - ((children.firstOrNull()?.getTargetHeight(dimensions) ?: 0) / 2))
                    FlexDirection.ROW, FlexDirection.ROW_REVERSE -> childOffsets[1] = (totalChildDimensions[1] - dimensions.height) / 2
                }
                Alignment.END -> when(childrenDirection) {
                    FlexDirection.COLUMN_REVERSE, FlexDirection.COLUMN -> childOffsets[1] = (dimensions.height - totalChildDimensions[1]) / 2 + (totalChildDimensions[1] / 2 - ((children.firstOrNull()?.getTargetHeight(dimensions) ?: 0) / 2))
                    FlexDirection.ROW, FlexDirection.ROW_REVERSE -> childOffsets[1] = (totalChildDimensions[1] - dimensions.height) / -2
                }
                Alignment.CENTER -> {}
            }

            // call children render
            children.forEachIndexed { index, child ->
                // calculate target width and height
                val targetWidth =
                    if (childOffsets[2] < 0) child.getTargetWidth(dimensions)
                    else (dimensions.width - child.getTargetWidth(dimensions)) / 2
                val targetHeight =
                    if (childOffsets[3] < 0) child.getTargetHeight(dimensions)
                    else (dimensions.height - child.getTargetHeight(dimensions)) / 2

                // calculate final child dimensions
                val childDimensions = FlexboxDimensions(
                    dimensions.x + childOffsets[0],
                    dimensions.y + childOffsets[1],
                    targetWidth - padding.left.calculate(dimensions, Axis.HORIZONTAL) - padding.right.calculate(dimensions, Axis.VERTICAL),
                    targetHeight - padding.top.calculate(dimensions, Axis.VERTICAL) - padding.bottom.calculate(dimensions, Axis.VERTICAL)
                )

                // render child
                child.render(childDimensions)

                // update offsets
                if (childOffsets[2] == -1) childOffsets[0] += targetWidth
                if (childOffsets[3] == -1) childOffsets[1] += targetHeight
                if (childOffsets[2] == -2) childOffsets[0] -= targetWidth
                if (childOffsets[3] == -2) childOffsets[1] -= targetHeight
            }
        }
    }

    open fun recalculate(dimensions: FlexboxDimensions) = FlexboxDimensions(
        dimensions.x,
        dimensions.y,
        getTargetWidth(dimensions),
        getTargetHeight(dimensions)
    )

    private fun getChildrenDefaultWidth(dimensions: FlexboxDimensions): Int = when (childrenDirection) {
        FlexDirection.ROW, FlexDirection.ROW_REVERSE -> children.sumOf { box -> box.getTargetWidth(dimensions) }
        FlexDirection.COLUMN_REVERSE, FlexDirection.COLUMN -> children.maxOfOrNull { box -> box.getTargetWidth(dimensions) } ?: 0
    }

    private fun getChildrenDefaultHeight(dimensions: FlexboxDimensions): Int = when (childrenDirection) {
        FlexDirection.ROW, FlexDirection.ROW_REVERSE -> children.maxOfOrNull { box -> box.getTargetHeight(dimensions) } ?: 0
        FlexDirection.COLUMN_REVERSE, FlexDirection.COLUMN -> children.sumOf { box -> box.getTargetHeight(dimensions) }
    }

    private fun getTargetWidth(dimensions: FlexboxDimensions) = ((width?.calculate(dimensions, Axis.HORIZONTAL) ?: defaultWidth.calculate(dimensions, Axis.HORIZONTAL))
            + border.left.calculate(dimensions, Axis.HORIZONTAL) + border.right.calculate(dimensions, Axis.HORIZONTAL))
        .coerceAtLeast(minWidth.calculate(dimensions, Axis.HORIZONTAL))
        .coerceAtLeast(getChildrenDefaultWidth(dimensions) + padding.left.calculate(dimensions, Axis.HORIZONTAL) + padding.right.calculate(dimensions, Axis.HORIZONTAL))
        .coerceAtMost(
            maxWidth.calculate(dimensions, Axis.HORIZONTAL)
                    - margin.left.calculate(dimensions, Axis.HORIZONTAL)
                    - margin.right.calculate(dimensions, Axis.HORIZONTAL)
        )

    private fun getTargetHeight(dimensions: FlexboxDimensions) = ((height?.calculate(dimensions, Axis.VERTICAL) ?: defaultHeight.calculate(dimensions, Axis.VERTICAL))
            + border.top.calculate(dimensions, Axis.VERTICAL) + border.bottom.calculate(dimensions, Axis.VERTICAL))
        .coerceAtLeast(minHeight.calculate(dimensions, Axis.VERTICAL))
        .coerceAtLeast(getChildrenDefaultHeight(dimensions) + padding.top.calculate(dimensions, Axis.VERTICAL) + padding.bottom.calculate(dimensions, Axis.VERTICAL))
        .coerceAtMost(
            maxHeight.calculate(dimensions, Axis.VERTICAL)
                    - margin.top.calculate(dimensions, Axis.VERTICAL)
                    - margin.bottom.calculate(dimensions, Axis.VERTICAL)
        )
}

// flexbox quad buffer collection
val flexboxQuad = bufferCollection(
    ShaderProgram(
        "flexbox",
        "/flexbox_vert.glsl",
        "/flexbox_frag.glsl",
        listOf("matrix", "color", "border", "borderColor", "borderRadius", "borderFactor")
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
