import dev.romainguy.kotlin.math.Float4
import dev.romainguy.kotlin.math.Mat4
import io.github.daylightnebula.kevinengine.app.App
import io.github.daylightnebula.kevinengine.app.AppInfo
import io.github.daylightnebula.kevinengine.app.app
import io.github.daylightnebula.kevinengine.app.scale
import io.github.daylightnebula.kevinengine.renderer.*
import kotlin.test.Test

class QuadTest {
    val quadPositionsBuffer = genBuffer(-1f, -1f, 0f, 1f, -1f, 0f, 1f, 1f, 0f, -1f, 1f, 0f)
    val scaleMatrix = Mat4.identity().scale(0.5f)
    val shader = ShaderProgram(
        "base",
        "/quad_vert.glsl",
        "/quad_frag.glsl",
        listOf("matrix")
    )

    val info = AppInfo(
        "KevinEngine-RendererTester",
        Float4(0f, 0f, 0f, 1f)
    )

    @Test
    fun testQuads() = app(info, object : App {
        override fun start() {
            setupRenderer(info)
            setShader(shader)
        }

        override fun update(delta: Float) = drawing {
            shader.setUniformMat4("matrix", scaleMatrix)
            attachBuffer(0, quadPositionsBuffer)
            drawBufferRaw(quadPositionsBuffer, RenderShapeType.QUADS)
            detachBufferIndex(0)
        }

        override fun stop() {}
    })
}