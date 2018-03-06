package games.support

import games.rougelike.BackgroundObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Grid {

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

    val map    = Array(mapWidth, { Array(mapHeight, { BackgroundObject() }) })
    
    fun render(gc: GraphicsContext) {
        gc.fill = Color.BLUE

        for ((x, xarr) in map.withIndex()) {
            for ((y, yval) in xarr.withIndex()) {
                yval.render(gc, x * cellSize, y * cellSize)
            }
        }
    }
}