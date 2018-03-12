package games.rougelike.objects

import games.rougelike.FPS
import games.rougelike.levels.GameLevel
import games.support.Grid
import games.support.interfaces.IGameObject
import games.support.*
import games.support.interfaces.IController
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import java.lang.Math.pow
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

class Player(gc: GraphicsContext) : IGameObject(gc), IController {
    val radius = 15.0
    val speed = Grid.cellSize * 4

    override var height: Double = radius * 2
    override var width: Double = radius * 2
    override var x: Double = 0.0
    override var y: Double = 0.0

    var immune = 0
    val immuneTime = (FPS / 2).toInt()

    override fun addEvents(target: Scene) {
        InputManager.addListener(target, InputBinding.GRENADE, InputEventType.CLICKED, {
            if (Equipment.acquiredEquipment[Equipment.EquipmentType.GRENADE]!!) {
                val dx = LevelManager.inputManager.mouseX - x
                val dy = LevelManager.inputManager.mouseY - y
                LevelManager.current.addLater(Grenade(gc, x, y,
                        airtime = min(Grenade.airtime, sqrt(pow(dx, 2.0) + pow(dy, 2.0)) / Grenade.speed),
                        direction = atan2(dy, dx)))
            }
        })
    }

    override fun render() {
        gc.fill = Color.PEACHPUFF
        gc.fillRect(x, y, width, height)
    }

    fun hit(amount: Double) {
        if (LevelManager.current.player.immune == 0) {
            LevelManager.current.player.immune = immuneTime

            HUD.corruption += amount
        }
    }

    override fun update() {
        if (immune > 0) {
            immune -= 1
        }

        val dx = speed / FPS * LevelManager.inputManager.inputNegPos(InputBinding.LEFT, InputBinding.RIGHT)
        val dy = speed / FPS * LevelManager.inputManager.inputNegPos(InputBinding.UP, InputBinding.DOWN)

        moveOnGrid(x + dx, y + dy, LevelManager.current.grid.map)

        val teleportSpace = intersectingGridSquares(LevelManager.current.grid.map).find { o: BackgroundObject -> o.teleporter != null }
        if (teleportSpace != null) {
            teleport(teleportSpace.teleporter!!)
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