import io.github.daylightnebula.kevinengine.renderer.ShaderType
import io.github.daylightnebula.kevinengine.renderer.convertShaderWebGL
import kotlin.test.Test

class ShaderConverterTest {
    @Test
    fun test() {
        val text = convertShaderWebGL("#version 330 core\n" +
                "layout(location = 0) in vec3 vertexPosition_modelspace;\n" +
                "layout(location = 1) in vec2 vertexUV;\n" +
                "\n" +
                "out vec2 UV;\n" +
                "\n" +
                "uniform mat4 matrix;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = matrix * vec4(vertexPosition_modelspace, 1);\n" +
                "    UV = vertexUV;\n" +
                "}", ShaderType.VERTEX)
        val success = text == "attribute vec3 vertexPosition_modelspace;\n" +
                "attribute vec2 vertexUV;\n" +
                "uniform mat4 matrix;\n" +
                "varying vec2 UV;\n" +
                "void main() {\n" +
                "gl_Position = matrix * vec4(vertexPosition_modelspace, 1);\n" +
                "UV = vertexUV;\n" +
                "}\n"

        println(text)
        if (success) println("Succeeded conversion")
        else println("Failed conversion")
    }
}