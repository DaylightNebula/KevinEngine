package io.github.daylightnebula.kevinengine

import java.io.File

actual fun asyncTextFile(path: String, callback: (text: String) -> Unit) {
//    callback(File(path).readText())
    callback(object {}.javaClass.getResource(path)?.readText() ?: "")
}

actual fun asyncBinFile(path: String, callback: (bin: ByteArray) -> Unit) {
//    callback(File(path).readBytes())
    callback(object {}.javaClass.getResource(path)?.readBytes() ?: byteArrayOf())
}