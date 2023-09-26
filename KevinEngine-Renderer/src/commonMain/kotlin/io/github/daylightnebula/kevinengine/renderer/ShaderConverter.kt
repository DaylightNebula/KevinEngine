package io.github.daylightnebula.kevinengine.renderer

fun convertShaderWebGL(text: String, type: ShaderType): String {
    val builder = StringBuilder()
    text.lines().forEach { line ->
        val tokens = line.trim().split(" ").toMutableList()
        var line: String = when(tokens.first()) {
            "#version" -> ""
            "layout(location" -> { tokens.removeAt(0); tokens.removeAt(0); tokens.removeAt(0); tokens[0] = "attribute"; tokens.joinToString(separator = " ") }
            "in" -> when (type) {
                ShaderType.VERTEX -> { tokens[0] = "attribute"; tokens.joinToString(separator = " ") }
                ShaderType.FRAGMENT -> { tokens[0] = "varying lowp"; tokens.joinToString(separator = " ") }
            }
            "out" -> when(type) {
                ShaderType.VERTEX -> { tokens[0] = "varying lowp"; tokens.joinToString(separator = " ") }
                ShaderType.FRAGMENT -> ""
            }
            "outColor" -> { tokens[0] = "gl_FragColor"; tokens.joinToString(separator = " ") }
            else -> line.trim()
        }
        if (line.isNotBlank()){
            // edge case for textures
            line = line.replace("texture(", "texture2D(")
            builder.append("$line\n")
        }
    }
    return builder.toString()
}