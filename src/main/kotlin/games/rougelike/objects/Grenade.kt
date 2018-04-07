package games.rougelike.objects

import games.rougelike.FPS
import games.rougelike.levels.GameLevel
import games.support.Grid
import games.support.LevelManager
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.lang.Math.pow
import kotlin.math.*

class Grenade(gc: GraphicsContext, x: Double, y: Double, airtime: Double = Grenade.airtime, direction: Double) : IGameObject(gc) {
    override var height = 12.0
    override var width = 10.0
    override var x = x
    override var y = y

    companion object {
        val speed = Grid.cellSize * 8.0 / FPS
        val airtime = 0.75 * FPS
    }

    var speed = Grenade.speed
    var direction = direction

    val airtime = airtime
    val fuse = 2.0 * FPS
    val explodeTime = 0.25 * FPS
    val maxExplosionRadius = Grid.cellSize * 1.5

    var counter = 0
    var exploding = false
    val explosionRadius get() = maxExplosionRadius * (counter / explodeTime)
    var causedDamage = false

    override fun render() {
        if (!exploding) {
            gc.fill = Color.BLACK
            gc.fillOval(x, y, width, height)
        } else {
            Effects.setHackEffectVisuals(gc, 1.0 - counter / explodeTime, if (causedDamage) Effects.HackEffect.HIT else Effects.HackEffect.MISS)
            gc.strokeOval(cx - explosionRadius, cy - explosionRadius, explosionRadius * 2, explosionRadius * 2)
        }
    }

    private val nextx get() = x + speed * cos(direction)
    private val nexty get() = y + speed * sin(direction)
    override fun update() {
        counter++
        if (!exploding) {
            if (counter < airtime) {

                for (junker in LevelManager.current.currentGameObjects.map { o: IGameObject -> o as? ShieldJunker }.filter { j: ShieldJunker? -> j != null }) {
                    if (junker!!.protectsFrom(x, y) && sqrt(pow(nextx - junker.cx, 2.0) + pow(nexty - junker.cy, 2.0)) <= junker.shieldRadius + height / 2) {
                        val normalAngle = atan2(cy - junker.cy, cx - junker.cx)
                        println("$direction -> $normalAngle -> ${normalAngle + (normalAngle - (direction - 180))}")
                        direction = normalAngle + (normalAngle - (direction - PI))
                    }
                }
                if (!moveOnGrid(nextx, nexty, grid = LevelManager.current.grid.map, slide = false)) {
                    // hit a wall, move to the wall and stop moving
                    moveOnGrid(nextx, nexty, grid = LevelManager.current.grid.map, slide = true)
                    speed = 0.0
                }
            }
            if (counter >= fuse) {
                exploding = true
                counter = 0
            }
        } else {
            if (counter >= explodeTime) {
                this.dead = true
            } else {
                for (junker in LevelManager.current.currentGameObjects.map { o: IGameObject -> o as? Junker }.filter { j: Junker? -> j != null }) {
                    if (junker!!.collidesWithCircle(cx, cy, explosionRadius)
                            && (junker !is ShieldJunker || !junker.protectsFrom(cx, cy))) {
                        junker.kill()
                        causedDamage = true
                    }
                }
            }
        }
    }

}