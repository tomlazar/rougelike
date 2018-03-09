package games.rougelike.objects

import games.rougelike.FPS
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
                    if ((Random().nextBoolean() && abs(target!!.gridx - gridx) >= 1) || abs(target!!.gridy - gridy) < 1) {
                        dx += (if (target!!.gridx.toInt() <= gridx) -1.0 else 1.0)
                    } else {
                        dy += (if (target!!.gridy.toInt() <= gridy) -1.0 else 1.0)
                    }
                }

                dx *= speed / FPS
                dy *= speed / FPS

                var newX: Double
                var newY: Double

                if (gridx.toInt() != Grid.mapToGrid(x + dx).toInt() && gridx % 1.0 != 0.0)
                    newX = Grid.mapFromGrid(round(Grid.mapToGrid(x + dx)))
                else
                    newX = x + dx

                if (gridy.toInt() != Grid.mapToGrid(y + dy).toInt() && gridy % 1.0 != 0.0)
                    newY = Grid.mapFromGrid(round(Grid.mapToGrid(y + dy)))
                else
                    newY = y + dy

                moveOnGrid(newX, newY, GameLevel.grid.map)
            }
        }

        if (GameLevel.player.immune == 0 && this.collidesWith(GameLevel.player)) {
            GameLevel.player.immune = FPS.toInt() / 2

            HUD.corruption += 1
        }
    }
}