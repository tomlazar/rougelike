package games.rougelike.objects

import games.support.Grid
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class BackgroundObject(type: BackgroundType = BackgroundType.GAP) {

    var type = type

    enum class BackgroundType(var traversable: Boolean) {
        FLOOR(true), GAP(false)
    }

    fun render(gc: GraphicsContext, x: Double, y: Double) {
        when (this.type) {
            BackgroundType.FLOOR -> {
                gc.fill = Color.GREEN
                gc.stroke = Color.BLACK
            }
            BackgroundType.GAP -> {
                gc.fill = Color.WHITE
                gc.stroke = Color.TRANSPARENT
            }
        }

        gc.fillRect(x, y, Grid.cellSize, Grid.cellSize)
        gc.strokeRect(x, y, Grid.cellSize, Grid.cellSize)
    }

}