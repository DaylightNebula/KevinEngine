package io.github.daylightnebula.kevinengine

import io.github.daylightnebula.kevinengine.ecs.Module
import io.github.daylightnebula.kevinengine.ecs.SystemsController
import io.github.daylightnebula.kevinengine.ecs.World

val mainWorld = World()
var keepRunning = true
fun stopApp() { keepRunning = false }

// create a window module
expect fun window(info: AppInfo): Module

// small scale start, loop update, stop
expect fun app(start: () -> Unit, loop: (delta: Float) -> Unit, stop: () -> Unit)

// create the app
fun run(vararg modules: Module) = app(
    start = {
        modules.forEach { SystemsController.register(it) }
        SystemsController.start()
    },
    loop = { SystemsController.update() },
    stop = { SystemsController.stop() }
)