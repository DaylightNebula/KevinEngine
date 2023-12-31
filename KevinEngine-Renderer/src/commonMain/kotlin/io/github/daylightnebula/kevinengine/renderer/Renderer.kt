package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.AppInfo
import io.github.daylightnebula.kevinengine.components.TransformComponent
import io.github.daylightnebula.kevinengine.components.VisibilityComponent
import io.github.daylightnebula.kevinengine.ecs.*
import io.github.daylightnebula.kevinengine.math.*

// mesh
// todo generate bone tree similar to mesh node tree
data class MeshNode(
    val name: String,
    val transformation: Mat4,
    val collections: List<BufferCollection>,
    val children: List<MeshNode>
)
data class Animation(val channels: HashMap<String, AnimationChannel>)
class AnimationChannel(
    val positions: List<Pair<Float3, Double>>,
    val rotations: List<Pair<Quaternion, Double>>,
    val scales: List<Pair<Float3, Double>>,
)
data class Bone(val id: Int, val offset: Mat4, private var matrix: Mat4 = Mat4.identity())
data class Mesh(val root: MeshNode, val bones: HashMap<String, Bone>, val animations: HashMap<String, Animation>): Component

// components
val meshQuery = Query(Material::class, Mesh::class, TransformComponent::class, VisibilityComponent::class)
data class Material(val shader: ShaderProgram, val map: HashMap<String, Any>): Component
fun mesh(vararg collections: BufferCollection) = Mesh(MeshNode("", Mat4.identity(), listOf(*collections), listOf()), hashMapOf(), hashMapOf())

// cameras
val cameraQuery = Query(Camera::class, TransformComponent::class)
data class Camera(val fov: Float, val aspectRatio: Float, val near: Float, val far: Float): Component

fun renderer(info: AppInfo) = module(
    startSystems = listOf(system { setupRenderer(info) }),
    updateSystems = listOf(system {
        // get camera
        val camera = cameraQuery.query().firstOrNull()?.components?.filter { it is Camera || it is TransformComponent }
            ?: throw IllegalStateException("No camera created, cannot render!")
        val viewMatrix = (camera[1] as TransformComponent).toMatrix()
        val cameraComp = camera[0] as Camera
        val perspectiveMatrix = perspective(cameraComp.fov, cameraComp.aspectRatio, cameraComp.near, cameraComp.far)

        startRender()
        meshQuery.query().forEach {
            // filter components
            val list = it.components.filter { it is Material || it is Mesh || it is TransformComponent || it is VisibilityComponent }

            // stop if not visible
            if (!(list[3] as VisibilityComponent).visibility) return@forEach

            // calculate transform
            val transform = (list[2] as TransformComponent).toMatrix()
            val matrix = perspectiveMatrix * viewMatrix * transform

            // get buffer collection and shader
            val mesh = (list[1] as Mesh)
            val rootNode = mesh.root
            val collections = rootNode.collections

            // apply material (uniforms)
            val material = (list[0] as Material)
            val materialMap = material.map
            val shader = material.shader
            materialMap.forEach { (name, obj) ->
                when(obj) {
                    is Float -> shader.setUniformFloat(name, obj)
                    is Float2 -> shader.setUniformVec2(name, obj)
                    is Float3 -> shader.setUniformVec3(name, obj)
                    is Float4 -> shader.setUniformVec4(name, obj)
                    is Mat4 -> shader.setUniformMat4(name, obj)
                    is Texture -> shader.setUniformTex(name, obj)
                }
            }

            // generate bone matrices
//            val boneMatArray = FloatArray(16 * 100)
//            val animation = mesh.animations["2H_Melee_Idle"]!!
//            mesh.bones.values.forEachIndexed { idx, bone ->
////                val channel = animation.channels[bone.name]!!
////                val position = channel.positions.first().first
////                val rotation = channel.rotations.first().first
////                val scale = channel.scales.first().first
////                val matrix = scale(scale) * rotation(rotation) * translation(position)
//
////                val array = bone.offset.toFloatArrayColumnAligned()
////                val array = matrix.toFloatArrayColumnAligned()
////                val array = (matrix * bone.offset).toFloatArrayColumnAligned()
//                val array = Mat4.identity().toFloatArrayColumnAligned()
//                repeat(16) { idx2 ->
//                    boneMatArray[idx * 16 + idx2] = array[idx2]
//                }
//            }
//            shader.setUniformMat4Array("bones[0]", boneMatArray)

            // do draw
            renderMeshNode(shader, rootNode, matrix)
        }
        endRender()
    })
)

fun renderMeshNode(shader: ShaderProgram, node: MeshNode, parentMatrix: Mat4) {
    // set matrix
    val matrix = parentMatrix * node.transformation
    shader.setUniformMat4("mvp", matrix)

    // render collections
    node.collections.forEach { it.render(shader) }

    // render children
    node.children.forEach { renderMeshNode(shader, it, matrix) }
}

expect fun setupRenderer(info: AppInfo)
expect fun startRender()
expect fun endRender()
expect fun drawArrays(type: RenderShapeType, count: Int)
expect fun drawIndexed(type: RenderShapeType, count: Int)

enum class RenderShapeType { TRIANGLES, QUADS }

//expect fun attachBuffer(index: Int, metadata: BufferMetadataID, buffer: Buffer)
//expect fun drawAttachedRaw(shader: ShaderProgram, count: Int, type: RenderShapeType)
//expect fun detachBufferIndex(index: Int)