package games.support

import games.rougelike.BackgroundObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Grid {

    companion object {
        val mapWidth = 40
        val mapHeight = 15
        val cellSize = 40.0
    }

    val map = Array(mapHeight, { Array(mapWidth, { BackgroundObject() }) })


    val width: Double
        get() = mapWidth * cellSize

    fun render(gc: GraphicsContext, xOffset: Double, yOffset: Double, viewWidth: Double, viewHeight: Double) {
        gc.fill = Color.BLUE

        val startRow = (yOffset / cellSize).toInt()
        val endRow = Math.min(((yOffset + viewWidth) / cellSize).toInt(), mapHeight - 1)

        val startColumn = (xOffset / cellSize).toInt()
        val endColumn = Math.min(((xOffset + viewHeight) / cellSize).toInt(), mapWidth - 1)

        for ((x, xarr) in map.sliceArray(startRow..endRow).withIndex()) {
            for ((y, yval) in xarr.sliceArray(startColumn..endColumn).withIndex()) {
                yval.render(gc, x * cellSize - xOffset, y * cellSize - yOffset)
            }
        }
    }

}