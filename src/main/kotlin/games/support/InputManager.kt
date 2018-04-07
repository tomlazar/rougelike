package games.support

import games.rougelike.objects.HUD
import games.support.interfaces.IController
import javafx.event.EventType
import javafx.scene.Scene
import javafx.scene.input.*


enum class InputBinding(vararg val input: Input<*, *>) {
    LEFT(KeyInput(KeyCode.A), KeyInput(KeyCode.LEFT)),
    RIGHT(KeyInput(KeyCode.D), KeyInput(KeyCode.RIGHT)),
    UP(KeyInput(KeyCode.W), KeyInput(KeyCode.UP)),
    DOWN(KeyInput(KeyCode.S), KeyInput(KeyCode.DOWN)),
    SET_TARGET(MouseInput(MouseButton.PRIMARY)),
    HACK(KeyInput(KeyCode.SPACE)),
    GRENADE(MouseInput(MouseButton.SECONDARY)),

    ZOOM_IN(KeyInput(KeyCode.Q)),
    ZOOM_OUT(KeyInput(KeyCode.E)),

    SPAWN_JUNKER(KeyInput(KeyCode.J)),
    PAUSE_GAMEPLAY(KeyInput(KeyCode.P));
}

enum class InputEventType {
    PRESSED, RELEASED, CLICKED
}

abstract class Input<Code : Enum<Code>, Ev : InputEvent>(private val containedObj: Code) {
    override fun equals(other: Any?) = (other is Input<*, *>) && (other).containedObj == containedObj
    override fun hashCode() = containedObj.hashCode()
    override fun toString() = containedObj.toString()
    abstract fun convertEventType(type: InputEventType): EventType<Ev>

    protected abstract fun extractCodeFromEvent(event: Ev): Code
    fun matchesEvent(event: Ev) = extractCodeFromEvent(event) == containedObj
}

class KeyInput(val key: KeyCode) : Input<KeyCode, KeyEvent>(key) {
    override fun convertEventType(type: InputEventType): EventType<KeyEvent> = when (type) {
        InputEventType.PRESSED -> KeyEvent.KEY_PRESSED
        InputEventType.RELEASED -> KeyEvent.KEY_RELEASED
        InputEventType.CLICKED -> KeyEvent.KEY_PRESSED
    }

    override fun extractCodeFromEvent(event: KeyEvent): KeyCode = event.code

    override fun toString(): String {
        return key.name.toLowerCase().capitalize()
    }
}

class MouseInput(val button: MouseButton) : Input<MouseButton, MouseEvent>(button) {
    override fun convertEventType(type: InputEventType): EventType<MouseEvent> = when (type) {
        InputEventType.PRESSED -> MouseEvent.MOUSE_PRESSED
        InputEventType.RELEASED -> MouseEvent.MOUSE_RELEASED
        InputEventType.CLICKED -> MouseEvent.MOUSE_CLICKED
    }

    override fun extractCodeFromEvent(event: MouseEvent): MouseButton = event.button

    override fun toString(): String {
        return "Mouse${
            when (button) {
                MouseButton.PRIMARY -> 1
                MouseButton.SECONDARY -> 2
                MouseButton.MIDDLE -> 3
                else -> button.ordinal
            }
        }"
    }
}

class InputManager : IController {
    private val inputActive = hashMapOf(*(
            (KeyCode.values().map { k -> KeyInput(k) }
                    + MouseButton.values().map { b -> MouseInput(b) })
                    .map { i -> Pair(i, false) }.toTypedArray()
            ))
    private var _mouseX = 0.0
    val mouseX get() = _mouseX
    private var _mouseY = 0.0
    val mouseY get() = _mouseY

    private fun isInputActive(vararg input: Input<*, *>): Boolean = input.any { input -> inputActive.contains(input) && inputActive[input]!! }
    fun isInputActive(input: InputBinding): Boolean = isInputActive(*input.input)

    fun isKeyDown(code: KeyCode) = isInputActive(KeyInput(code))
    fun isMouseDown(button: MouseButton) = isInputActive(MouseInput(button))

    override fun addEvents(target: Scene) {
        target.addEventHandler(KeyEvent.KEY_PRESSED, { event: KeyEvent? ->
            inputActive[KeyInput(event!!.code)] = true
        })
        target.addEventHandler(KeyEvent.KEY_RELEASED, { event: KeyEvent? ->
            inputActive[KeyInput(event!!.code)] = false
        })

        target.addEventHandler(MouseEvent.MOUSE_PRESSED, { event: MouseEvent? ->
            inputActive[MouseInput(event!!.button)] = true
        })
        target.addEventHandler(MouseEvent.MOUSE_RELEASED, { event: MouseEvent? ->
            inputActive[MouseInput(event!!.button)] = false
        })

        val mouseMoveLambda = { event: MouseEvent? ->
            _mouseX = InputManager.translateSceneX(event!!.sceneX)
            _mouseY = InputManager.translateSceneY(event.sceneY)
        }
        target.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveLambda)
        target.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseMoveLambda)
    }

    companion object {
        fun translateSceneX(sceneX: Double): Double {
            return sceneX + LevelManager.current.camera.sceneCamera.translateX
        }

        fun translateSceneY(sceneY: Double): Double {
            return sceneY + LevelManager.current.camera.sceneCamera.translateY - HUD.HEIGHT
        }

        private fun <Ev : InputEvent> addListener(target: Scene, input: Input<*, Ev>, inputType: InputEventType, action: () -> Unit) {
            target.addEventHandler(input.convertEventType(inputType), { event: Ev? ->
                if (input.matchesEvent(event!!))
                    action.invoke()
            })
        }

        fun addListener(target: Scene, input: InputBinding, inputType: InputEventType, action: () -> Unit) =
                input.input.forEach { input -> addListener(target, input, inputType, action) }
    }

    fun inputNegPos(neg: InputBinding, pos: InputBinding) = (if (isInputActive(neg)) -1.0 else 0.0) + (if (isInputActive(pos)) 1.0 else 0.0)
}