package io.github.daylightnebula.kevinengine.renderer

fun bufferCollection(renderShape: RenderShapeType, vararg buffers: Pair<BufferMetadata, Buffer>) =
    BasicBufferCollection(renderShape, mapOf(*buffers))
fun indexedCollection(shape: RenderShapeType, indices: ShortArray, vararg buffers: Pair<BufferMetadata, Buffer>) =
    IndexedBufferCollection(shape, indices, mapOf(*buffers))

interface BufferCollection {
    fun render(shader: ShaderProgram)
}

class BasicBufferCollection(val shape: RenderShapeType, val buffers: Map<BufferMetadata, Buffer>): BufferCollection {
    override fun render(shader: ShaderProgram) {
        // load attributes
        buffers.entries.forEachIndexed { index, (metadata, buffer) ->
            if (!buffer.isInitialized) buffer.load()

            enableBuffer(index)
            bindBuffer(buffer.get(), ARRAY_BUFFER)
            attachBuffer(shader.getAttribute(metadata.name), metadata.entrySize, buffer)
            clearBuffer(ARRAY_BUFFER)
        }

        // draw
        if (!shader.isInitialized) shader.load()
        else {
            shader.use()
            drawArrays(shape, buffers.values.first().size)
        }

        // unbind attributes
        repeat(buffers.size) { idx -> disableBuffer(idx) }
    }
}

class IndexedBufferCollection(
    val shape: RenderShapeType,
    val indices: ShortArray,
    val buffers: Map<BufferMetadata, Buffer>
): BufferCollection {
    val indicesBuffer = ShortBuffer(ELEMENT_ARRAY_BUFFER, indices)

    override fun render(shader: ShaderProgram) {
        if (!indicesBuffer.isInitialized) indicesBuffer.load()

        // load attributes
        buffers.entries.forEachIndexed { index, (metadata, buffer) ->
            if (!buffer.isInitialized) buffer.load()

            enableBuffer(index)
            bindBuffer(buffer.get(), ARRAY_BUFFER)
            attachBuffer(shader.getAttribute(metadata.name), metadata.entrySize, buffer)
            clearBuffer(ARRAY_BUFFER)
        }

        // draw
        if (!shader.isInitialized) shader.load()
        else {
            shader.use()
            bindBuffer(indicesBuffer.get(), ELEMENT_ARRAY_BUFFER)
            drawIndexed(shape, indices.size)
            clearBuffer(ELEMENT_ARRAY_BUFFER)
        }

        // unbind attributes
        repeat(buffers.size) { idx -> disableBuffer(idx) }
    }
}
