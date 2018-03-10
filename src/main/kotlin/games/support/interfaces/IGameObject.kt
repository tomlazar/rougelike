package games.support.interfaces

import games.rougelike.objects.BackgroundObject
import games.support.Grid
import games.support.Util
import javafx.geometry.BoundingBox
import javafx.scene.canvas.GraphicsContext
import java.awt.font.ImageGraphicAttribute
import java.lang.Math.pow
import kotlin.math.*

abstract class IGameObject(val gc: GraphicsContext) {

    abstract var height: Double
    abstract var width: Double

    abstract var x: Double
    abstract var y: Double

    var cx
        get() = x + width / 2
        set(value) {
            x = value - width / 2
        }
    var cy
        get() = y + height / 2
        set(value) {
            y = value - height / 2
        }

    var gridx
        get() = Grid.mapToGrid(x)
        set(value) {
            x = Grid.mapFromGrid(value)
        }
    var gridy
        get() = Grid.mapToGrid(y)
        set(value) {
            y = Grid.mapFromGrid(value)
        }

    var dead: Boolean = false

    fun getBoundingBox(): BoundingBox {
        return BoundingBox(x, y, width, height)
    }

    fun collidesWith(other: IGameObject): Boolean {
        return getBoundingBox().intersects(other.getBoundingBox())
    }

    fun collidesWithCircle(centerX: Double, centerY: Double, radius: Double): Boolean {
        val leftOffset = x - centerX
        val rightOffset = x + width - centerX
        val topOffset = y - centerY
        val bottomOffset = y + height - centerY
        return getBoundingBox().contains(centerX, centerY) // center of circle is inside object
                || lineSegmentIntersectsCircle(leftOffset, topOffset, bottomOffset, radius) // left side of object is inside circle
                || lineSegmentIntersectsCircle(rightOffset, topOffset, bottomOffset, radius) // right side
                || lineSegmentIntersectsCircle(topOffset, leftOffset, rightOffset, radius) // top side
                || lineSegmentIntersectsCircle(bottomOffset, leftOffset, rightOffset, radius) // bottom side
    }

    private fun lineSegmentIntersectsCircle(normalOffset: Double, tangentialOffsetLow: Double, tangentialOffsetHigh: Double, radius: Double): Boolean {
        if (abs(normalOffset) > radius)
            return false
        val maxTangentialDistance = sqrt(pow(radius, 2.0) - pow(normalOffset, 2.0))
        return tangentialOffsetLow <= maxTangentialDistance && -tangentialOffsetHigh <= maxTangentialDistance
    }

    fun distanceTo(other: IGameObject): Double {
        return sqrt(pow(x - other.x, 2.0) + pow(y - other.y, 2.0))
    }

    abstract fun render()
    abstract fun update()

    fun intersectingGridSquares(grid: Array<Array<BackgroundObject>>, x: Double = this.x, y: Double = this.y): List<BackgroundObject> {
        return Util.integersInRange(max(0.0, Grid.mapToGrid(x)), min(grid.size.toDouble(), Grid.mapToGrid(x + width))).map { gx ->
            Util.integersInRange(max(0.0, Grid.mapToGrid(y)), min(grid.size.toDouble(), Grid.mapToGrid(y + height))).map { gy ->
                grid[gx][gy]
            }
        }.flatten()
    }

    fun moveOnGrid(newx: Double, newy: Double, grid: Array<Array<BackgroundObject>>, slide: Boolean = true): Boolean {
        val newPositionTraversable = intersectingGridSquares(grid, newx, newy).all { o: BackgroundObject -> o.type.traversable }

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