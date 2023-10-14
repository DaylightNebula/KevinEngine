package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.assets.gltf.gltfLoader
import io.github.daylightnebula.kevinengine.ecs.Component
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.renderer.ShaderProgram

data class Model(val path: String): Component

val modelShader = ShaderProgram(
    "builtin_obj",
    "/model_vert.glsl",
    "/model_frag.glsl",
    listOf("mvp", "diffuse")
)

fun assets() = module(
    startSystems = listOf(system { runKAssetConversion() }),
    updateSystems = listOf(loadObjs, gltfLoader, loadModelComponents)
)

expect fun runKAssetConversion()