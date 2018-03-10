package games.rougelike.objects

import games.rougelike.FPS
import games.support.Grid
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType

class ShieldJunker(gc: GraphicsContext, gridX: Double, gridY: Double, var target: IGameObject? = null, speed: Double = Grid.cellSize * 1.5) : Junker(gc, gridX, gridY, target, speed) {

    var currentAngle = 0.0
    var angleSpeed = 180.0 / 3.0 / FPS

    private val desiredAngle: Double
        get() {
            val deltaX = cx - target!!.cx
            val deltaY = -(cy - target!!.cy)

            return Math.toDegrees(Math.atan2(deltaY, deltaX))
        }

    init {
        var angle = desiredAngle
    }

    override fun render() {
        super.render()

        gc.stroke = Color.BLUE
        gc.lineWidth = 4.0
        gc.strokeArc(x - width / 2, y - width / 2, width * 2, height * 2, currentAngle + 90, 180.0, ArcType.OPEN)
        gc.lineWidth = 1.0
    }

    override fun update() {
        super.update()

        currentAngle = desiredAngle
    }
}