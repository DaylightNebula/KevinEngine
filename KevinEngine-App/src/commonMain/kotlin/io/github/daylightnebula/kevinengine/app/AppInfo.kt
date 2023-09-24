package io.github.daylightnebula.kevinengine.app

import dev.romainguy.kotlin.math.Float4

data class AppInfo(
    val winName: String,
    val clearColor: Float4 = Float4(1f, 1f, 1f, 1f),
    val nativeInfo: NativeInfo = NativeInfo()
)

data class NativeInfo(
    val initWidth: Int = 1280,
    val initHeight: Int = 720,
    val decorated: Boolean = true,
    val maximized: Boolean = false,
    val windowTransparent: Boolean = false
)