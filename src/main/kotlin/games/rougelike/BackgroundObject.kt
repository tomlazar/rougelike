package games.rougelike

import games.support.Grid
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class BackgroundObject {

    fun render(gc: GraphicsContext, x: Double, y: Double) {
        gc.fill = Color.GREEN
        gc.stroke = Color.BLACK

        gc.fillRect(x, y, Grid.cellSize, Grid.cellSize)
        gc.strokeRect(x, y, Grid.cellSize, Grid.cellSize)
    }

}