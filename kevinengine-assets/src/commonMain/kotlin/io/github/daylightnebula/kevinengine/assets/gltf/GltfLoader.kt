package io.github.daylightnebula.kevinengine.assets.gltf

import io.github.daylightnebula.kevinengine.asyncTextFile
import io.github.daylightnebula.kevinengine.ecs.Component
import io.github.daylightnebula.kevinengine.ecs.Query
import io.github.daylightnebula.kevinengine.ecs.system
import kotlinx.serialization.json.Json

data class GltfModel(val path: String): Component

val gltfQuery = Query(GltfModel::class)
private val json = Json { ignoreUnknownKeys = true }

val gltfLoader = system {
    gltfQuery.query().forEach { entity ->
        val model = entity.components.filterIsInstance<GltfModel>().first()
        entity.remove(GltfModel::class)
        val path = "/${model.path}.gltf"
        println("Path $path")
        asyncTextFile(path) { text ->
            val file = json.decodeFromString<GltfFile>(text)
            println("Decoded file $file")
        }
    }
}