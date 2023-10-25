package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.app
import io.github.daylightnebula.kevinengine.asyncBinFile
import io.github.daylightnebula.kevinengine.asyncTextFile
import io.github.daylightnebula.kevinengine.ecs.Entity
import io.github.daylightnebula.kevinengine.ecs.Query
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.renderer.*

val loadedAssets = hashMapOf<String, KAsset>()
val applyAssets = hashMapOf<String, MutableList<Entity>>()
val loading = mutableListOf<String>()

val query = Query(Model::class)
val loadModelComponents = system {
    query.query().forEach { entity ->
        // grab and remove model component
        val model = entity.components.first { it is Model } as Model
        entity.remove(Model::class)
        asyncTextFile("/${model.path}") { text ->
            loadAssimpAsset(entity, text, model.path.split(".").last())
        }
    }
}

fun applyKAssetToEntity(entity: Entity, asset: KAsset, path: String) {
    // get mesh and generate some final arrays
    val mesh = asset.meshes.firstOrNull() ?: throw IllegalArgumentException("")
    val positions = FloatArray(mesh.indices.size * 3)
    val uvs = FloatArray(mesh.indices.size * 2)

    // use indices to fill above arrays
    repeat(mesh.indices.size) { idx ->
        val index = mesh.indices[idx]
        val point = mesh.points[index]

        positions[index * 3 + 0] = point.vertex.x
        positions[index * 3 + 1] = point.vertex.y
        positions[index * 3 + 2] = point.vertex.z
        uvs[index * 2 + 0] = point.uvs.x
        uvs[index * 2 + 1] = point.uvs.y
    }

    // insert mesh
    entity.insert(mesh(
        bufferCollection(
            RenderShapeType.TRIANGLES,
            metadata("vertexPosition_modelspace", 3) to genBuffer(*positions),
            metadata("vertexUV", 2) to genBuffer(*uvs)
        )
    ))
}

expect fun loadAssimpAsset(entity: Entity, text: String, type: String)
