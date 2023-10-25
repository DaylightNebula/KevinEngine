package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.ecs.Entity
import io.github.daylightnebula.kevinengine.renderer.*
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AITexel
import org.lwjgl.assimp.AITexture
import org.lwjgl.assimp.Assimp
import org.lwjgl.system.MemoryUtil
import java.lang.IllegalStateException
import java.nio.ByteBuffer

// https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter27/chapter27.html
// todo save to some type of kasset for JS
// todo textures

actual fun loadAssimpAsset(entity: Entity, text: String) {
    // convert text to byte buffer (cant use byte buffer wrap cause java)
    val _data: ByteArray = text.encodeToByteArray()
    val data = MemoryUtil.memCalloc(_data.size)
    data.put(_data)
    data.flip()

    // import the text and free the data array
    val scene = Assimp.aiImportFileFromMemory(
        data,
        Assimp.aiProcess_Triangulate or
                Assimp.aiProcess_FlipUVs,
        "gltf"
    ) ?: throw IllegalStateException("Failed to load assimp scene!")
    MemoryUtil.memFree(data)

    // load meshes
    val numMeshes = scene.mNumMeshes()
    val meshes = Array(numMeshes) { AIMesh.create(scene.mMeshes()!!.get(it)) }
    val collections = mutableListOf<BufferCollection>()

    // for each mesh
    meshes.forEach { mesh ->
        // process vertices
        val vertices = FloatArray(mesh.mNumVertices() * 3)
        val aiVertices = mesh.mVertices()
        repeat(mesh.mNumVertices()) { vIdx ->
            val vertex = aiVertices.get()
            vertices[vIdx * 3] = vertex.x()
            vertices[vIdx * 3 + 1] = vertex.y()
            vertices[vIdx * 3 + 2] = vertex.z()
        }

        // process textures
        val aiTextures = mesh.mTextureCoords(0)!!
        val textures = FloatArray(aiTextures.remaining() * 2)
        repeat (aiTextures.remaining()) {tIdx ->
            val vertex = aiTextures.get()
            textures[tIdx * 2] = vertex.x()
            textures[tIdx * 2 + 1] = vertex.y()
        }

        // process normals

        // process indices
        val aiFaces = mesh.mFaces()
        val indices = ShortArray(aiFaces.remaining() * 3)
        repeat (indices.size / 3) { iIdx ->
            // get face and indices
            val face = aiFaces.get()
            val aiIndices = face.mIndices()
            if (aiIndices.remaining() != 3) throw IllegalStateException("Model not triangulated!")

            // add indices
            indices[iIdx * 3 + 0] = aiIndices.get().toShort()
            indices[iIdx * 3 + 1] = aiIndices.get().toShort()
            indices[iIdx * 3 + 2] = aiIndices.get().toShort()
        }
        println("Highest index ${indices.maxBy { it }} with vertices ${vertices.size / 3} ${textures.size / 2}")

        // create collection
        collections.add(
            indexedCollection(
                modelShader, RenderShapeType.TRIANGLES,
                indices,
                metadata("vertexPosition_modelspace", 3) to FloatBuffer(ARRAY_BUFFER, vertices),
                metadata("vertexUV", 2) to FloatBuffer(ARRAY_BUFFER, textures)
            )
        )
    }

    // add mesh
    entity.insert(Mesh(collections))
}