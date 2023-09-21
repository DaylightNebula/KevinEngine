package io.github.daylightnebula.kevengine.glfw.mouse

sealed class MouseEvent {
    class MouseEnter: MouseEvent()
    class MouseExit: MouseEvent()
    data class MouseMove(val newX: Float, val newY: Float, val dx: Float, val dy: Float): MouseEvent()
    data class MouseButtonPressed(val button: MouseButton): MouseEvent()
    data class MouseButtonReleased(val button: MouseButton): MouseEvent()
}