package games.rougelike.objects

import games.rougelike.FPS
import games.rougelike.levels.GameLevel
import games.support.interfaces.IController
import games.support.interfaces.IGameObject
import games.support.key_hack
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Effects(gc: GraphicsContext) : IGameObject(gc), IController {
    override var height = 0.0
    override var width = 0.0
    override var x = 0.0
    override var y = 0.0

    override fun addEvents(target: Scene) {

    }

    enum class HackEffect(val duration: Double) {
        INACTIVE(0.0), WAITING(0.15 * FPS), ACTIVE(0.1 * FPS);

        val nextState
            get() = when (this) {
                HackEffect.ACTIVE -> HackEffect.WAITING
                else -> HackEffect.ACTIVE
            }
    }

    var hackEffectState = HackEffect.INACTIVE
    var hackEffectX = 0.0
    var hackEffectY = 0.0
    var hackEffectCounter = 0
    val hackEffectWeight = 5.0

    override fun render() {
        if (hackEffectState == HackEffect.ACTIVE) {
            gc.lineWidth = hackEffectWeight * (1.0 - hackEffectCounter / hackEffectState.duration)
            gc.stroke = Color.SKYBLUE
            gc.strokeLine(GameLevel.player.x + GameLevel.player.width / 2, GameLevel.player.y + GameLevel.player.height / 2,
                    hackEffectX, hackEffectY)
        }
    }

    override fun update() {
        val hacking = GameLevel.keybank.isKeyDown(key_hack) && Junker.targetedJunker != null
        if (hacking)
            Junker.targetedJunker!!.hackingProgress += 1

        hackEffectCounter += 1
        if (hackEffectCounter >= hackEffectState.duration) {
            hackEffectCounter = 0
            if (hacking) {
                hackEffectState = hackEffectState.nextState
                hackEffectX = Junker.targetedJunker!!.x + Junker.targetedJunker!!.width * Math.random()
                hackEffectY = Junker.targetedJunker!!.y + Junker.targetedJunker!!.height * Math.random()
            } else {
                hackEffectState = HackEffect.INACTIVE
            }
        }
    }
}