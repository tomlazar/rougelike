package games.rougelike

import games.support.Grid
import games.support.IController
import games.support.IGameObject
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration


const val APP_NAME: String = ""
const val FPS = 25.0
const val WIDTH = 600.0
const val HEIGHT = 500.0
val BACKGROUND = Color.LIGHTBLUE!!

val grid = Grid()
val gameObjects = mutableListOf<IGameObject>()
val controllers = listOf<IController>()
val addLaterQueue = mutableListOf<IGameObject>()

class Main : Application() {

    override fun start(stage: Stage?) {
        stage!!.title = APP_NAME

        val root = Group()

        val scene = Scene(root)
        stage.scene = scene

        controllers.forEach { c: IController -> c.addEvents(scene) }

        val canvas = Canvas(WIDTH, HEIGHT)
        root.children.add(canvas)

        val kf = KeyFrame(Duration(1000 / FPS), EventHandler { update(); render(canvas.graphicsContext2D) })

        val loop = Timeline(kf)
        loop.cycleCount = Animation.INDEFINITE
        loop.play()

        stage.show()
    }

    private fun update() {

        val removeLaterQueue = mutableListOf<IGameObject>()
        gameObjects.forEach({ o: IGameObject ->
            o.update()
            if (o.dead)
                removeLaterQueue.add(o)
        })

        addLaterQueue.forEach { o: IGameObject -> gameObjects.add(o) }
        addLaterQueue.clear()

        removeLaterQueue.forEach { o: IGameObject -> gameObjects.remove(o) }
    }

    private fun render(gc: GraphicsContext) {
        gc.fill = BACKGROUND
        gc.fillRect(0.0, 0.0, WIDTH, HEIGHT)

        grid.render(gc, 30.0, 0.0, grid.width, grid.width)
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}