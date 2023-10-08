package io.github.daylightnebula.kevinengine.ecms

// todo multithreading
// todo finish system group implementation
// todo states

// Systems: code that gets run by the thread manager
fun system(callback: () -> Unit) = System(callback)
data class System(val callback: () -> Unit)
fun System.execute() = callback()
fun List<System>.executeSystems() = forEach { it.execute() }

// Module: a group of systems with some controls over events, the system group, state, etc
fun module(
    vararg updateSystems: System,
    systemGroup: String? = null,
    startSystems: List<System> = listOf(),
    stopSystems: List<System> = listOf()
) = Module(systemGroup, startSystems, listOf(*updateSystems), stopSystems)

data class Module(
    val systemGroup: String? = null,
    val startSystems: List<System> = listOf(),
    val updateSystems: List<System> = listOf(),
    val stopSystems: List<System> = listOf()
)

// System Controller
object SystemsController {
    // list of all registered modules
    private val modules = mutableListOf<Module>()
    fun register(module: Module) = modules.add(module)

    fun start() = modules.forEach { it.startSystems.executeSystems() }
    fun update() = modules.forEach { it.updateSystems.executeSystems() }
    fun stop() = modules.forEach { it.stopSystems.executeSystems() }
}