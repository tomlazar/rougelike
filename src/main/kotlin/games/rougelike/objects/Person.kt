package games.rougelike.objects

import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

open class Person(gc: GraphicsContext, val name: String) : IGameObject(gc) {
    val radius = 15.0

    override var height: Double = radius * 2
    override var width: Double = radius * 2
    override var x: Double = 0.0
    override var y: Double = 0.0

    override fun render() {
        gc.fill = Color.PEACHPUFF
        gc.fillRect(x, y, width, height)
    }

    override fun update() {}
}