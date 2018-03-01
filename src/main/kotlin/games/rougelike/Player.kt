package games.rougelike

import games.support.IController
import games.support.IGameObject
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Player : IGameObject(), IController {

    override fun addEvents(scene: Scene) {

    }

    override var height: Double = 10.0
    override var width: Double = 10.0
    override var x: Double = 200.0
    override var y: Double = 200.0

    override fun render(gc: GraphicsContext, xOffset: Double, yOffset: Double) {
        gc.fill = Color.RED
        gc.fillOval(xOffset + x, yOffset + y, width, height);
    }


}