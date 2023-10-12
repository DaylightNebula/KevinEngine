package io.github.daylightnebula.kevinengine

import io.github.daylightnebula.kevinengine.ecs.Query
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.callKeyListeners
import io.github.daylightnebula.kevinengine.mouse.MouseButton
import io.github.daylightnebula.kevinengine.mouse.triggerMouseButton
import io.github.daylightnebula.kevinengine.mouse.triggerMouseEnter
import io.github.daylightnebula.kevinengine.mouse.triggerMouseMoveEvent
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.glfw.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import java.lang.Thread.sleep
import kotlin.properties.Delegates

var frameTargetMS: Long? = null
var deltaSeconds = 0f
var windowID by Delegates.notNull<Long>()
    private set

fun setTargetFrameMS(targetMS: Long?) { frameTargetMS = targetMS }

actual fun window(info: AppInfo) = module(
    startSystems = listOf(system {
        glfwSetErrorCallback { error, description -> println("GLFW error $error: $description") }

        // initialize and create window
        if (!glfwInit()) throw RuntimeException("Failed to initialize glfw!")
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_DECORATED, info.nativeInfo.decorated.toGLFW())
        glfwWindowHint(GLFW_MAXIMIZED, info.nativeInfo.maximized.toGLFW())
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, info.nativeInfo.windowTransparent.toGLFW())
        glfwWindowHint(GLFW_SAMPLES, 4); // 4x antialiasing
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // We want OpenGL 3.3
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, true.toGLFW()); // To make Mac happy; should not be needed
        windowID = glfwCreateWindow(info.width, info.height, info.winName, 0, 0)

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
    }),
    updateSystems = listOf(system {
        glfwSwapBuffers(windowID)
        glfwPollEvents()
        if (glfwWindowShouldClose(windowID)) stopApp()
    }),
    stopSystems = listOf(system {
        // remove window
        glfwFreeCallbacks(windowID)
        glfwDestroyWindow(windowID)
        glfwTerminate()
    })
)

actual fun app(start: () -> Unit, loop: (delta: Float) -> Unit, stop: () -> Unit) {
    start()
    while(keepRunning) {
        Query.clearQueries()

        // get starting time and then call update
        val timer = System.currentTimeMillis()
        loop(deltaSeconds)

        // get ms took to run frame
        val elapsedMS = (System.currentTimeMillis() - timer).coerceAtLeast(0)

        // wait if necessary
        frameTargetMS?.let { target ->
            val waitTime = (target - elapsedMS).coerceAtLeast(0)
            sleep(waitTime)
        }

//        println("Test ${Quaternionf().lookAlong(Vector3f(-4f, -3f, -3f), Vector3f(0f, 1f, 0f))}")

        // if 1 ms has not passed, wait 1 ms
        if (System.currentTimeMillis() - timer < 1) sleep(1)

        // update delta time
        deltaSeconds = (System.currentTimeMillis() - timer).coerceAtLeast(0) / 1000f
    }
}

// help extensions functions
fun Boolean.toGLFW(): Int = if (this) GLFW_TRUE else GLFW_FALSE