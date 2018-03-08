package games.rougelike

import games.support.Grid
import games.support.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.util.*
import kotlin.math.*

class Junker : IGameObject {
    val radius = Grid.cellSize / 2
    override var height = radius * 2
    override var width = radius * 2
    override var x: Double
    override var y: Double

    var target: IGameObject?
    var speed: Double

    constructor (gridx: Double, gridy: Double, target: IGameObject? = null, speed: Double = Grid.cellSize * 1.5) {
        x = Grid.mapFromGrid(gridx)
        y = Grid.mapFromGrid(gridy)
        this.target = target
        this.speed = speed
    }

    override fun render(gc: GraphicsContext) {
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

                var newx: Double
                var newy: Double

                if (gridx.toInt() != Grid.mapToGrid(x + dx).toInt() && gridx % 1.0 != 0.0)
                    newx = Grid.mapFromGrid(round(Grid.mapToGrid(x + dx)))
                else
                    newx = x + dx
                if (gridy.toInt() != Grid.mapToGrid(y + dy).toInt() && gridy % 1.0 != 0.0)
                    newy = Grid.mapFromGrid(round(Grid.mapToGrid(y + dy)))
                else
                    newy = y + dy

                moveOnGrid(newx, newy, grid.map)
            }
        }

        if (this.collidesWith(player)) {
            player.dead = true
        }
    }
}