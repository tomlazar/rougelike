package games.rougelike.objects.enemies

import games.rougelike.FPS
import games.rougelike.objects.BackgroundObject
import games.rougelike.objects.Effects
import games.rougelike.objects.Equipment
import games.rougelike.objects.Player
import games.support.*
import games.support.interfaces.IController
import games.support.interfaces.IGameObject
import javafx.scene.Scene
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
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
            //if (targetedJunker != null)
            //    targetedJunker!!.hackingProgress = 0.0
            targetedJunker = j
        }
    }

    private val isBeingTargeted get() = this == targetedJunker
    var hackingProgress = 0.0
    val hackTime = 1.5 * FPS * (Effects.HackEffect.HIT.duration / (Effects.HackEffect.HIT.duration + Effects.HackEffect.HIT_WAITING.duration))

    constructor (gc: GraphicsContext, gridX: Double, gridY: Double, target: IGameObject? = null, speed: Double = Grid.cellSize * 1.5) : super(gc) {
        x = Grid.mapFromGrid(gridX)
        y = Grid.mapFromGrid(gridY)
        this.target = target
        this.speed = speed
    }

    override fun addEvents(target: Scene) {
        InputManager.addListener(target, InputBinding.SET_TARGET, InputEventType.CLICKED, {
            if (!this.dead && getBoundingBox().contains(LevelManager.inputManager.mouseX, LevelManager.inputManager.mouseY)
                    && Equipment.acquiredEquipment[Equipment.EquipmentType.HACK]!!)
                switchTargetedJunker(this)
        })
    }

    var renderPushButtonDelay = 2
    override fun render() {
        gc.fill = Color.GRAY
        gc.fillRect(x, y, width, height)

        if (Equipment.acquiredEquipment[Equipment.EquipmentType.PUSH]!! && push == null && this.distanceTo(LevelManager.current.player) <= Player.pushRange && canPush) {
            if (renderPushButtonDelay <= 0) {
                gc.fill = Color.WHITE
                gc.stroke = Color.BLACK
                gc.fillRoundRect(x + width / 4, y + width / 4, width / 2, height / 2, 5.0, 5.0)
                gc.strokeRoundRect(x + width / 4, y + width / 4, width / 2, height / 2, 5.0, 5.0)

                gc.fill = Color.BLACK
                val text = if (InputBinding.PUSH.input.first() is KeyInput) InputBinding.PUSH.input.first().toString() else "*"

                val textObj = Text(text)
                textObj.textAlignment = TextAlignment.CENTER
                gc.fillText(text, x + width / 2 - (textObj.layoutBounds.width / 2), y + height / 2 + (textObj.layoutBounds.height / 3))
            } else
                renderPushButtonDelay--
        } else
            renderPushButtonDelay = 2

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
        if (push != null) {
            val move = pushSpeed / FPS / distanceTo(Grid.mapFromGrid(this.push!!.gridx), Grid.mapFromGrid(this.push!!.gridy))
            this.x = this.x + (Grid.mapFromGrid(this.push!!.gridx) - this.x) * move
            this.y = this.y + (Grid.mapFromGrid(this.push!!.gridy) - this.y) * move
            if (this.intersectingGridSquares(LevelManager.current.grid.map).all { o -> o.type == BackgroundObject.BackgroundType.GAP })
                this.kill()
        } else {
            if (target != null) {
                if (!tracking && distanceTo(target!!) < targetingDistance)
                    tracking = true
                if (tracking && distanceTo(target!!) > trackingDistance)
                    tracking = false
            }

            update_Move()

            if (hackingProgress >= hackTime)
                this.kill()

            if (this.collidesWith(LevelManager.current.player)) {
                LevelManager.current.player.hit(0.5)
            }
        }
    }

    val targetingDistance = Grid.cellSize * 4.0
    val trackingDistance = Grid.cellSize * 8.0
    var tracking = false

    private fun update_Move() {
        // track the target, moving along the grid

        if (target != null && tracking) {
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

                moveOnGrid(newX, newY, LevelManager.current.grid.map)
            }
        }
    }

    var push : BackgroundObject.PushLocation? = null
    val pushSpeed = Grid.cellSize * 8

    val canPush get() = this.intersectingGridSquares(LevelManager.current.grid.map).any { o -> o.push != null }

    fun tryPush() : Boolean {
        val pushOffset = this.intersectingGridSquares(LevelManager.current.grid.map).firstOrNull { o -> o.push != null }?.push
        return if (pushOffset != null) {
            push = BackgroundObject.PushLocation(gridx + pushOffset.gridx, gridy + pushOffset.gridy)
            true
        } else
            false
    }

    fun kill() {
        this.dead = true
        if (targetedJunker == this)
            switchTargetedJunker(null)
    }
}