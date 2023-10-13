package io.github.daylightnebula.kevinengine.assets.gltf

import kotlinx.serialization.Serializable

@Serializable
data class GltfFile(
    val asset: GltfAsset,
    val scene: Int,
    val scenes: List<GltfScene>,
    val nodes: List<GltfNode>,
    val materials: List<GltfMaterial>,
    val meshes: List<GltfMesh>,
    val buffers: List<GltfBuffer>
)

@Serializable
data class GltfAsset(val generator: String, val version: String)

@Serializable
data class GltfScene(val name: String, val nodes: List<Int>)

@Serializable
data class GltfNode(val mesh: Int, val name: String, val rotation: List<Float>)

@Serializable
data class GltfMaterial(val doubleSided: Boolean, val name: String, val pbrMetallicRoughness: GltfRoughness)

@Serializable
data class GltfRoughness(val baseColorFactor: List<Float>, val metallicFactor: Float, val roughnessFactor: Float)

@Serializable
data class GltfMesh(val name: String, val primitives: List<GltfPrimitive>)

@Serializable
data class GltfPrimitive(val attributes: GltfPrimitiveAttributes, val indices: Int, val material: Int)

@Serializable
data class GltfPrimitiveAttributes(val POSITION: Int, val NORMAL: Int, val TEXCOORD_0: Int)

@Serializable
data class GltfBuffer(val byteLength: Int, val uri: String)
