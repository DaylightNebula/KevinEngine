package io.github.daylightnebula.kevinengine.renderer.tests

import io.github.daylightnebula.kevinengine.*
import io.github.daylightnebula.kevinengine.components.TransformComponent
import io.github.daylightnebula.kevinengine.components.VisibilityComponent
import io.github.daylightnebula.kevinengine.ecs.entity
import io.github.daylightnebula.kevinengine.ecs.module
import io.github.daylightnebula.kevinengine.ecs.system
import io.github.daylightnebula.kevinengine.keyboard.Key
import io.github.daylightnebula.kevinengine.keyboard.KeyEvent
import io.github.daylightnebula.kevinengine.keyboard.addKeyListener
import io.github.daylightnebula.kevinengine.math.Float3
import io.github.daylightnebula.kevinengine.math.Float4
import io.github.daylightnebula.kevinengine.renderer.Camera
import io.github.daylightnebula.kevinengine.renderer.PrimitiveMaterial
import io.github.daylightnebula.kevinengine.renderer.PrimitiveMesh
import io.github.daylightnebula.kevinengine.renderer.models.OBJMesh
import io.github.daylightnebula.kevinengine.renderer.renderer

class ObjTest {
    val info = AppInfo("ObjTest", clearColor = Float4(0f, 0f, 0f, 1f))
    fun main() = run(
        window(info),
        renderer(info),
        module(
            startSystems = listOf(system {
                addKeyListener("esc_close") { key, event ->
                    if (key == Key.KEY_ESCAPE && event == KeyEvent.Released) stopApp()
                }

                // create cube
                entity(
                    TransformComponent(),
                    VisibilityComponent(),
                    OBJMesh("/SM_Veh_Carriage_Logs_01.obj"),
                    PrimitiveMaterial(hashMapOf())
                ).spawn()

                // create camera
                entity(
                    Camera(45f, 1280f/720f, 0.1f, 100f),
                    TransformComponent(position = Float3(-4f, -3f, -3f)).apply { lookAt(Float3()) }
                ).spawn()
            })
        )
    )
}