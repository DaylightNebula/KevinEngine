import io.github.daylightnebula.kevinengine.*
import io.github.daylightnebula.kevinengine.components.TransformComponent
import io.github.daylightnebula.kevinengine.components.VisibilityComponent
import io.github.daylightnebula.kevinengine.ecs.*
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.scale
import io.github.daylightnebula.kevinengine.renderer.*
import kotlin.test.Test

class QuadTest {
    val scaleMatrix = scale(0.5f)
    val shader = ShaderProgram(
        "base",
        "/quad_vert.glsl",
        "/quad_frag.glsl"
    )
    val buffers = bufferCollection(
        shader,
        RenderShapeType.QUADS,
        metadata("positions", 3) to genBuffer(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
        ),
        metadata("colors", 3) to genBuffer(
            0.393f,  0.621f,  0.362f,
            0.673f,  0.211f,  0.457f,
            0.820f,  0.883f,  0.371f,
            0.982f,  0.099f,  0.879f
        )
    )

    val info = AppInfo(
        "kevinengine-rendererTester",
        Float4(0f, 0f, 0f, 1f)
    )

    @Test
    fun quadTest() = run(
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
                    Mesh(buffers),
                    Material(hashMapOf())
                ).spawn()

                entity(
                    Camera(45f, 1280f/720f, 0.1f, 100f),
                    TransformComponent(position = Float3(0f, 0f, -1f))
                ).spawn()
            })
        )
    )
}