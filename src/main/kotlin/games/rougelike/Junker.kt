package games.rougelike

import games.support.Grid
import games.support.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class Junker : IGameObject {
    val radius = Grid.cellSize / 2
    override var height = radius * 2
    override var width = radius * 2
    override var x: Double
    override var y: Double

    val gridx get() = Grid.mapToGrid(x)
    val gridy get() = Grid.mapToGrid(y)

    var target : IGameObject?
    var speed : Double

    constructor (gridx: Double, gridy: Double, target : IGameObject? = null, speed : Double = Grid.cellSize * 1.5) {
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
                    dx += (if (Grid.mapToGrid(target!!.x).toInt() <= gridx) -1.0 else 1.0)
                } else if (gridy % 1.0 != 0.0) {
                    // moving on y-axis
                    dy += (if (Grid.mapToGrid(target!!.y).toInt() <= gridy) -1.0 else 1.0)
                } else {
                    // move anywhere
                    //if (abs(target!!.x - x) > abs(target!!.y - y)) {
                    if (Random().nextBoolean()) {
                        dx += (if (Grid.mapToGrid(target!!.x).toInt() <= gridx) -1.0 else 1.0)
                    } else {
                        dy += (if (Grid.mapToGrid(target!!.y).toInt() <= gridy) -1.0 else 1.0)
                    }
                }

                dx *= speed / FPS
                dy *= speed / FPS

                if (dx != 0.0 && Grid.mapToGrid(x).toInt() != Grid.mapToGrid(x + dx).toInt() && Grid.mapToGrid(x) % 1.0 != 0.0)
                    x = Grid.mapFromGrid(floor(Grid.mapToGrid(x + dx) + 0.5))
                else
                    x += dx

                if (dy != 0.0 && Grid.mapToGrid(y).toInt() != Grid.mapToGrid(y + dy).toInt() && Grid.mapToGrid(y) % 1.0 != 0.0)
                    y = Grid.mapFromGrid(floor(Grid.mapToGrid(y + dy) + 0.5))
                else
                    y += dy
            }
        }

        if (this.collidesWith(player)) {
            player.dead = true
        }
    }
}