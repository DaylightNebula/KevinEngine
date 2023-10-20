package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.assets.gltf.loadGltfs
import io.github.daylightnebula.kevinengine.ecs.Component
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.math.Float2
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.renderer.ShaderProgram
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

data class Model(val path: String): Component

val modelShader = ShaderProgram(
    "builtin_obj",
    "/model_vert.glsl",
    "/model_frag.glsl",
    listOf("mvp", "diffuse")
)

fun assets() = module(
    startSystems = listOf(system {}),
    updateSystems = listOf(loadObjs, loadGltfs, loadModelComponents)
)

@Serializable
class KAsset(val meshes: Array<KMesh>)

// mesh
@Serializable
class KMesh(val points: Array<KMeshPoint>, val indices: Array<Int>)
@Serializable
class KMeshPoint(val vertex: Float3, val normal: Float3, val uvs: Float2, val tangent: Float3)

fun serializeKAsset(asset: KAsset) = Cbor.encodeToByteArray(asset)
fun deserializeKAsset(text: ByteArray) = Cbor.decodeFromByteArray<KAsset>(text)
