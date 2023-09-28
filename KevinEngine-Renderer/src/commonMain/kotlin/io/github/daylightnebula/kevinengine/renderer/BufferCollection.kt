package io.github.daylightnebula.kevinengine.renderer

data class BufferMetadata(val name: String, val layoutIndex: Int, val infoCount: Int)
fun metadata(name: String, layoutIndex: Int, infoCount: Int) = BufferMetadata(name, layoutIndex, infoCount)

fun bufferCollection(shader: ShaderProgram, renderShape: RenderShapeType, vararg buffers: Pair<BufferMetadata, Buffer>) =
    BufferCollection(shader, renderShape, mapOf(*buffers))

data class BufferCollection(val shader: ShaderProgram, val renderShape: RenderShapeType, private val buffers: Map<BufferMetadata, Buffer>) {
    fun render() {
        // attach buffers
        buffers.entries.forEachIndexed { index, (metadata, buffer) ->
            attachBuffer(index, metadata, buffer)
        }

        // draw attached
        drawAttachedRaw(shader, buffers.values.first().length, renderShape)

        // detach buffers
        repeat(buffers.size) { i -> detachBufferIndex(i) }
    }
}