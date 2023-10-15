package io.github.daylightnebula.kevinengine.assets

import io.github.daylightnebula.kevinengine.math.Float2
import io.github.daylightnebula.kevinengine.math.Float3
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.AIVector3D
import org.lwjgl.assimp.Assimp
import java.io.File
import java.lang.IllegalArgumentException

val validAssimpFiles = listOf("gltf", "glb", "obj", "dae", "fbx")

actual fun runKAssetConversion() = recursivelyConvertKAsset(File("../assets/"))

fun recursivelyConvertKAsset(root: File) {
    // split global list into directories, asset files, and model files
    val globalList = root.listFiles() ?: return
    val directories = globalList.filter { it.isDirectory }
    val assetFiles = globalList.filter { it.extension == "kasset" }.map { it.nameWithoutExtension }
    val modelFiles = globalList.filter { validAssimpFiles.contains(it.extension) && !assetFiles.contains(it.nameWithoutExtension) }
    println("Replacing ${modelFiles.size}")

    // run directory operations
    directories.forEach { recursivelyConvertKAsset(it) }

    // load model files
    modelFiles.forEach { file ->
        // load scene and mesh
        val aScene = Assimp.aiImportFile(file.path, 0)
            ?: throw IllegalArgumentException("Unable to import file ${file.path}")

        // build list of meshes
        val meshes = Array(aScene.mNumMeshes()) { mIdx ->
            // get source mesh
            val aMesh = AIMesh.create(aScene.mMeshes()!!.get(mIdx))

            // assemble array of mesh points
            val meshPoints = Array(aMesh.mNumVertices()) { vIdx ->
                val v = aMesh.mVertices().get(vIdx)
                val n = aMesh.mNormals()?.get(vIdx) ?: AIVector3D.create()
                val t = aMesh.mTangents()?.get(vIdx) ?: AIVector3D.create()
                val uvs = aMesh.mTextureCoords(0)?.get(vIdx) ?: AIVector3D.create()

                KMeshPoint(
                    vertex = Float3(v.x(), v.y(), v.z()),
                    normal = Float3(n.x(), n.y(), n.z()),
                    uvs = Float2(uvs.x(), uvs.y()),
                    tangent = Float3(t.x(), t.y(), t.z())
                )
            }

            // assemble array of indices
            val indices = Array(aMesh.mNumFaces() * aMesh.mFaces().get(0).mNumIndices()) { 0 }
            var c = 0
            repeat(aMesh.mNumFaces()) { fIdx ->
                val face = aMesh.mFaces().get(fIdx)
                repeat(face.mNumIndices()) { iIdx ->
                    indices[c] = face.mIndices().get(iIdx)
                    c++
                }
            }

            // create final mesh object
            KMesh(meshPoints, indices)
        }

        // serialize as save KAsset
        val text = serializeKAsset(KAsset(meshes))
        File(root, "${file.nameWithoutExtension}.kasset").writeBytes(text)
    }
}