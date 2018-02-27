package tomlazar.games.support

import javafx.scene.canvas.GraphicsContext

abstract class IGameController : IGameObject() {

    override var height = 0.0
    override var width  = 0.0
    override var x      = 0.0
    override var y      = 0.0

    // This object has no form
    override fun render(gc: GraphicsContext) { }
}