package io.github.daylightnebula.kevinengine.app

data class AppInfo(val winName: String, val nativeInfo: NativeInfo)
data class NativeInfo(
    val initWidth: Int = 1280,
    val initHeight: Int = 720,
    val decorated: Boolean = true,
    val maximized: Boolean = false,
    val windowTransparent: Boolean = false
)