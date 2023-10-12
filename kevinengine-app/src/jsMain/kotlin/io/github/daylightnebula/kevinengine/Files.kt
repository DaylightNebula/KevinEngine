package io.github.daylightnebula.kevinengine

import org.khronos.webgl.Int8Array

actual fun asyncTextFile(path: String, callback: (text: String) -> Unit) {
    kotlinx.browser.window.fetch(path).then { res ->
        res.text().then { text ->
            callback(text)
        }
    }
}

actual fun asyncBinFile(path: String, callback: (bin: ByteArray) -> Unit) {
    kotlinx.browser.window.fetch(path).then { res ->
        res.arrayBuffer().then { buf ->
            callback(Int8Array(buf).unsafeCast<ByteArray>())
        }
    }
}