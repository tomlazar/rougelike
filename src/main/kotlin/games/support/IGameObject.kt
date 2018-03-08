package games.support

import games.rougelike.BackgroundObject
import javafx.geometry.BoundingBox
import javafx.scene.canvas.GraphicsContext
import kotlin.math.*

abstract class IGameObject {

    abstract var height: Double
    abstract var width: Double

    abstract var x: Double
    abstract var y: Double

    val gridx get() = Grid.mapToGrid(x)
    val gridy get() = Grid.mapToGrid(y)

    var dead: Boolean = false

    fun getBoundingBox(): BoundingBox {
        return BoundingBox(x, y, width, height)
    }

    fun collidesWith(other: IGameObject): Boolean {
        return getBoundingBox().intersects(other.getBoundingBox())
    }

    abstract fun render(gc: GraphicsContext)
    abstract fun update()

    fun moveOnGrid(newx: Double, newy: Double, grid: Array<Array<BackgroundObject>>): Boolean {
        var newPositionTraversable = true
        for (gx in Grid.mapToGrid(floor(newx)).toInt()..Grid.mapToGrid(floor(newx + this.width)).toInt()) {
            for (gy in Grid.mapToGrid(floor(newy)).toInt()..Grid.mapToGrid(floor(newy + this.height)).toInt()) {
                if ((gx in 0 until grid.size && gy in 0 until grid[gx].size)
                        && !grid[gx][gy].type.traversable) {
                    newPositionTraversable = false
                    break
                }
            }
            if (!newPositionTraversable)
                break
        }

        return when {
            newPositionTraversable -> {
                x = newx
                y = newy
                true
            }
            newy != y && newx != x ->
                (moveOnGrid(newx, y, grid) // try sliding along x-axis
                        || moveOnGrid(x, newy, grid)) // try sliding along y-axis
            else -> false
        }
    }
}