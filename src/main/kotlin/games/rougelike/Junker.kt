package games.rougelike

import games.rougelike.levels.GameLevel
import games.support.Grid
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.util.*
import kotlin.math.*

@Suppress("ConvertSecondaryConstructorToPrimary")
class Junker : IGameObject {
    private val radius = Grid.cellSize / 2
    override var height = radius * 2
    override var width = radius * 2
    override var x: Double
    override var y: Double

    private var target: IGameObject?
    private var speed: Double

    constructor (gc: GraphicsContext, gridX: Double, gridY: Double, target: IGameObject? = null, speed: Double = Grid.cellSize * 1.5) : super(gc) {
        x = Grid.mapFromGrid(gridX)
        y = Grid.mapFromGrid(gridY)
        this.target = target
        this.speed = speed
    }

    override fun render() {
        gc.fill = Color.GRAY
        gc.fillRect(x, y, width, height)
    }

    override fun update() {
        if (target != null) {
            if (target!!.dead)
                target = null
            else {
                var dx = 0.0
                var dy = 0.0

                if (gridx % 1.0 != 0.0) {
                    // moving on x-axis
                    dx += (if (target!!.gridx.toInt() <= gridx) -1.0 else 1.0)
                } else if (gridy % 1.0 != 0.0) {
                    // moving on y-axis
                    dy += (if (target!!.gridy.toInt() <= gridy) -1.0 else 1.0)
                } else {
                    // move anywhere
                    if (Random().nextBoolean()) {
                        dx += (if (target!!.gridx.toInt() <= gridx) -1.0 else 1.0)
                    } else {
                        dy += (if (target!!.gridy.toInt() <= gridy) -1.0 else 1.0)
                    }
                }

                dx *= speed / FPS
                dy *= speed / FPS

                val newX: Double
                val newY: Double

                if (gridx.toInt() != Grid.mapToGrid(x + dx).toInt() && gridx % 1.0 != 0.0)
                    newX = Grid.mapFromGrid(floor(Grid.mapToGrid(x + dx) + 0.5))
                else
                    newX = x + dx

                if (gridy.toInt() != Grid.mapToGrid(y + dy).toInt() && gridy % 1.0 != 0.0)
                    newY = Grid.mapFromGrid(floor(Grid.mapToGrid(y + dy) + 0.5))
                else
                    newY = y + dy

                moveOnGrid(newX, newY, GameLevel.grid.map)
            }
        }

        if (this.collidesWith(GameLevel.player)) {
            GameLevel.player.dead = true
        }
    }
}