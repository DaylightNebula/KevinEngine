import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import io.github.daylightnebula.kevinengine.app.App
import io.github.daylightnebula.kevinengine.app.AppInfo
import io.github.daylightnebula.kevinengine.app.app
import io.github.daylightnebula.kevinengine.app.scale
import io.github.daylightnebula.kevinengine.renderer.*
import kotlin.test.Test

class TexturedQuadTest {
    val buffers = bufferCollection(
        RenderShapeType.QUADS,
        metadata("positions", 0, 3) to genBuffer(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            1f, 1f, 0f,
            -1f, 1f, 0f
        ),
        metadata("colors", 1, 2) to genBuffer(
            0f, 1f,
            1f, 1f,
            1f, 0f,
            0f, 0f
        )
    )
    val scaleMatrix = Mat4.identity().scale(0.5f)
    val shader = ShaderProgram(
        "base",
        "/texquad_vert.glsl",
        "/texquad_frag.glsl",
        listOf("matrix", "tex0")
    )
    val texture = Texture("/flowers.jpg")

    val info = AppInfo(
        "KevinEngine-RendererTesterTextured",
        Float4(0f, 0f, 0f, 1f)
    )

    @Test
    fun testTexturedQuad() = app(info, object: App {
        override fun start() {
            setupRenderer(info)
            setShader(shader)
        }

        override fun update(delta: Float) = drawing {
            shader.setUniformMat4("matrix", scaleMatrix)
            shader.setUniformTex("tex0", texture)
            buffers.render()
        }

        override fun stop() {}
    })
}