package io.github.daylightnebula.kevinengine

expect fun asyncTextFile(path: String, callback: (text: String) -> Unit)
expect fun asyncBinFile(path: String, callback: (bin: ByteArray) -> Unit)