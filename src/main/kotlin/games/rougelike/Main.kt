package games.rougelike

import games.support.*
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.SubScene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Duration
import java.util.*


const val APP_NAME: String = ""
const val FPS = 25.0
const val GAME_WIDTH = 800.0
const val GAME_HEIGHT = 600.0

val grid = Grid()
val keybank = KeyBank()
val hud = HUD()

val player = Player()
val camera = TrackingCamera(player)

val gameObjects = mutableListOf<IGameObject>(player, camera)
val controllers = mutableListOf<IController>(keybank)
val addLaterQueue = mutableListOf<IGameObject>()


class Main : Application() {

    override fun start(stage: Stage?) {
        stage!!.title = APP_NAME

        // Create the main game window
        val gameCanvas = Canvas(Grid.mapFromGrid(Grid.mapWidth.toDouble()), Grid.mapFromGrid(Grid.mapHeight.toDouble()))
        val gameScene = SubScene(Group(gameCanvas), GAME_WIDTH, GAME_HEIGHT)
        gameScene.camera = camera.sceneCamera

        // Create the hud window
        val hudCanvas = Canvas(HUD.WIDTH, HUD.HEIGHT)
        val gameHud = SubScene(VBox(hudCanvas), HUD.WIDTH, HUD.HEIGHT)

        var layout = VBox(gameHud, gameScene)

        val scene = Scene(layout)
        buildWorld()
        stage.scene = scene

        controllers.forEach({ c: IController -> c.addEvents(scene) })

        val kf = KeyFrame(Duration(1000 / FPS), EventHandler { update(); render(gameCanvas.graphicsContext2D, hudCanvas.graphicsContext2D) })

        val loop = Timeline(kf)
        loop.cycleCount = Animation.INDEFINITE
        loop.play()

        stage.width = GAME_WIDTH
        stage.height = GAME_HEIGHT + HUD.HEIGHT
        stage.show()
    }

    private fun buildWorld() {
        gameObjects.add(player)
        gameObjects.add(camera)

        for (i in 1..4)
            gameObjects.add(
                    Junker((5 + (Random().nextInt(Grid.mapWidth - 5))).toDouble(),
                            (5 + (Random().nextInt(Grid.mapHeight - 5))).toDouble(),
                            if (i <= 2) player else gameObjects.last(),
                            Grid.cellSize * (Random().nextDouble() + (if (i <= 2) 2.0 else 0.5))))
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

    private fun render(gc: GraphicsContext, tgc: GraphicsContext) {
        /*
        gc.fill = BACKGROUND
        gc.fillRect(0.0, 0.0, GAME_WIDTH, GAME_HEIGHT)
        */
        hud.render(tgc)
        grid.render(gc)
        gameObjects.forEach({ o: IGameObject -> o.render(gc) })
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}