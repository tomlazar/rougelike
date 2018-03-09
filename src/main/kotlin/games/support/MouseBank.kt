package games.support

import games.rougelike.HUD
import games.rougelike.levels.GameLevel
import games.support.interfaces.IController
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent

class MouseBank : IController {
    val buttonDown = hashMapOf(*MouseButton.values().map({ k -> Pair(k, false) }).toTypedArray())
    var mouseX = 0.0
    var mouseY = 0.0

    override fun addEvents(target: Scene) {
        target.setOnMousePressed({ event: MouseEvent? ->
            buttonDown[event!!.button] = true
        })
        target.setOnMouseReleased({ event: MouseEvent? ->
            buttonDown[event!!.button] = false
        })

        val mouseMoveLambda = { event: MouseEvent? ->
            mouseX = translateSceneX(event!!.sceneX)
            mouseY = translateSceneY(event.sceneY)
        }
        target.setOnMouseMoved(mouseMoveLambda)
        target.setOnMouseDragged(mouseMoveLambda)
    }

    fun translateSceneX(sceneX: Double): Double {
        return sceneX + GameLevel.camera.sceneCamera.translateX
    }

    fun translateSceneY(sceneY: Double): Double {
        return sceneY + GameLevel.camera.sceneCamera.translateY - HUD.HEIGHT
    }
}