package io.github.daylightnebula.kevinengine.renderer

fun bufferCollection(renderShape: RenderShapeType, vararg buffers: Pair<String, Buffer>) =
    BufferCollection(renderShape, mapOf(*buffers))

data class BufferCollection(val renderShape: RenderShapeType, private val buffers: Map<String, Buffer>) {
    fun render() {
        buffers.entries.forEachIndexed { index, (name, buffer) ->
            println("Attaching $index with $name")
            attachBuffer(name, index, buffer)
        }
        val length = buffers.values.first().length
        println("Length $length")
        drawAttachedRaw(length, renderShape)
        repeat(buffers.size) { i -> detachBufferIndex(i); println("Detaching $i") }
    }
}