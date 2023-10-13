package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.asyncTextFile
import io.github.daylightnebula.kevinengine.ecs.Component
import io.github.daylightnebula.kevinengine.ecs.Entity
import io.github.daylightnebula.kevinengine.ecs.Query
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.math.Float2
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.renderer.*

data class ObjModel(val path: String): Component
private val objQuery = Query(ObjModel::class)
internal val loadObjs = system {
    objQuery.query().forEach { entity ->
        val model = entity.components.first { it is ObjModel } as ObjModel

        entity.remove(ObjModel::class)
        asyncTextFile("/${model.path}.obj") { text ->
            // setup data lists
            val vertices = mutableListOf<Float3>()
            val normals = mutableListOf<Float3>()
            val uvs = mutableListOf<Float2>()
            val faces = mutableListOf<Float3>()

            val finalVertices = mutableListOf<Float>()
            val finalUVs = mutableListOf<Float>()

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
                    "f" -> {
                        // create list of multiple 3 int tuples (0 = vertex, 1 = uv, 2 = normal)
                        val faceIndices = tokens.subList(1, tokens.size).map {
                            val subtokens = it.split("/")
                            Triple(subtokens[0].toInt(), subtokens[1].toInt(), subtokens[2].toInt())
                        }

                        val addFaceIndex: (index: Int) -> Unit = { i ->
                            val faceIndex = faceIndices[i]
                            val vertex = vertices[faceIndex.first - 1]
                            val uv = uvs[faceIndex.second - 1]
                            finalVertices.addAll(listOf(vertex.x, vertex.y, vertex.z))
                            finalUVs.addAll(listOf(uv.x, 1f - uv.y))
                            println("UV $uv")
                        }

                        // add mandatory three of face indices
                        repeat(3) { addFaceIndex(it) }
                        if (faceIndices.size > 3) repeat(faceIndices.size - 3) {
                            addFaceIndex(it + 2)
                            addFaceIndex(it + 1)
                            addFaceIndex(it + 3)
                        }
                    }

                    else -> println("Unknown obj type $header")
                }
            }

            // create a new mesh
            val mesh = Mesh(
                bufferCollection(
                    modelShader,
                    RenderShapeType.TRIANGLES,
                    metadata("position", 0, 3) to genBuffer(*finalVertices.toFloatArray()),
                    metadata("uvs", 1, 2) to genBuffer(*finalUVs.toFloatArray())
                )
            )

            // create a buffer collection and add mesh to entity
            entity.insert(mesh)
        }
    }
}