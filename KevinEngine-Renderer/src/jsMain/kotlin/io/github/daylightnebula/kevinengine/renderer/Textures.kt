package io.github.daylightnebula.kevinengine.renderer

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.WebGLTexture
import org.w3c.dom.Image
import org.khronos.webgl.WebGLRenderingContext as GL

val textures = mutableListOf<WebGLTexture>()
actual class Texture actual constructor (val path: String) {
    private var id = -1

    actual val width: Int = 0
    actual val height: Int = 0
    actual val isInitialized: Boolean
        get() = id != -1

    actual fun load() {
        // create texture
        val texture = gl.createTexture()
            ?: throw IllegalStateException("Could not create texture!")
        gl.bindTexture(GL.TEXTURE_2D, texture)

        // set params
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.LINEAR_MIPMAP_LINEAR)
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.LINEAR)
        gl.texImage2D(
            GL.TEXTURE_2D,
            0, GL.RGBA,
            1, 1,
            0, GL.RGBA,
            GL.UNSIGNED_BYTE,
            Uint8Array(arrayOf<Byte>(Byte.MIN_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE))
        )

        // load image
        val image = Image()
        image.onload = {
            // load texture
            gl.bindTexture(GL.TEXTURE_2D, texture)
            gl.texImage2D(
                GL.TEXTURE_2D, 0,
                GL.RGBA, GL.RGBA, GL.UNSIGNED_BYTE,
                image
            )

            // set more parameters or generate mip maps
            if (isPowerOf2(image.width) && isPowerOf2(image.height)) {
                gl.generateMipmap(GL.TEXTURE_2D);
            } else {
                gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S, GL.CLAMP_TO_EDGE);
                gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T, GL.CLAMP_TO_EDGE);
                gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.LINEAR);
            }
        }
        image.src = path

        // save texture
        id = textures.size
        textures.add(texture)
    }

    actual fun get(): Int {
        if (id == -1) load()
        return id
    }
}

fun isPowerOf2(value: Int): Boolean {
    return (value and (value - 1)) == 0;
}