package io.github.daylightnebula.kevinengine.app.keyboard

// buffer of all keys pressed states
private val keyBuffer = BooleanArray(256)

// create a list of keyboard event listeners, with the first to update the above buffer
// they are a string id, mapped to a boolean flag to determine if they received repeat events and a callback
private val listeners: MutableMap<String, Pair<Boolean, (key: Key, event: KeyEvent) -> Unit>> = mutableMapOf(
    "UPDATE_INTERNAL_KEYS" to (false to { key, event ->
        when (event) {
            KeyEvent.Released ->
                keyBuffer[key.ordinal] = false
            KeyEvent.Pressed ->
                keyBuffer[key.ordinal] = true
            KeyEvent.Repeated -> {}
        }
    })
)

// internal function to call listeners
internal fun callKeyListeners(key: Key, event: KeyEvent) = listeners.forEach { name, (repeatFlag, callback) ->
    if (event != KeyEvent.Repeated || repeatFlag)
        try {
            callback(key, event)
        } catch (ex: Exception) {
            System.err.println("Error occurred in keyboard callback $name")
            ex.printStackTrace()
        }
}

// functions to manipulate event listeners
fun addKeyListener(name: String, receiveRepeat: Boolean = false, listener: (key: Key, event: KeyEvent) -> Unit) { listeners[name] = receiveRepeat to listener }
fun removeKeyListener(name: String) = listeners.remove(name)

// functions to interact with the above buffer
fun isKeyPressed(key: Key) = keyBuffer[key.ordinal]