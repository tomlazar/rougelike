package games.rougelike

import games.support.Grid
import games.support.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class BackgroundObject : IGameObject() {

    // dont matter
    override var height = Grid.cellSize
    override var width = Grid.cellSize
    override var x = 0.0
    override var y = 0.0

    override fun render(gc: GraphicsContext, xOffset: Double, yOffset: Double) {
        gc.fill = Color.GREEN
        gc.stroke = Color.BLACK

        gc.fillRect(xOffset, yOffset, width, height)
        gc.strokeRect(xOffset, yOffset, width, height)
    }

}