package games.rougelike.objects

import games.rougelike.FPS
import games.rougelike.levels.GameLevel
import games.support.*
import games.support.interfaces.IController
import games.support.interfaces.IGameObject
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import java.util.*
import kotlin.math.*

@Suppress("ConvertSecondaryConstructorToPrimary")
open class Junker : IGameObject, IController {
    // Junker is the superclass to all basic enemies

    private val radius = Grid.cellSize / 2
    override var height = radius * 2
    override var width = radius * 2
    override var x: Double
    override var y: Double

    private var target: IGameObject?
    private var speed: Double

    companion object {
        var targetedJunker: Junker? = null
        fun switchTargetedJunker(j: Junker?) {
            if (targetedJunker != null)
                targetedJunker!!.hackingProgress = 0.0
            targetedJunker = j
        }
    }
    private val isBeingTargeted get() = this == targetedJunker
    var hackingProgress = 0.0
    val hackTime = 1.5 * FPS * (Effects.HackEffect.ACTIVE.duration / (Effects.HackEffect.ACTIVE.duration + Effects.HackEffect.WAITING.duration))

    constructor (gc: GraphicsContext, gridX: Double, gridY: Double, target: IGameObject? = null, speed: Double = Grid.cellSize * 1.5) : super(gc) {
        x = Grid.mapFromGrid(gridX)
        y = Grid.mapFromGrid(gridY)
        this.target = target
        this.speed = speed
    }

    override fun addEvents(target: Scene) {
        target.addEventHandler(MouseEvent.MOUSE_CLICKED, MouseBank.makeButtonListener(button_setTarget, this, { switchTargetedJunker(this) }))
    }

    override fun render() {
        gc.fill = Color.GRAY
        gc.fillRect(x, y, width, height)

        gc.lineWidth = 1.5
        val targetSymbolRadius = radius * sqrt(2.0) + gc.lineWidth + 3
        gc.stroke = Color.DARKRED
        if (isBeingTargeted) {
            gc.strokeOval(x + radius - targetSymbolRadius, y + radius - targetSymbolRadius, targetSymbolRadius * 2, targetSymbolRadius * 2)
        }
        gc.lineWidth *= 3
        gc.strokeArc(x + radius - targetSymbolRadius, y + radius - targetSymbolRadius, targetSymbolRadius * 2, targetSymbolRadius * 2,
                90 - 180 * (hackingProgress / hackTime), 360 * (hackingProgress / hackTime), ArcType.OPEN)
    }

    override fun update() {
        update_Move()

        if (hackingProgress >= hackTime)
            this.kill()

        if (this.collidesWith(GameLevel.player)) {
            GameLevel.player.hit(1)
        }
    }

    private fun update_Move() {
        // track the target, moving along the grid

        if (target != null) {
            if (target!!.dead)
                target = null
            else {
                var dx = 0.0
                var dy = 0.0

                if (gridx % 1.0 != 0.0) {
                    // moving on x-axis
                    dx += (if (target!!.gridx.toInt() <= gridx) -1.0 else 1.0)
                } else if (gridy % 1.0 != 0.0) {
                    // moving on y-axis
                    dy += (if (target!!.gridy.toInt() <= gridy) -1.0 else 1.0)
                } else {
                    // move anywhere
                    if ((Random().nextBoolean() && abs(target!!.gridx - gridx) >= 1) || abs(target!!.gridy - gridy) < 1) {
                        dx += (if (target!!.gridx.toInt() <= gridx) -1.0 else 1.0)
                    } else {
                        dy += (if (target!!.gridy.toInt() <= gridy) -1.0 else 1.0)
                    }
                }

                dx *= speed / FPS
                dy *= speed / FPS

                var newX: Double
                var newY: Double

                if (gridx.toInt() != Grid.mapToGrid(x + dx).toInt() && gridx % 1.0 != 0.0)
                    newX = Grid.mapFromGrid(round(Grid.mapToGrid(x + dx)))
                else
                    newX = x + dx

                if (gridy.toInt() != Grid.mapToGrid(y + dy).toInt() && gridy % 1.0 != 0.0)
                    newY = Grid.mapFromGrid(round(Grid.mapToGrid(y + dy)))
                else
                    newY = y + dy

                moveOnGrid(newX, newY, GameLevel.grid.map)
            }
        }
    }

    fun kill() {
        this.dead = true
        if (targetedJunker == this)
            switchTargetedJunker(null)
    }
}