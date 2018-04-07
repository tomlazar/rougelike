package games.rougelike.objects

import com.sun.org.apache.xpath.internal.WhitespaceStrippingElementMatcher
import games.rougelike.levels.GameLevel
import games.support.LevelManager
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Text
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
        // back bar
        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, WIDTH, HEIGHT)

        val sep = 10.0
        val barWidth = 20.0
        var current = sep

        // corruption
        gc.fill = Color.RED
        gc.stroke = Color.RED
        for (i in 0 until MAX_CORRUPTION) {
            gc.strokeRect(current, 2.0, barWidth, 36.0)
            if (i <= corruption)
                if (i == corruption.toInt()) {
                    val remainder = corruption % 1.0
                    gc.fillRect(current, 2.0, barWidth * remainder, 36.0)
                } else
                    gc.fillRect(current, 2.0, barWidth, 36.0)

            current += sep + barWidth
        }

        gc.fill = Color.WHITE
        val acquiredEquipment = Equipment.EquipmentType.values()
                .filter { t -> Equipment.acquiredEquipment[t]!! }
        if (!acquiredEquipment.isEmpty()) {
            val text = acquiredEquipment.joinToString(",  ", prefix = "Equipment:  ") { t -> t.description }
            val height = Text(text).layoutBounds.height
            val width = Text(text).layoutBounds.width
            gc.fillText(text, current, 2.0 + height)
            current += sep + width
        }
    }

    override fun update() {
        if (corruption >= MAX_CORRUPTION) {
            LevelManager.current.player.dead = true
        }
    }
}