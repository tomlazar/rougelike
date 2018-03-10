package games.rougelike.objects

import games.rougelike.FPS
import games.support.Grid
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import kotlin.math.atan2
import kotlin.math.sqrt

class ShieldJunker(gc: GraphicsContext, gridX: Double, gridY: Double, var target: IGameObject? = null, speed: Double = Grid.cellSize * 1.5) : Junker(gc, gridX, gridY, target, speed) {

    var currentAngle = 0.0
    var angleSpeed = 180.0 / 3.0 / FPS
    val shieldArc = 150.0
    val shieldRadius = width

    private val desiredAngle: Double
        get() {
            return if (target != null) {
                val deltaX = target!!.cx - cx
                val deltaY = target!!.cy - cy

                Math.toDegrees(atan2(deltaY, deltaX))
            } else currentAngle
        }

    fun protectsFrom(attackX: Double, attackY: Double): Boolean {
        val dx = attackX - cx
        val dy = attackY - cy
        val attackAngle = remainder(Math.toDegrees(Math.atan2(dy, dx)), 360.0)
        val minAngle = remainder(currentAngle, 360.0) - shieldArc / 2
        val maxAngle = remainder(currentAngle, 360.0) + shieldArc / 2
        return attackAngle in minAngle..maxAngle && sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0)) > shieldRadius
    }

    private fun remainder(x: Double, y: Double): Double = ((x % y) + y) % y

    init {
        var angle = desiredAngle
    }

    override fun render() {
        super.render()

        gc.stroke = Color.BLUE
        gc.lineWidth = 4.0
        gc.strokeArc(cx - shieldRadius, cy - shieldRadius, shieldRadius * 2, shieldRadius * 2, -(currentAngle - shieldArc / 2), -shieldArc, ArcType.OPEN)
        gc.lineWidth = 1.0
    }

    override fun update() {
        super.update()

        currentAngle = desiredAngle
    }
}