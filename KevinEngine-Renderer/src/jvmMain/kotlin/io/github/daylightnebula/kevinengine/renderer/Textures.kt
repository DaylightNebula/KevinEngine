package io.github.daylightnebula.kevinengine.renderer

import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import javax.imageio.ImageIO

actual fun provideFileBytes(path: String) =
    object {}.javaClass.getResource(path)?.readBytes()
        ?: throw IllegalArgumentException("Could not find anything on path $path")

actual class Texture(val image: BufferedImage) {
    private var id = -1

    // constructor that turns the path into an image
    actual constructor(path: String): this(ImageIO.read(Texture::class.java.getResource(path)))

    actual val width: Int = image.width
    actual val height: Int = image.height
    actual val isInitialized: Boolean
        get() = id != -1

    private fun getImageBytes(): ByteBuffer {
        val pixels = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, pixels, 0, image.width)
        val buffer = ByteBuffer.allocateDirect(image.width * image.height * 4)

        for (h in 0..< image.height) {
            for (w in 0..< image.width) {
                val pixel: Int = pixels.get(h * image.width + w)
                buffer.put((pixel shr 16 and 0xFF).toByte())
                buffer.put((pixel shr 8 and 0xFF).toByte())
                buffer.put((pixel and 0xFF).toByte())
                buffer.put((pixel shr 24 and 0xFF).toByte())
            }
        }

        return buffer.flip()
    }

    actual fun load() {
        id = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, id)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, getImageBytes())
        GL30.glGenerateMipmap(GL_TEXTURE_2D)
    }

    actual fun get(): Int {
        if (!isInitialized) load()
        return id
    }
}