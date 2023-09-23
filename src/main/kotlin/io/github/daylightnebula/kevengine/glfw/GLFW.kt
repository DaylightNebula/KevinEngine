package io.github.daylightnebula.kevengine.glfw

import io.github.daylightnebula.kevengine.glfw.keyboard.Key
import io.github.daylightnebula.kevengine.glfw.keyboard.KeyEvent
import io.github.daylightnebula.kevengine.glfw.keyboard.callKeyListeners
import io.github.daylightnebula.kevengine.glfw.mouse.MouseButton
import io.github.daylightnebula.kevengine.glfw.mouse.triggerMouseButton
import io.github.daylightnebula.kevengine.glfw.mouse.triggerMouseEnter
import io.github.daylightnebula.kevengine.glfw.mouse.triggerMouseMoveEvent
import org.lwjgl.glfw.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions
import org.lwjgl.glfw.GLFWVulkan.nglfwGetRequiredInstanceExtensions
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.system.MemoryUtil.memAllocPointer
import java.awt.SystemColor.window
import java.lang.Thread.sleep
import kotlin.properties.Delegates

abstract class GLFWApp {
    abstract fun start()
    abstract fun update(delta: Float)
    abstract fun stop()
}

var frameTargetMS: Long? = null
var deltaSeconds = 0f
var windowID by Delegates.notNull<Long>()
    private set

fun stopApp() { glfwSetWindowShouldClose(windowID, true) }
fun setTargetFrameMS(targetMS: Long?) { frameTargetMS = targetMS }

fun glfwApp(
    winName: String,
    app: GLFWApp,
    width: Int = 1280,
    height: Int = 720,
    decorated: Boolean = true,
    maximized: Boolean = false
) {
    glfwSetErrorCallback { error, description -> println("GLFW error $error: $description") }

    // initialize and create window
    if (!glfwInit()) throw RuntimeException("Failed to initialize glfw!")
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
    glfwWindowHint(GLFW_DECORATED, decorated.toGLFW())
    glfwWindowHint(GLFW_MAXIMIZED, maximized.toGLFW())
    glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, true.toGLFW())
    windowID = glfwCreateWindow(width, height, winName, 0, 0)

    // setup window
    glfwMakeContextCurrent(windowID)
    glfwSwapInterval(1)

    // setup inputs
    glfwSetKeyCallback(windowID, object: GLFWKeyCallback() {
        override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
            callKeyListeners(Key.values()[scancode], KeyEvent.values()[action])
        }
    })
    glfwSetMouseButtonCallback(windowID, object: GLFWMouseButtonCallback() {
        override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
            triggerMouseButton(MouseButton.values()[button], action == GLFW_PRESS)
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