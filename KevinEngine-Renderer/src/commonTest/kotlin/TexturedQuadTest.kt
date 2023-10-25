import io.github.daylightnebula.kevinengine.AppInfo
import io.github.daylightnebula.kevinengine.components.TransformComponent
import io.github.daylightnebula.kevinengine.components.VisibilityComponent
import io.github.daylightnebula.kevinengine.ecs.*
import io.github.daylightnebula.kevinengine.info
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.scale
import io.github.daylightnebula.kevinengine.renderer.*
import io.github.daylightnebula.kevinengine.stopApp
import io.github.daylightnebula.kevinengine.window
import kotlin.test.Test

class TexturedQuadTest {
    val scaleMatrix = scale(0.5f)
    val shader = ShaderProgram(
        "base",
        "/texquad_vert.glsl",
        "/texquad_frag.glsl"
    )
    val texture = Texture("/flowers.jpg")
    val buffers = indexedCollection(
        shader, RenderShapeType.TRIANGLES,
        shortArrayOf(0, 1, 2, 0, 2, 3),
        metadata("vertexPosition_modelspace", 3) to genBuffer(
            -1f, -1f, 0f,
            -1f, 1f, 0f,
            1f, 1f, 0f,
            1f, -1f, 0f
        ),
        metadata("vertexUV", 2) to genBuffer(
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f
        )
    )

    val info = AppInfo(
        "kevinengine-rendererTesterTextured",
        Float4(0f, 0f, 0f, 1f)
    )

    @Test
    fun test() = io.github.daylightnebula.kevinengine.run(
        window(info),
        renderer(info),
        module(
            startSystems = listOf(system {
                addKeyListener("esc_close") { key, event ->
                    if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
                }

                entity(
                    TransformComponent(scale = Float3(0.5f)),
                    VisibilityComponent(),
                    mesh(buffers),
                    Material(hashMapOf("tex0" to texture))
                ).spawn()

                entity(
                    Camera(45f, 1280f/720f, 0.1f, 100f),
                    TransformComponent(position = Float3(0f, 0f, -1f))
                ).spawn()
            })
        )
    )
}