package io.github.daylightnebula.kevinengine.assets.gltf

import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.Float4
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

val gltfJson = Json { ignoreUnknownKeys = true }

@Serializable
data class Gltf(
    val scene: Int = 0,
    val scenes: List<GltfScene> = listOf(),
    val animations: List<GltfAnimation> = listOf(),
    val materials: List<GltfMaterial> = listOf(),
    val meshes: List<GltfMesh> = listOf(),
    val textures: List<GltfTexture> = listOf(),
    val images: List<GltfImage> = listOf(),
    val skins: List<GltfSkin> = listOf(),
    val accessors: List<GltfAccessor> = listOf(),
    val bufferViews: List<GltfBufferView> = listOf(),
    val samplers: List<GltfSampler> = listOf(),
    val buffers: List<GltfBuffer> = listOf()
)

@Serializable
data class GltfScene(
    val name: String = "",
    val nodes: List<Int> = listOf(),
)

// nodes
@Serializable
data class GltfNode(
    val mesh: Int? = null,
    val skin: Int? = null,
    val name: String = "",
    val translation: Float3 = Float3(0f),
    val scale: Float3 = Float3(1f),
    val rotation: Float4 = Float4(0f, 0f, 0f, 1f),
    val children: List<Int> = listOf()
)

// animations
@Serializable
data class GltfAnimation(val name: String = "", val channels: List<GltfChannel> = listOf(), val samplers: List<GltfAnimationSampler> = listOf())
@Serializable
data class GltfChannel(val sampler: Int = 0, val target: GltfChannelTarget = GltfChannelTarget())
@Serializable
data class GltfChannelTarget(val node: Int = 0, val path: String = "")
@Serializable
data class GltfAnimationSampler(val input: Int = 0, val output: Int = 0, val interpolation: String = "")

// materials
@Serializable
data class GltfMaterial(
    val name: String = "",
    val pbrMetallicRoughness: GltfPbrMetallicRoughness? = null,
    val normalTexture: GltfTextureInfo? = null,
    val occlusionTexture: GltfTextureInfo? = null,
    val emissiveTexture: GltfTextureInfo? = null,
    val emissiveFactor: Float3 = Float3(0f),
    val extras: JsonObject? = null,
    val alphaMode: String = "OPAQUE",
    val alphaCutoff: Float = 0.5f,
    val doubleSided: Boolean = false,
)
@Serializable
data class GltfPbrMetallicRoughness(
    val baseColorFactor: List<Float> = listOf(1f, 1f, 1f),
    val baseColorTexture: GltfTextureInfo? = null,
    val metallicRoughnessTexture: GltfTextureInfo? = null,
    val metallicFactor: Float = 1f,
    val roughnessFactor: Float = 1f,
    val extras: JsonObject? = null
)
@Serializable
data class GltfTextureInfo(
    val index: Int,
    val texCoord: Int = 0,
    val scale: Float = 1f,
    val strength: Float = 1f,
    val extras: JsonObject
)

// mesh
@Serializable
data class GltfMesh(
    val primitives: List<GltfPrimitive>,
    val weights: List<Float>? = null,
    val name: String? = null,
    val extras: JsonObject? = null
)
@Serializable
data class GltfPrimitive(
    val attributes: HashMap<String, Int>,
    val indices: Int? = null,
    val material: Int? = null,
    val mode: Int = 4,
    val targets: List<JsonObject>? = null,
    val extras: JsonObject? = null
)

// textures
@Serializable
data class GltfTexture(
    val sampler: Int? = null,
    val source: Int? = null,
    val name: String? = null,
    val extras: JsonObject? = null
)

// images
@Serializable
data class GltfImage(
    val uri: String? = null,
    val mimeType: String? = null,
    val name: String? = null,
    val bufferView: Int? = null,
    val extras: JsonObject? = null
)

// skins
@Serializable
data class GltfSkin(
    val joints: List<Int>,
    val inverseBindMatrices: Int? = null,
    val skeleton: Int? = null,
    val name: String? = null,
    val extras: JsonObject? = null
)

// accessors
@Serializable
data class GltfAccessor(
    val type: GltfAccessorType,
    val componentType: Int,
    val count: Int,
    val byteOffset: Int = 0,
    val normalized: Boolean = false,
    val sparse: GltfAccessorSparse? = null,
    val bufferView: Int? = null,
    val max: List<Float> = listOf(),
    val min: List<Float> = listOf(),
    val name: String? = null,
    val extras: JsonObject? = null
)
@Serializable
enum class GltfAccessorType(val numElements: Int) {
    SCALAR(1),
    VEC2(2), VEC3(3), VEC4(4),
    MAT2(4), MAT3(9), MAT4(16)
}
@Serializable
data class GltfAccessorSparse(
    val count: Int,
    val indices: GltfAccessorSparseIndices,
    val values: GltfAccessorSparseValues
)
@Serializable
data class GltfAccessorSparseIndices(
    val bufferView: Int,
    val byteOffset: Int = 0,
    val componentType: Int,
    val extras: JsonObject? = null
)
@Serializable
data class GltfAccessorSparseValues(
    val bufferView: Int,
    val byteOffset: Int = 0,
    val extras: JsonObject? = null
)

// buffer views
@Serializable
data class GltfBufferView(
    val buffer: Int,
    val byteOffset: Int = 0,
    val byteLength: Int,
    val byteStride: Int? = null,
    val target: Int? = null,
    val name: String? = null,
    val extras: JsonObject? = null
)

// samplers
@Serializable
data class GltfSampler(
    val magFilter: Int? = null,
    val minFilter: Int? = null,
    val wrapS: Int = 10497,
    val wrapT: Int = 10497,
    val name: String? = null,
    val extras: JsonObject? = null
)

// buffers
@Serializable
data class GltfBuffer(
    val uri: String? = null,
    val byteLength: Int,
    val name: String? = null,
    val extras: JsonObject? = null
)
