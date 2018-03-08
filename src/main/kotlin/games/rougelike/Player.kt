package games.rougelike

import games.rougelike.levels.GameLevel
import games.support.Grid
import games.support.interfaces.IGameObject
import games.support.*
import games.support.interfaces.IController
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color

class Player(gc: GraphicsContext) : IGameObject(gc), IController {
    val radius = 15.0
    val speed = Grid.cellSize * 4

    override var height: Double = radius * 2
    override var width: Double = radius * 2
    override var x: Double = 0.0

    override var y: Double = 0.0

    var mousePoint = Point2D(0.0, 0.0)

    override fun addEvents(target: Scene) {
        target.setOnMouseMoved { event: MouseEvent? ->
            run {
                var relx = event!!.sceneX + GameLevel.camera.sceneCamera.translateX
                val rely = event.sceneY + GameLevel.camera.sceneCamera.translateY - HUD.HEIGHT
                mousePoint = Point2D(relx, rely)
            }
        }
    }

    override fun render() {
        gc.fill = Color.PEACHPUFF
        gc.fillRect(x, y, width, height)

        gc.fill = Color.HOTPINK
        gc.fillOval(mousePoint.x - 25.0, mousePoint.y - 25.0, 50.0, 50.0)
    }

    override fun update() {
        val dx = speed / FPS * GameLevel.keybank.keyNegPos(key_left, key_right)
        val dy = speed / FPS * GameLevel.keybank.keyNegPos(key_up, key_down)

        moveOnGrid(x + dx, y + dy, GameLevel.grid.map)
    }
}