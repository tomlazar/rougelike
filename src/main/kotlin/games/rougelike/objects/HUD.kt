package games.rougelike.objects

import games.rougelike.levels.GameLevel
import games.support.LevelManager
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.math.roundToInt

class HUD(gc: GraphicsContext) : IGameObject(gc) {
    override var width = 0.0
    override var x = 0.0
    override var y = 0.0
    override var height = 0.0

    companion object {
        val WIDTH = GameLevel.WIDTH
        val HEIGHT = 40.0

        const val MAX_CORRUPTION = 5
        var corruption = 0.0
        var score = 0.0
    }

    override fun render() {
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, WIDTH, HEIGHT)

        val sep = 10.0
        val barWidth = 20.0
        var current = sep

        gc.fill = Color.RED
        for (i in 0..Math.floor(corruption).roundToInt()) {
            if(i == 0)
                continue
            gc.fillRect(current, 2.0, barWidth, 36.0)
            current += sep + barWidth
        }

        if(corruption > 0) {
            val remainder = corruption % 1
            gc.fillRect(current, 2.0, barWidth * remainder, 36.0)
        }
    }

    override fun update() {
        if (corruption >= MAX_CORRUPTION) {
            LevelManager.current.player.dead = true
        }
    }
}