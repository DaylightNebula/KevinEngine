package io.github.daylightnebula.kevinengine.renderer

import io.github.daylightnebula.kevinengine.app.AppInfo

expect fun setupRenderer(info: AppInfo)
expect fun setShader(shader: ShaderProgram?)
expect fun drawing(internal: () -> Unit)
expect fun drawVBO(vbo: VBO)