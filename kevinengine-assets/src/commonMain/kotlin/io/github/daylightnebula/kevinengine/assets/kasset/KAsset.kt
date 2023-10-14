package io.github.daylightnebula.kevinengine.assets.kasset

import io.github.daylightnebula.kevinengine.math.Float2
import io.github.daylightnebula.kevinengine.math.Float3
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val chunkSplit = "\n"
const val keyValueSplit = ":"
const val elementSplit = ";"

@Serializable
class KAsset(val meshes: Array<KMesh>)
@Serializable
class KMesh(val vertices: Array<KMeshPoint>, val indices: Array<Int>)
@Serializable
class KMeshPoint(val vertex: Float3, val normal: Float3, val uvs: Float2, val tangent: Float3)

fun serializeKAsset(asset: KAsset) = Json.encodeToString(asset)
fun deserializeKAsset(text: String) = Json.decodeFromString<KAsset>(text)
