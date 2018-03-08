package games.rougelike

import games.support.Grid
import games.support.IController
import games.support.IGameObject
import games.support.*
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color

class Player : IGameObject() {
    val radius = 15.0
    override var height: Double = radius * 2
    override var width: Double = radius * 2
    override var x: Double = 0.0
    override var y: Double = 0.0

    override fun render(gc: GraphicsContext) {
        gc.fill = Color.PEACHPUFF
        gc.fillRect(x, y, width, height)
    }

    val speed = Grid.cellSize * 2

    override fun update() {
        val dx = speed / FPS * keybank.keyNegPos(key_left, key_right)
        val dy = speed / FPS * keybank.keyNegPos(key_up, key_down)

        moveOnGrid(x + dx, y + dy, grid.map)
    }
}