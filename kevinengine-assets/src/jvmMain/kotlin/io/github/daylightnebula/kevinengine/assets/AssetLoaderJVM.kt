package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.ecs.Entity
import io.github.daylightnebula.kevinengine.renderer.*
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AITexel
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
                Assimp.aiProcess_ValidateDataStructure or
                Assimp.aiProcess_FlipUVs,
        "gltf"
    ) ?: throw IllegalStateException("Failed to load assimp scene!")
    MemoryUtil.memFree(data)

    // load meshes
    val numMeshes = scene.mNumMeshes()
    val meshes = Array(numMeshes) { AIMesh.create(scene.mMeshes()!!.get(it)) }
//    if (numMeshes != 1) throw IllegalStateException("Only one mesh supported for assimp loading right now!")
//    val mesh = AIMesh.create(scene.mMeshes()!!.get())

    // process mesh
//    val vertices = mutableListOf<Float>()
    val vertices = FloatArray(meshes.sumOf { mesh -> mesh.mNumVertices() } * 3)
    var vIdx = 0
    val textures = FloatArray(meshes.sumOf { mesh -> mesh.mTextureCoords(0)!!.remaining() } * 2)
    var tIdx = 0
    val normals = FloatArray(meshes.sumOf { mesh -> mesh.mNormals()!!.remaining() } * 3)
    var nIdx = 0
    val indices = IntArray(meshes.sumOf { mesh -> mesh.mNumFaces() * 3 })
    var iIdx = 0
    var indexOffset = 0

    meshes.forEach { mesh ->
        // process vertices
        val aiVertices = mesh.mVertices()
        while (aiVertices.remaining() > 0) {
            val vertex = aiVertices.get()
            vertices[vIdx * 3] = vertex.x()
            vertices[vIdx * 3 + 1] = vertex.y()
            vertices[vIdx * 3 + 2] = vertex.z()
            vIdx++
        }

        // process textures
        val aiTextures = mesh.mTextureCoords(0)!!
        while (aiTextures.remaining() > 0) {
            val vertex = aiTextures.get()
            textures[tIdx * 2] = vertex.x()
            textures[tIdx * 2 + 1] = vertex.y()
            tIdx++
        }

        // process normals

        // process indices
        val aiFaces = mesh.mFaces()
        val curOffset = indexOffset
        while (aiFaces.remaining() > 0) {
            val face = aiFaces.get()
            val aiIndices = face.mIndices()
            while (aiIndices.remaining() > 0) {
                indices[iIdx] = aiIndices.get() + curOffset
                iIdx++
            }
            indexOffset++
        }
    }

    // add mesh
    entity.insert(Mesh(indexedCollection(
        modelShader,
        RenderShapeType.TRIANGLES,
        ShortArray(indices.size) { indices[it].toShort() },
        metadata("vertexPosition_modelspace", 3) to FloatBuffer(ARRAY_BUFFER, vertices),
        metadata("vertexUV", 2) to FloatBuffer(ARRAY_BUFFER, textures),
    )))
    println("Complete assimp load!")
}