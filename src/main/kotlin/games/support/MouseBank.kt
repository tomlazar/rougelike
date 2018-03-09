package games.support

import games.rougelike.objects.HUD
import games.rougelike.levels.GameLevel
import games.support.interfaces.IController
import games.support.interfaces.IGameObject
import javafx.scene.Scene
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent

val button_setTarget = MouseButton.PRIMARY

class MouseBank() : IController {
    val buttonDown = hashMapOf(*MouseButton.values().map({ k -> Pair(k, false) }).toTypedArray())
    var mouseX = 0.0
    var mouseY = 0.0

    override fun addEvents(target: Scene) {
        target.addEventHandler(MouseEvent.MOUSE_PRESSED, { event: MouseEvent? ->
            buttonDown[event!!.button] = true
        })
        target.addEventHandler(MouseEvent.MOUSE_RELEASED, { event: MouseEvent? ->
            buttonDown[event!!.button] = false
        })

        val mouseMoveLambda = { event: MouseEvent? ->
            mouseX = translateSceneX(event!!.sceneX)
            mouseY = translateSceneY(event.sceneY)
        }
        target.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveLambda)
        target.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseMoveLambda)
    }

    companion object {
        fun translateSceneX(sceneX: Double): Double {
            return sceneX + GameLevel.camera.sceneCamera.translateX
        }

        fun translateSceneY(sceneY: Double): Double {
            return sceneY + GameLevel.camera.sceneCamera.translateY - HUD.HEIGHT
        }

        fun makeButtonListener(button: MouseButton, obj: IGameObject, action: () -> Unit): (MouseEvent?) -> Unit {
            return { event: MouseEvent? ->
                if (event!!.button == button && obj.getBoundingBox().contains(translateSceneX(event.sceneX), translateSceneY(event.sceneY)))
                    action.invoke()
            }
        }
    }
}