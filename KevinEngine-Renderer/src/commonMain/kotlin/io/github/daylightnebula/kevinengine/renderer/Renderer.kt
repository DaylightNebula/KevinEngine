package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.AppInfo
import io.github.daylightnebula.kevinengine.components.TransformComponent
import io.github.daylightnebula.kevinengine.components.VisibilityComponent
import io.github.daylightnebula.kevinengine.ecs.*
import io.github.daylightnebula.kevinengine.math.*

val meshQuery = Query(PrimitiveMaterial::class, PrimitiveMesh::class, TransformComponent::class, VisibilityComponent::class)
data class PrimitiveMesh(val collection: BufferCollection): Component
data class PrimitiveMaterial(val map: HashMap<String, Any>): Component

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
            val list = it.components.filter { it is PrimitiveMaterial || it is PrimitiveMesh || it is TransformComponent || it is VisibilityComponent }

            // stop if not visible
            if (!(list[3] as VisibilityComponent).visibility) return@forEach

            // calculate transform
            val transform = (list[2] as TransformComponent).toMatrix()
            val matrix = perspectiveMatrix * viewMatrix * transform

            // get buffer collection and shader
            val collection = (list[1] as PrimitiveMesh).collection
            val shader = collection.shader
            shader.setUniformMat4("mvp", matrix)

            // apply material
            val materialMap = (list[0] as PrimitiveMaterial).map
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

            // do draw
            collection.render()
        }
        endRender()
    })
)

expect fun setupRenderer(info: AppInfo)
expect fun startRender()
expect fun endRender()

enum class RenderShapeType { TRIANGLES, QUADS }

expect fun attachBuffer(index: Int, metadata: BufferMetadata, buffer: Buffer)
expect fun drawAttachedRaw(shader: ShaderProgram, count: Int, type: RenderShapeType)
expect fun detachBufferIndex(index: Int)