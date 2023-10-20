package io.github.daylightnebula.kevinengine.assets.gltf

import io.github.daylightnebula.kevinengine.assets.modelShader
import io.github.daylightnebula.kevinengine.asyncTextFile
import io.github.daylightnebula.kevinengine.ecs.Component
import io.github.daylightnebula.kevinengine.ecs.Query
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.math.*
import io.github.daylightnebula.kevinengine.renderer.*
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class GltfModel(val path: String): Component
val query = Query(GltfModel::class)

@OptIn(ExperimentalEncodingApi::class)
val loadGltfs = system {
    // query all models
    query.query().forEach { entity ->
        // get and request model
        val model = entity.components.find { it is GltfModel } as GltfModel
        entity.remove(GltfModel::class)
        asyncTextFile("/${model.path}.gltf") {
            // get gltf
            val gltf = gltfJson.decodeFromString<Gltf>(it)

            // load buffers
            val initBuffers = Array(gltf.buffers.size) {
                Base64.decode(gltf.buffers[it].uri?.substring("data:application/octet-stream;base64,".length) ?: "")
            }
            val buffers = Array(gltf.bufferViews.size) { idx ->
                val view = gltf.bufferViews[idx]
                val buffer = initBuffers[view.buffer]
                buffer.sliceArray(IntRange(view.byteOffset, view.byteOffset + view.byteLength - 1))
            }

            // final lists for vertices, normals, and uvs
            val fVertices = mutableListOf<Float>()
            val fUvs = mutableListOf<Float>()

            // load meshes
            val meshes = Array(gltf.meshes.size) { mIdx ->
                val gltfMesh = gltf.meshes[mIdx]

                // get data from accessors
                val primitive = gltfMesh.primitives.firstOrNull() ?: throw IllegalArgumentException("Only one primitive supported!")
                val indices = gltf.accessors[primitive.indices!!].fromBuffers(buffers) as? Array<UShort> ?: throw IllegalArgumentException("Gltf indices must be array of ushort!")
                val attributes = primitive.attributes.mapValues { (_, aIdx) -> gltf.accessors[aIdx] }
                val positions = attributes["POSITION"]!!.fromBuffers(buffers) as? Array<Float3> ?: throw IllegalArgumentException("Gltf positions must be array of vec3!")
                val uvs = attributes["TEXCOORD_0"]!!.fromBuffers(buffers) as? Array<Float2> ?: throw IllegalArgumentException("Gltf uvs must be array of vec2!")

                println("Indices ${indices.size} : ${indices.toList().maxBy { it }} ${positions.size}")
            }

            // create a new mesh
            val mesh = Mesh(
                bufferCollection(
                    modelShader,
                    RenderShapeType.TRIANGLES,
                    metadata("position", 0, 3) to genBuffer(*fVertices.toFloatArray()),
                    metadata("uvs", 1, 2) to genBuffer(*fUvs.toFloatArray())
                )
            )

            // create a buffer collection and add mesh to entity
            entity.insert(mesh)
        }
    }
}

// get a slice of a byte array from a gltf accessor
fun ByteArray.fromAccessor(accessor: GltfAccessor): ByteArray =
    this.sliceArray(IntRange(accessor.byteOffset, accessor.byteOffset + (accessor.count) - 1)) // todo account for total byte count

// turns a byte array into an array specified by the given component type
fun ByteArray.convertToAccessorComponentType(type: Int): Array<*> = when(type) {
    5120 -> this.toTypedArray()
    5121 -> this.map { it.toUByte() }.toTypedArray()
    5122 -> Array(size / 2) { idx -> (this[idx * 2].toUByte().toInt() + (this[(idx * 2) + 1].toInt() shl 8)).toShort() }
    5123 -> Array(size / 2) { idx -> (this[idx * 2].toUByte().toInt() + (this[(idx * 2) + 1].toInt() shl 8)).toUShort() }
    5125 -> Array(size / 4) { idx -> this[idx * 4].toUInt() + (this[idx * 4 + 1].toUInt() shl 8) + (this[idx * 4 + 2].toUInt() shl 16) + (this[idx * 4 + 3].toUInt() shl 24) }
    5126 -> Array(size / 4) { idx -> Float.fromBits(this[idx * 4].toInt() + (this[idx * 4 + 1].toInt() shl 8) + (this[idx * 4 + 2].toInt() shl 16) + (this[idx * 4 + 3].toInt() shl 24)) }
    else -> throw IllegalArgumentException("Illegal accessor component type $type")
}

// turns a gltf accessor into an array of whatever its specified type after grabbing its byte from the given array of byte arrays
fun Array<*>.safeFloats(type: String) = this as? Array<Float> ?: throw IllegalArgumentException("Gltf accessor $type only supports float arrays!")
fun GltfAccessor.fromBuffers(buffers: Array<ByteArray>): Array<*> {
    // get data using above functions
    val data = buffers[bufferView ?: 0]
        .fromAccessor(this)
        .convertToAccessorComponentType(componentType)

    // match converted to given type
    return when(type) {
        GltfAccessorType.SCALAR -> data
        GltfAccessorType.VEC2 -> {
            val data = data.safeFloats("Vec2")
            Array(data.size / 2) { Float2(data[it * 2], data[it * 2 + 1]) }
        }
        GltfAccessorType.VEC3 -> {
            val data = data.safeFloats("Vec3")
            Array(data.size / 3) { Float3(data[it * 3], data[it * 3 + 1], data[it * 3 + 2]) }
        }
        GltfAccessorType.VEC4 -> {
            val data = data.safeFloats("Vec4")
            Array(data.size / 4) { Float4(data[it * 4], data[it * 4 + 1], data[it * 4 + 2], data[it * 4 + 3]) }
        }
        GltfAccessorType.MAT2 -> {
            val data = data.safeFloats("Mat2")
            Array(data.size / 4) {
                Mat2(
                    Float2(data[it * 4], data[it * 4 + 1]),
                    Float2(data[it * 4 + 2], data[it * 4 + 3])
                )
            }
        }
        GltfAccessorType.MAT3 -> {
            val data = data.safeFloats("Mat3")
            Array(data.size / 9) {
                Mat3(
                    Float3(data[it * 9 + 0], data[it * 9 + 1], data[it * 9 + 2]),
                    Float3(data[it * 9 + 3], data[it * 9 + 4], data[it * 9 + 5]),
                    Float3(data[it * 9 + 6], data[it * 9 + 7], data[it * 9 + 8])
                )
            }
        }
        GltfAccessorType.MAT4 -> {
            val data = data.safeFloats("Mat4")
            Array(data.size / 16) {
                Mat4(
                    Float4(data[it * 16 + 0], data[it * 16 + 1], data[it * 16 + 2], data[it * 16 + 3]),
                    Float4(data[it * 16 + 4], data[it * 16 + 5], data[it * 16 + 6], data[it * 16 + 7]),
                    Float4(data[it * 16 + 8], data[it * 16 + 9], data[it * 16 + 10], data[it * 16 + 11]),
                    Float4(data[it * 16 + 12], data[it * 16 + 13], data[it * 16 + 14], data[it * 16 + 15]),
                )
            }
        }
    }
}
