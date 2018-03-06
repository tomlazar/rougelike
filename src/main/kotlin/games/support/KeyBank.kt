package games.support

import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

val key_left = KeyCode.A
val key_right = KeyCode.D
val key_up = KeyCode.W
val key_down = KeyCode.S

val key_zoom_in = KeyCode.Q
val key_zoom_out = KeyCode.E

class KeyBank : IController {
    private val keyDown = hashMapOf(*KeyCode.values().map({ k -> Pair(k, false) }).toTypedArray())

    fun isKeyDown(code: KeyCode): Boolean {
        return keyDown[code]!!
    }

    override fun addEvents(scene: Scene) {
        scene.setOnKeyPressed({ event: KeyEvent? ->
            val c = event!!.code
            keyDown[c] = true
        })
        scene.setOnKeyReleased({ event: KeyEvent? ->
            val c = event!!.code
            keyDown[c] = false
        })
    }

    fun keyNegPos(neg: KeyCode, pos: KeyCode): Double {
        return (if (isKeyDown(neg)) -1.0 else 0.0) + (if (isKeyDown(pos)) 1.0 else 0.0)
    }
}