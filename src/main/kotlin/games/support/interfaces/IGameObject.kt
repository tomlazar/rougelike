package games.support.interfaces

import games.rougelike.objects.BackgroundObject
import games.support.Grid
import javafx.geometry.BoundingBox
import javafx.scene.canvas.GraphicsContext
import kotlin.math.*

abstract class IGameObject(val gc: GraphicsContext){

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

    abstract fun render()
    abstract fun update()

    fun moveOnGrid(newx: Double, newy: Double, grid: Array<Array<BackgroundObject>>, slide: Boolean = true): Boolean {
        var gleft = max(0, Grid.mapToGrid(newx).toInt())
        var gright = min(grid.size, Grid.mapToGrid(newx + width).toInt())
        var gtop = max(0, Grid.mapToGrid(newy).toInt())
        var gbottom = min(grid[0].size, Grid.mapToGrid(newy + height).toInt())
        if (Grid.mapToGrid(newx + width) % 1.0 == 0.0)
            gright -= 1
        if (Grid.mapToGrid(newy + height) % 1.0 == 0.0)
            gbottom -= 1

        val newPositionTraversable =
                (gleft..gright).asSequence().all { gx ->
                    (gtop..gbottom).asSequence().all { gy ->
                        grid[gx][gy].type.traversable
                    }
                }

        return when {
            newPositionTraversable -> {
                x = newx
                y = newy
                true
            }
            slide -> {
                // depending on the direction of motion ((x,y) -> (newx,newy)), attempt to "slide" along the untraversable edge by setting one of the coordinates to the grid edge
                if (newx < x && (Grid.mapToGrid(x) % 1.0 == 0.0 || Grid.mapToGrid(newx).toInt() != Grid.mapToGrid(x).toInt())
                        && moveOnGrid(Grid.mapFromGrid(ceil(Grid.mapToGrid(newx))), newy, grid, false)) // don't try sliding again from a recursive slide call
                    true
                else if (newx > x && (Grid.mapToGrid(x + width) % 1.0 == 0.0 || Grid.mapToGrid(newx + width).toInt() != Grid.mapToGrid(x + width).toInt())
                        && moveOnGrid(Grid.mapFromGrid(floor(Grid.mapToGrid(newx + width))) - width, newy, grid, false))
                    true
                else if (newy < y && (Grid.mapToGrid(y) % 1.0 == 0.0 || Grid.mapToGrid(newy).toInt() != Grid.mapToGrid(y).toInt())
                        && moveOnGrid(newx, Grid.mapFromGrid(ceil(Grid.mapToGrid(newy))), grid, false))
                    true
                else newy > y && (Grid.mapToGrid(y + height) % 1.0 == 0.0 || Grid.mapToGrid(newy + height).toInt() != Grid.mapToGrid(y + height).toInt())
                        && moveOnGrid(newx, Grid.mapFromGrid(floor(Grid.mapToGrid(newy + height))) - height, grid, false)
            }
            else -> false
        }
    }
}