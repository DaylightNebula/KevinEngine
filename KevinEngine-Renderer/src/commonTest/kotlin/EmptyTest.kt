import io.github.daylightnebula.kevinengine.*
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.renderer.renderer
import kotlin.test.Test

class EmptyTest {
    private val info = AppInfo("Blank Test", Float4(0f, 0f, 0f, 1f))

    @Test
    fun emptyTest() = run(window(info), renderer(info))
//    fun main() = app(info, object: App {
//        override fun start() {
//            addKeyListener("esc_close") { key, event ->
//                if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
//            }
//
//            setupRenderer(info)
//        }
//        override fun update(delta: Float) = drawing {}
//        override fun stop() {}
//    })
}