package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.app
import io.github.daylightnebula.kevinengine.assets.kasset.KAsset
import io.github.daylightnebula.kevinengine.assets.kasset.deserializeKAsset
import io.github.daylightnebula.kevinengine.asyncTextFile
import io.github.daylightnebula.kevinengine.ecs.Entity
import io.github.daylightnebula.kevinengine.ecs.Query
import io.github.daylightnebula.kevinengine.ecs.system

val loadedAssets = hashMapOf<String, KAsset>()
val applyAssets = hashMapOf<String, MutableList<Entity>>()
val loading = mutableListOf<String>()

val query = Query(Model::class)
val loadModelComponents = system {
    query.query().forEach { entity ->
        // grab and remove model component
        val model = entity.components.first { it is Model } as Model
        entity.remove(Model::class)

        // if this asset is loaded, apply it
        if (loadedAssets.containsKey(model.path))
            applyKAssetToEntity(entity, loadedAssets[model.path]!!)
        else {
            // add to apply assets list
            var list = applyAssets[model.path]
            if (list == null) {
                list = mutableListOf()
                applyAssets[model.path] = list
            }
            list.add(entity)

            // if this asset is load being loaded, start async load
            if (!loading.contains(model.path)) {
                loading.add(model.path)
                asyncTextFile(model.path) { text ->
                    // deserialize and save new asset
                    val asset = deserializeKAsset(text)
                    loadedAssets[model.path] = asset

                    // apply asset to all waiting entities
                    applyAssets[model.path]?.forEach { entity -> applyKAssetToEntity(entity, asset) }
                    applyAssets.remove(model.path)
                }
            }
        }
    }
}

fun applyKAssetToEntity(entity: Entity, asset: KAsset) {
    TODO("Apply k asset to entity!")
}
