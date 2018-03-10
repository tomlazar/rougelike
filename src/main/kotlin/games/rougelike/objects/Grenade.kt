package games.rougelike.objects

import games.rougelike.FPS
import games.rougelike.levels.GameLevel
import games.support.Grid
import games.support.LevelManager
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.math.cos
import kotlin.math.sin

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

    override fun render() {
        if (!exploding) {
            gc.fill = Color.BLACK
            gc.fillOval(x, y, width, height)
        } else {
            Effects.setHackEffectVisuals(gc, 1.0 - counter / explodeTime)
            gc.strokeOval(x + width - explosionRadius, y + height - explosionRadius, explosionRadius * 2, explosionRadius * 2)
        }
    }

    override fun update() {
        counter++
        if (!exploding) {
            if (counter < airtime)
                moveOnGrid(x + speed * cos(direction), y + speed * sin(direction), grid = GameLevel.grid.map)
            if (counter >= fuse) {
                exploding = true
                counter = 0
            }
        } else {
            if (counter >= explodeTime) {
                this.dead = true
            } else {
                for (junker in LevelManager.current.currentGameObjects.map { o: IGameObject -> o as? Junker }.filter { j: Junker? -> j != null }) {
                    if (junker!!.collidesWithCircle(x + width, y + height, explosionRadius))
                        junker.kill()
                }
            }
        }
    }

}