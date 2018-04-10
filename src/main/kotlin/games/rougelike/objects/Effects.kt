package games.rougelike.objects

import games.rougelike.FPS
import games.rougelike.objects.enemies.Junker
import games.rougelike.objects.enemies.ShieldJunker
import games.support.*
import games.support.interfaces.IController
import games.support.interfaces.IGameObject
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.lang.Math.pow
import kotlin.math.*

class Effects(gc: GraphicsContext) : IGameObject(gc), IController {
    override var height = 0.0
    override var width = 0.0
    override var x = 0.0
    override var y = 0.0

    override fun addEvents(target: Scene) {
        InputManager.addListener(target, InputBinding.SPAWN_JUNKER, InputEventType.CLICKED, {
            val junker = ShieldJunker(gc, Grid.mapToGrid(LevelManager.inputManager.mouseX),
                    Grid.mapToGrid(LevelManager.inputManager.mouseY), target = LevelManager.current.player)
            LevelManager.current.addLater(junker)
            junker.addEvents(target)
        })

        InputManager.addListener(target, InputBinding.PAUSE_GAMEPLAY, InputEventType.CLICKED, {
            LevelManager.current.toggleSuspended()
        })
    }

    override fun render() {
        renderHackEffect()
    }

    override fun update() {
        updateHackEffect()
    }

    // region Hack Effect
    enum class HackEffect(val duration: Double) {
        INACTIVE(0.0), HIT_WAITING(0.25 * FPS), MISS_WAITING(0.15 * FPS), HIT(0.15 * FPS), MISS(0.1 * FPS);

        val isActive get() = this == HIT || this == MISS

        val nextState
            get() =
                when {
                    this == HIT -> HIT_WAITING
                    this == MISS -> MISS_WAITING
                    canHitTargetNow -> HIT
                    else -> MISS
                }

    }

    var hackEffectState = HackEffect.INACTIVE
    var hackEffectX = 0.0
    var hackEffectY = 0.0
    var hackEffectCounter = 0

    companion object {
        val hackEffectWeight = 5.0
        fun setHackEffectVisuals(gc: GraphicsContext, weight: Double, effectState: HackEffect) {
            gc.lineWidth = hackEffectWeight * weight
            gc.stroke = if (effectState == HackEffect.HIT) Color.DARKBLUE else Color.SKYBLUE
        }

        val hackRange get() = Junker.targetedJunker!!.targetingDistance

        private val hackSourceX get () = LevelManager.current.player.x + LevelManager.current.player.width / 2
        private val hackSourceY get () = LevelManager.current.player.y + LevelManager.current.player.height / 2

        val canHitTargetNow
            get() =
                Junker.targetedJunker != null
                        && LevelManager.current.player.distanceTo(Junker.targetedJunker!!) < hackRange
                        && (Junker.targetedJunker!! !is ShieldJunker // if we have a shield junker, check if it is shielded:
                        || !(Junker.targetedJunker!! as ShieldJunker).protectsFrom(hackSourceX, hackSourceY))
    }


    fun renderHackEffect() {
        if (hackEffectState.isActive) {
            setHackEffectVisuals(gc, 1.0 - hackEffectCounter / hackEffectState.duration, hackEffectState)
            gc.strokeLine(hackSourceX, hackSourceY, hackEffectX, hackEffectY)
        }
    }

    fun updateHackEffect() {
        val hacking = (Equipment.acquiredEquipment[Equipment.EquipmentType.HACK]!!
                && (LevelManager.inputManager.isInputActive(InputBinding.HACK)
                || (hackEffectState == HackEffect.HIT || hackEffectState == HackEffect.MISS))
                && Junker.targetedJunker != null)


        if (hackEffectState == HackEffect.HIT && Junker.targetedJunker != null)
            Junker.targetedJunker!!.hackingProgress += 1

        hackEffectCounter += 1
        if (hackEffectCounter >= hackEffectState.duration) {
            hackEffectCounter = 0
            if (hacking) {
                hackEffectState = hackEffectState.nextState
                hackEffectX = Junker.targetedJunker!!.x + Junker.targetedJunker!!.width * Math.random()
                hackEffectY = Junker.targetedJunker!!.y + Junker.targetedJunker!!.height * Math.random()

                if (hackEffectState == HackEffect.MISS) {
                    val dx = hackEffectX - hackSourceX
                    val dy = hackEffectY - hackSourceY
                    val dist = sqrt(pow(dx, 2.0) + pow(dy, 2.0))
                    val missDistance = hackRange - max(Junker.targetedJunker!!.width, Junker.targetedJunker!!.height)
                    if (dist > missDistance) {
                        hackEffectX = hackSourceX + dx * missDistance / dist
                        hackEffectY = hackSourceY + dy * missDistance / dist
                    }
                }
            } else {
                hackEffectState = HackEffect.INACTIVE
            }
        }
    }
// endregion

}