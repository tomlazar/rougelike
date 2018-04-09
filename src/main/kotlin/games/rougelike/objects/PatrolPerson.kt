package games.rougelike.objects

import games.rougelike.FPS
import games.support.Grid
import javafx.scene.canvas.GraphicsContext
import kotlin.math.abs
import kotlin.math.min

class PatrolPerson(gc: GraphicsContext, name: String, val patrolPoints: Array<Pair<Double, Double>>) : Person(gc, name) {

    var currentPatrolPoint = 0

    override fun update() {
        super.update()

        moving = true

        val pp = patrolPoints[currentPatrolPoint]
        when {
            gridx != pp.first -> {
                val dx = Grid.mapFromGrid(min(abs(pp.first - gridx), Grid.mapToGrid(speed / FPS))) * ((pp.first - gridx) / abs(pp.first - gridx))
                moveOnGrid(x + dx, y)
            }
            gridy != pp.second -> {
                val dy = Grid.mapFromGrid(min(abs(pp.second - gridy), Grid.mapToGrid(speed / FPS))) * ((pp.second - gridy) / abs(pp.second - gridy))
                moveOnGrid(x, y + dy)
            }
            else -> {
                currentPatrolPoint++
                currentPatrolPoint %= patrolPoints.size
            }
        }
    }
}