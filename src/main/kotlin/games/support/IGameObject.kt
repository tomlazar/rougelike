package games.support

import javafx.geometry.BoundingBox
import javafx.scene.canvas.GraphicsContext

abstract class IGameObject {

    abstract var height: Double
    abstract var width: Double

    abstract var x: Double
    abstract var y: Double

    var dead: Boolean = false

    fun getBoundingBox(): BoundingBox {
        return BoundingBox(x, y, width, height)
    }

    fun collidesWith(other: IGameObject): Boolean {
        return getBoundingBox().intersects(other.getBoundingBox())
    }

    abstract fun render(gc: GraphicsContext)
    abstract fun update()
}