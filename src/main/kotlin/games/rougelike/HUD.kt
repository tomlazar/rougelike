package games.rougelike

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class HUD {
    companion object {
        val WIDTH = GAME_WIDTH
        val HEIGHT = 40.0

        var health = 0.0
        var score = 0.0
    }

    fun render(gc: GraphicsContext) {
        gc.fill = Color.BLACK
        gc.fillRect(0.0,0.0, WIDTH, HEIGHT)
    }

    fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}