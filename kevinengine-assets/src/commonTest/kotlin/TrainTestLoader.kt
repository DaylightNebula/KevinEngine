import io.github.daylightnebula.kevinengine.AppInfo
import io.github.daylightnebula.kevinengine.assets.Model
import io.github.daylightnebula.kevinengine.assets.ObjModel
import io.github.daylightnebula.kevinengine.assets.assets
import io.github.daylightnebula.kevinengine.assets.gltf.GltfModel
import io.github.daylightnebula.kevinengine.assets.modelShader
import io.github.daylightnebula.kevinengine.components.TransformComponent
import io.github.daylightnebula.kevinengine.components.VisibilityComponent
import io.github.daylightnebula.kevinengine.ecs.entity
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.renderer.Camera
import io.github.daylightnebula.kevinengine.renderer.Material
import io.github.daylightnebula.kevinengine.renderer.Texture
import io.github.daylightnebula.kevinengine.renderer.renderer
import io.github.daylightnebula.kevinengine.run
import io.github.daylightnebula.kevinengine.stopApp
import io.github.daylightnebula.kevinengine.window
import kotlin.test.Test

class TrainTestLoader {
    val info = AppInfo("ObjTest", clearColor = Float4(0f, 0f, 0f, 1f))
    val texture = Texture("/SimpleTrains_Texture_01.png")

    @Test
    fun test() = run(
        window(info), renderer(info), assets(),
        module(
            startSystems = listOf(system {
                addKeyListener("esc_close") { key, event ->
                    if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
                }

                // create cube
                entity(
                    TransformComponent(scale = Float3(0.25f)),
                    VisibilityComponent(),
//                    Model("SM_Veh_Carriage_Coal_01.obj"),
//                    Material(modelShader, hashMapOf("diffuse" to Texture("/SimpleTrains_Texture_01.png")
                    Model("barbarian.gltf"),
                    Material(modelShader, hashMapOf("diffuse" to Texture("/barbarian_texture.png")
                ))).spawn()

                // create camera
                entity(
                    Camera(45f, 1280f / 720f, 0.1f, 100f),
                    TransformComponent(position = Float3(0f, -0.25f, -1f)).apply { lookAt(Float3(0f, -0.25f, 0f)) }
                ).spawn()
            })
        )
    )
}