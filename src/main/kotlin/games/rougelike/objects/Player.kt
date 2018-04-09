package games.rougelike.objects

import games.rougelike.FPS
import games.rougelike.levels.GameLevel
import games.rougelike.objects.enemies.Junker
import games.support.Grid
import games.support.interfaces.IGameObject
import games.support.*
import games.support.interfaces.IController
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.lang.Math.pow
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

class Player(gc: GraphicsContext) : Person(gc, "Dennis"), IController {

    var immune = 0
    val immuneTime = (FPS / 2).toInt()

    companion object {
        val pushRange = Grid.cellSize * 2
    }

    val maxGrenadeCooldown = (2 * FPS).toInt()
    var grenadeCooldown = 0

    override fun addEvents(target: Scene) {
        InputManager.addListener(target, InputBinding.GRENADE, InputEventType.CLICKED, {
            if (Equipment.acquiredEquipment[Equipment.EquipmentType.GRENADE]!! && grenadeCooldown <= 0) {
                val dx = LevelManager.inputManager.mouseX - x
                val dy = LevelManager.inputManager.mouseY - y
                LevelManager.current.addLater(Grenade(gc, x, y,
                        airtime = min(Grenade.airtime, sqrt(pow(dx, 2.0) + pow(dy, 2.0)) / Grenade.speed),
                        direction = atan2(dy, dx)))
                grenadeCooldown = maxGrenadeCooldown
            }
        })
        InputManager.addListener(target, InputBinding.PUSH, InputEventType.CLICKED, {
            if (Equipment.acquiredEquipment[Equipment.EquipmentType.PUSH]!!) {
                val pushJunk = LevelManager.current.currentGameObjects
                        .filter { o -> o is Junker }.map { o -> o as Junker }
                        .filter { j -> j.distanceTo(this) <= pushRange && j.canPush }
                        .minBy { j -> j.distanceTo(this) }
                pushJunk?.tryPush()
            }
        })
    }

    fun hit(amount: Double) {
        if (LevelManager.current.player.immune == 0) {
            LevelManager.current.player.immune = immuneTime

            HUD.corruption += amount
        }
    }

    override fun update() {
        super.update()

        if (immune > 0) {
            immune -= 1
        }
        grenadeCooldown--

        moving = listOf(InputBinding.LEFT, InputBinding.RIGHT, InputBinding.DOWN, InputBinding.UP)
                .any { b -> LevelManager.inputManager.isInputActive(b) }

        val dx = speed / FPS * LevelManager.inputManager.inputNegPos(InputBinding.LEFT, InputBinding.RIGHT)
        val dy = speed / FPS * LevelManager.inputManager.inputNegPos(InputBinding.UP, InputBinding.DOWN)

        moveOnGrid(x + dx, y + dy, LevelManager.current.grid.map)

        var eventsPassed = true
        for (precheckEvent in intersectingGridSquares().flatMap { o -> o.precheckEventTriggers }.toSet()) {
            if (!precheckEvent.trigger()) {
                eventsPassed = false
                break
            }
        }

        if (eventsPassed) {
            for (triggerEvent in intersectingGridSquares().flatMap { o -> o.eventTriggers }.toSet()) {
                triggerEvent.trigger()
            }
            for (resetEvent in intersectingGridSquares().flatMap { o -> o.eventResets }.toSet()) {
                resetEvent.reset()
            }

            val teleportSpace = intersectingGridSquares(LevelManager.current.grid.map).find { o: BackgroundObject -> o.teleporter != null }
            if (teleportSpace != null) {
                teleport(teleportSpace.teleporter!!)
            }
        }
    }

    private fun teleport(target: BackgroundObject.TeleportLocation) {
        val level = GameLevel.getLevel(target.level)
        level!!.player.gridx = target.gridx
        level.player.gridy = target.gridy
        Junker.targetedJunker = null
        LevelManager.current = level
    }
}