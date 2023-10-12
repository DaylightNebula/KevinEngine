package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.renderer.ShaderProgram

val modelShader = ShaderProgram(
    "builtin_obj",
    "/model_vert.glsl",
    "/model_frag.glsl",
    listOf("mvp", "diffuse")
)

fun assets() = module(
    updateSystems = listOf(loadObjs)
)