package io.github.daylightnebula.kevinengine.app.mouse

// some publicly available values values
var mouseX = 0f
    private set
var mouseY = 0f
    private set
var isMouseInside = false
    private set

// buffer and map of listeners
private val mouseButtonBuffer = BooleanArray(10)
private val mouseListeners: MutableMap<String, (event: MouseEvent) -> Unit> = mutableMapOf(
    "UPDATE_INTERNAL_MOUSE" to { event ->
        when(event) {
            is MouseEvent.MouseButtonPressed -> mouseButtonBuffer[event.button.ordinal] = true
            is MouseEvent.MouseButtonReleased -> mouseButtonBuffer[event.button.ordinal] = false
            is MouseEvent.MouseEnter -> isMouseInside = true
            is MouseEvent.MouseExit -> isMouseInside = false
            is MouseEvent.MouseMove -> {} // handled by update function
        }
    }
)

fun addMouseListener(name: String, callback: (event: MouseEvent) -> Unit) { mouseListeners[name] = callback }
fun removeMouseListener(name: String) = mouseListeners.remove(name)

private fun callListeners(event: MouseEvent) = mouseListeners.forEach { entry ->
    val name = entry.key
    val callback = entry.value
    try {
        callback(event)
    } catch (ex: Exception) {
        println("Error occurred in mouse event callback $name")
        ex.printStackTrace()
    }
}

internal fun triggerMouseMoveEvent(newX: Float, newY: Float) {
    // update trackers
    val dx = newX - mouseX
    val dy = newY - mouseY
    mouseX = newX
    mouseY = newY

    // call listeners
    callListeners(MouseEvent.MouseMove(newX, newY, dx, dy))
}

internal fun triggerMouseButton(button: MouseButton, pressed: Boolean) = callListeners(
    when (pressed) {
        true -> MouseEvent.MouseButtonPressed(button)
        false -> MouseEvent.MouseButtonReleased(button)
    }
)

internal fun triggerMouseEnter(enter: Boolean) = callListeners(
    when(enter) {
        true -> MouseEvent.MouseEnter()
        false -> MouseEvent.MouseExit()
    }
)