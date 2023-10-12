package io.github.daylightnebula.kevinengine

import io.github.daylightnebula.kevinengine.math.Float4

data class AppInfo(
    val winName: String,
    val clearColor: Float4 = Float4(1f, 1f, 1f, 1f),
    val nativeInfo: NativeInfo = NativeInfo(),
    var width: Int = 1280,
    var height: Int = 720,
)

data class NativeInfo(
    val decorated: Boolean = true,
    val maximized: Boolean = false,
    val windowTransparent: Boolean = false
)