package games.rougelike.objects

import games.rougelike.levels.GameLevel
import games.support.LevelManager
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Text

class HUD(gc: GraphicsContext) : IGameObject(gc) {
    override var width = 0.0
    override var x = 0.0
    override var y = 0.0
    override var height = 0.0

    companion object {
        val WIDTH = GameLevel.WIDTH
        val HEIGHT = 100.0

        const val MAX_CORRUPTION = 5
        var corruption = 0.0
        var score = 0.0
        var deaths = 0
        var objective = ""
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
        val corruptionHeight = HUD.HEIGHT - 8.0
        for (i in 0 until MAX_CORRUPTION) {
            gc.strokeRect(current, 4.0, barWidth, corruptionHeight)
            if (i <= corruption)
                if (i == corruption.toInt()) {
                    val remainder = corruption % 1.0
                    gc.fillRect(current, 2.0, barWidth * remainder, corruptionHeight)
                } else
                    gc.fillRect(current, 2.0, barWidth, corruptionHeight)

            current += sep + barWidth
        }

        // text
        var currentHeight = 2.0
        gc.fill = Color.WHITE
        val acquiredEquipment = Equipment.EquipmentType.values()
                .filter { t -> Equipment.acquiredEquipment[t]!! }
        if (!acquiredEquipment.isEmpty()) {
            val texts = mutableListOf<String>()
            texts.add("Equipment:  ")
            for (e in acquiredEquipment) {
                val desc = e.description + if (acquiredEquipment.indexOf(e) < acquiredEquipment.size - 1) ", " else ""
                val sumText = texts.last() + desc
                if (Text(sumText).layoutBounds.width > WIDTH - current)
                    texts.add(desc)
                else
                    texts[texts.size - 1] = sumText
            }
            val savedCurrent = current

            for (text in texts) {
                current = savedCurrent
                val height = Text(text).layoutBounds.height
                val width = Text(text).layoutBounds.width
                gc.fillText(text, current, currentHeight + height)
                current += sep + width
                currentHeight += 2.0 + height
            }
            current = savedCurrent
        }

        val savedCurrent = current
        if (objective != "") {
            gc.fill = Color.SKYBLUE
            val objectiveText = "Objective: $objective"
            val height = Text(objectiveText).layoutBounds.height
            val width = Text(objectiveText).layoutBounds.width
            gc.fillText(objectiveText, current, currentHeight + height)
            current += sep + width
        }
        current = savedCurrent

        gc.fill = Color.PALEVIOLETRED
        val floorText = "Level: ${LevelManager.current.levelId}"
        val height = Text(floorText).layoutBounds.height
        val width = Text(floorText).layoutBounds.width
        gc.fillText(floorText, current, (2.0 + height) * 4)
    }

    val godMode = true

    override fun update() {
        if (corruption >= MAX_CORRUPTION) {
            deaths++
            if (godMode) {
                Events.GameEvent.GOD_MODE.trigger()
            } else
                LevelManager.current.player.dead = true
        }
    }
}