package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.ecs.Entity
import io.github.daylightnebula.kevinengine.math.*
import io.github.daylightnebula.kevinengine.renderer.*
import org.lwjgl.assimp.*
import org.lwjgl.system.MemoryUtil
import java.lang.IllegalStateException
import java.nio.ByteBuffer

// https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter27/chapter27.html
// https://learnopengl.com/Guest-Articles/2020/Skeletal-Animation
// todo save to some type of kasset for JS
// todo textures

actual fun loadAssimpAsset(entity: Entity, text: String, type: String) {
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
        type
    ) ?: throw IllegalStateException("Failed to load assimp scene!")
    MemoryUtil.memFree(data)

    val bones = hashMapOf<String, Bone>()

    // load meshes
    val numMeshes = scene.mNumMeshes()
    val meshes = Array(numMeshes) { loadMesh(AIMesh.create(scene.mMeshes()!!.get(it)), bones) }

    // nodes
    val rootNode = scene.mRootNode()?.let { loadNodes(it, meshes) }
        ?: throw IllegalStateException("Assimp mesh had no root node!")

    // todo load animation info
    val aiAnimations = scene.mAnimations()
    val animations = hashMapOf<String, Animation>()
    while (aiAnimations != null && aiAnimations.remaining() > 0) {
        val aiAnimation = AIAnimation.create(aiAnimations.get())
        val pair = loadAnimation(aiAnimation)
        animations[pair.first] = pair.second
    }

    // add mesh
    entity.insert(Mesh(rootNode, bones, animations))
}

fun loadMesh(mesh: AIMesh, bones: HashMap<String, Bone>): IndexedBufferCollection {
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

    // load bones and weights for each vertex
    val aiBones = mesh.mBones()
    val vertexBoneIDs = IntArray(vertices.size / 3 * 4)
    val vertexWeights = FloatArray(vertices.size / 3 * 4)
    while(aiBones != null && aiBones.remaining() > 0) {
        // get and save bone
        val bone = AIBone.create(aiBones.get())
        val boneID = bones.size
        bones[bone.mName().dataString()] = Bone(boneID, bone.mOffsetMatrix().toMat4())

        // apply weights
        val aiWeights = bone.mWeights()
        while(aiWeights.remaining() > 0) {
            // get weight and its vertex counterparts
            val weight = aiWeights.get()
            val range = weight.mVertexId() * 4 .. weight.mVertexId() * 4 + 3

            // find set index by
            // 1. attempting to find an empty slot in range
            // 2. find the lowest weight that is less than the given weight
            // 3. skip
            val setIndex = vertexBoneIDs.indexInRangeWithCondition(range) { it == -1 }
                ?: vertexWeights.indexInRangeWithLowest(range) { if (it < weight.mWeight()) it - weight.mWeight() else null }
                ?: continue

            // save
            vertexBoneIDs[setIndex] = boneID
            vertexWeights[setIndex] = weight.mWeight()
        }
    }

    return indexedCollection(
        RenderShapeType.TRIANGLES, indices,
        metadata("vertexPosition_modelspace", 3) to FloatBuffer(ARRAY_BUFFER, vertices),
        metadata("vertexUV", 2) to FloatBuffer(ARRAY_BUFFER, textures),
//        metadata("boneIDs", 4) to IntBuffer(ARRAY_BUFFER, vertexBoneIDs),
//        metadata("boneWeights", 4) to FloatBuffer(ARRAY_BUFFER, vertexWeights)
    )
}

fun loadNodes(node: AINode, meshes: Array<IndexedBufferCollection>): MeshNode {
    // load meshes
    val collections = mutableListOf<IndexedBufferCollection>()
    val aiMeshes = node.mMeshes()
    while (aiMeshes != null && aiMeshes.remaining() > 0) collections.add(meshes[aiMeshes.get()])

    // load children
    val children = mutableListOf<MeshNode>()
    val aiChildren = node.mChildren()
    while(aiChildren != null && aiChildren.remaining() > 0) children.add(loadNodes(AINode.create(aiChildren.get()), meshes))

    // create final node
    println("Node ${node.mName().dataString()}")
    return MeshNode(node.mName().dataString(), node.mTransformation().toMat4(), collections, children)
}

fun loadAnimation(anim: AIAnimation): Pair<String, Animation> {
    val aiChannels = anim.mChannels()
    val channels = hashMapOf<String, AnimationChannel>()
    while (aiChannels != null && aiChannels.remaining() > 0) {
        val pair = loadChannel(AINodeAnim.create(aiChannels.get()))
        channels[pair.first] = pair.second
    }
    return anim.mName().dataString() to Animation(channels)
}

fun loadChannel(channel: AINodeAnim): Pair<String, AnimationChannel> =
    channel.mNodeName().dataString() to AnimationChannel(
        channel.mPositionKeys()?.map { key -> key.mValue().toFloat3() to key.mTime() } ?: listOf(),
        channel.mRotationKeys()?.map { key -> key.mValue().toQuaternion() to key.mTime() } ?: listOf(),
        channel.mScalingKeys()?.map { key -> key.mValue().toFloat3() to key.mTime() } ?: listOf(),
    )

fun IntArray.indexInRangeWithCondition(range: IntRange, cond: (value: Int) -> Boolean): Int? {
    range.forEach { idx -> if (cond(this[idx])) return idx }
    return null
}

fun FloatArray.indexInRangeWithLowest(range: IntRange, cond: (value: Float) -> Float?): Int? {
    var final: Int? = null
    var finalAmount = Float.MAX_VALUE
    range.forEach { idx ->
        val amount = cond(this[idx]) ?: return@forEach
        if (amount < finalAmount) {
            final = idx
            finalAmount = amount
        }
    }
    return final
}

fun AIMatrix4x4.toMat4(): Mat4 = Mat4(
    Float4(this.a1(), this.b1(), this.c1(), this.d1()),
    Float4(this.a2(), this.b2(), this.c2(), this.d2()),
    Float4(this.a3(), this.b3(), this.c3(), this.d3()),
    Float4(this.a4(), this.b4(), this.c4(), this.d4())
)

fun AIVector3D.toFloat3() = Float3(x(), y(), z())
fun AIQuaternion.toQuaternion() = Quaternion(x(), y(), z(), w())
