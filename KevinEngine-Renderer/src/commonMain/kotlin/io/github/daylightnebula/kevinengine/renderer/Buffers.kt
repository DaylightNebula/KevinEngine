@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.daylightnebula.kevinengine.renderer

const val ARRAY_BUFFER = 0x8892
const val ELEMENT_ARRAY_BUFFER = 0x8893

fun metadata(name: String, entrySize: Int) = BufferMetadata(name, entrySize)
data class BufferMetadata(val name: String, val entrySize: Int)

// functions to be abstracted to various platforms
expect fun createBuffer(): Int
expect fun enableBuffer(id: Int)
expect fun disableBuffer(id: Int)
expect fun clearBuffer(type: Int)
expect fun bindBuffer(id: Int, type: Int)
expect fun uploadData(id: Int, type: Int, array: FloatArray)
expect fun uploadData(id: Int, type: Int, array: IntArray)
expect fun uploadData(id: Int, type: Int, array: ShortArray)
expect fun uploadData(id: Int, type: Int, array: ByteArray)
expect fun attachBuffer(index: Int, entrySize: Int, buffer: Buffer)

// abstract buffer
abstract class Buffer(val type: Int) {
    private var id = -1
    val isInitialized: Boolean get() = id != -1
    abstract val size: Int

    abstract fun load0(id: Int)
    internal fun load() {
        id = createBuffer()
        bindBuffer(id, type)
        load0(id)
    }

    fun get(): Int {
        if (id == -1) load()
        return id
    }
}

fun genBuffer(vararg floats: Float, type: Int = ARRAY_BUFFER) = FloatBuffer(type, floats)
class FloatBuffer(type: Int, val array: FloatArray): Buffer(type) {
    override val size: Int = array.size
    override fun load0(id: Int) = uploadData(id, type, array)
}

fun genBuffer(vararg values: Int, type: Int = ARRAY_BUFFER) = IntBuffer(type, values)
class IntBuffer(type: Int, val array: IntArray): Buffer(type) {
    override val size: Int = array.size
    override fun load0(id: Int) = uploadData(id, type, array)
}

fun genBuffer(vararg values: Short, type: Int = ARRAY_BUFFER) = ShortBuffer(type, values)
class ShortBuffer(type: Int, val array: ShortArray): Buffer(type) {
    override val size: Int = array.size
    override fun load0(id: Int) = uploadData(id, type, array)
}

fun genBuffer(vararg values: Byte, type: Int = ARRAY_BUFFER) = ByteBuffer(type, values)
class ByteBuffer(type: Int, val array: ByteArray): Buffer(type) {
    override val size: Int = array.size
    override fun load0(id: Int) = uploadData(id, type, array)
}
