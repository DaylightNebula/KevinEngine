package io.github.daylightnebula.kevinengine.app

import io.github.daylightnebula.kevinengine.app.keyboard.Key
import io.github.daylightnebula.kevinengine.app.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.app.keyboard.callKeyListeners
import io.github.daylightnebula.kevinengine.app.mouse.MouseButton
import io.github.daylightnebula.kevinengine.app.mouse.triggerMouseButton
import io.github.daylightnebula.kevinengine.app.mouse.triggerMouseEnter
import io.github.daylightnebula.kevinengine.app.mouse.triggerMouseMoveEvent
import org.lwjgl.glfw.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import java.lang.Thread.sleep
import kotlin.properties.Delegates

actual interface App {
    actual fun start()
    actual fun update(delta: Float)
    actual fun stop()
}

var frameTargetMS: Long? = null
var deltaSeconds = 0f
var windowID by Delegates.notNull<Long>()
    private set

actual fun stopApp() { glfwSetWindowShouldClose(windowID, true) }
fun setTargetFrameMS(targetMS: Long?) { frameTargetMS = targetMS }

actual fun app(
    info: AppInfo,
    app: App
) {
    glfwSetErrorCallback { error, description -> println("GLFW error $error: $description") }

    // initialize and create window
    if (!glfwInit()) throw RuntimeException("Failed to initialize glfw!")
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
    glfwWindowHint(GLFW_DECORATED, info.nativeInfo.decorated.toGLFW())
    glfwWindowHint(GLFW_MAXIMIZED, info.nativeInfo.maximized.toGLFW())
    glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, info.nativeInfo.windowTransparent.toGLFW())
    windowID = glfwCreateWindow(info.nativeInfo.initWidth, info.nativeInfo.initHeight, info.winName, 0, 0)

    // setup window
    glfwMakeContextCurrent(windowID)
    glfwSwapInterval(1)

    // setup inputs
    glfwSetKeyCallback(windowID, object: GLFWKeyCallback() {
        override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
            if (scancode < Key.entries.size) callKeyListeners(Key.entries[scancode], KeyEvent.entries[action])
        }
    })
    glfwSetMouseButtonCallback(windowID, object: GLFWMouseButtonCallback() {
        override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
            triggerMouseButton(MouseButton.entries[button], action == GLFW_PRESS)
        }
    })
    glfwSetCursorPosCallback(windowID, object: GLFWCursorPosCallback() {
        override fun invoke(window: Long, xpos: Double, ypos: Double) {
            // get window size
            val width = IntArray(1)
            val height = IntArray(1)
            glfwGetWindowSize(windowID, width, height)

            // call mouse move
            triggerMouseMoveEvent(xpos.toFloat() / width.first(), ypos.toFloat() / height.first())
        }
    })
    glfwSetCursorEnterCallback(windowID, object: GLFWCursorEnterCallback() {
        override fun invoke(window: Long, entered: Boolean) {
            triggerMouseEnter(entered)
        }
    })

    // show window
    glfwShowWindow(windowID)

    // call app start
    app.start()

    // create loop
    while(!glfwWindowShouldClose(windowID)) {
        // start timer
        val timer = System.currentTimeMillis()

        // get start time and call update
        app.update(deltaSeconds)

        // get ms took to run frame
        val elapsedMS = (System.currentTimeMillis() - timer).coerceAtLeast(0)

        // wait if necessary
        frameTargetMS?.let { target ->
            val waitTime = (target - elapsedMS).coerceAtLeast(0)
            sleep(waitTime)
        }

        // update delta time
        deltaSeconds = (System.currentTimeMillis() - timer).coerceAtLeast(0) / 1000f

        // poll events
        glfwSwapBuffers(windowID)
        glfwPollEvents()
    }

    // call stop
    app.stop()

    // remove window
    glfwFreeCallbacks(windowID)
    glfwDestroyWindow(windowID)
    glfwTerminate()
}

// help extensions functions
fun Boolean.toGLFW(): Int = if (this) GLFW_TRUE else GLFW_FALSE