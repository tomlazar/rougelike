package games.support

import games.rougelike.objects.BackgroundObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Grid(val gc: GraphicsContext) {

    companion object {
        val mapWidth = 15
        val mapHeight = 40
        val cellSize = 40.0

        fun mapToGrid(x: Double): Double {
            return x / Companion.cellSize
        }

        fun mapFromGrid(x: Double): Double {
            return x * Companion.cellSize
        }
    }

    val map = Array(mapWidth, { x ->
        Array(mapHeight, { y ->
            BackgroundObject(if (x in 5 until 10 && y in 5 until 15) BackgroundObject.BackgroundType.GAP else BackgroundObject.BackgroundType.FLOOR)
        })
    })

    fun render() {
        gc.fill = Color.BLUE

        for ((x, xarr) in map.withIndex()) {
            for ((y, yval) in xarr.withIndex()) {
                yval.render(gc, x * cellSize, y * cellSize)
            }
        }
    }
}