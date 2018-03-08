package games.rougelike

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.math.roundToInt

class HUD {
    companion object {
        const val WIDTH = GAME_WIDTH
        const val HEIGHT = 40.0

        const val MAX_CORRUPTION = 10
        var corruption = 6.45
        var score = 0.0
    }

    fun render(gc: GraphicsContext) {
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, WIDTH, HEIGHT)

        val sep = 10.0
        val barWidth = 20.0
        var current = sep

        gc.fill = Color.RED
        for(i in 0..Math.floor(corruption).roundToInt()) {
            gc.fillRect(current, 2.0, barWidth, 36.0)
            current += sep + barWidth
        }

        val remainder = corruption % 1
        gc.fillRect(current, 2.0, barWidth * remainder, 36.0)
    }

    fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}