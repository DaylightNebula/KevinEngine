package io.github.daylightnebula.kevinengine.renderer.models

import io.github.daylightnebula.kevinengine.asyncTextFile
import io.github.daylightnebula.kevinengine.math.Float2
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.renderer.PrimitiveMesh

class OBJMesh(path: String): PrimitiveMesh(null) {
    init {
        asyncTextFile(path) { text ->
            // setup data lists
            val vertices = mutableListOf<Float3>()
            val normals = mutableListOf<Float3>()
            val uvs = mutableListOf<Float2>()
            val faces = mutableListOf<Float3>()

            // for each line in text
            text.lines().forEach { line ->
                // split up into tokens, skip blank lines and comments
                if (line.isBlank()) return@forEach
                val tokens = line.split(" ")
                if (tokens.first().startsWith("#")) return@forEach

                when (val header = tokens.first()) {
                    "v" -> vertices.add(Float3(tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat()))
                    "vn" -> normals.add(Float3(tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat()))
                    "vt" -> uvs.add(Float2(tokens[1].toFloat(), tokens[2].toFloat()))
                    "f" -> println("TODO faces $tokens")
                    else -> println("Unknown obj type $header")
                }
            }
        }
    }
}