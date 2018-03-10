package games.rougelike.levels

import games.rougelike.FPS
import games.rougelike.objects.*
import games.support.*
import games.support.interfaces.IController
import games.support.interfaces.IGameObject
import games.support.interfaces.ILevel
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.SubScene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.Background
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Duration
import java.io.File
import java.util.*

class GameLevel : ILevel() {

    companion object {
        val HEIGHT = 800.0
        val WIDTH = 600.0

        val keybank = KeyBank()
        val mousebank = MouseBank()

        const val NAME: String = ""
        val LEVEL_REGEX = "Level([0-9]+)\\.csv".toRegex()

        val levels = File(".").listFiles().filter { f: File -> f.isFile && LEVEL_REGEX.matches(f.name) }.map { f: File ->
            val it = GameLevel()
            it.build(f.name)
            it
        }

        fun getLevel(id: Int) = levels.find { l: GameLevel -> l.levelId == id }
    }

    var levelId: Int = -1

    fun build(gridfile: String) {
        // Create the main game window
        levelId = LEVEL_REGEX.matchEntire(gridfile)!!.groupValues[1].toInt()
        val map = Util.transpose(CsvReader.readCsv(gridfile))
                .map { row: Array<String> ->
                    row.map { cell: String ->
                        BackgroundObject.fromCode(cell)
                    }.toTypedArray()
                }.toTypedArray()
        val gameCanvas = Canvas(Grid.mapFromGrid(map.size.toDouble()), Grid.mapFromGrid(map[0].size.toDouble()))
        val gameScene = SubScene(Group(gameCanvas), WIDTH, HEIGHT)
        player = Player(gameCanvas.graphicsContext2D)
        player.x = Grid.mapFromGrid(2.0)
        player.y = Grid.mapFromGrid(6.0)
        camera = TrackingCamera(gameCanvas.graphicsContext2D, player)
        gameScene.camera = camera.sceneCamera
        grid = Grid(gameCanvas.graphicsContext2D, map)
        effects = Effects(gameCanvas.graphicsContext2D)

        // Create the hud window
        val hudCanvas = Canvas(HUD.WIDTH, HUD.HEIGHT)
        val gameHud = SubScene(VBox(hudCanvas), HUD.WIDTH, HUD.HEIGHT)
        hud = HUD(hudCanvas.graphicsContext2D)

        // finialize layout
        val layout = VBox(gameHud, gameScene)

        scene = Scene(layout)
        //stage.scene = scene

        // create evil bots
        for (i in 1..4) {
            var junker = Junker(gameCanvas.graphicsContext2D,
                    (5 + (Random().nextInt(grid.mapWidth - 5))).toDouble(),
                    (5 + (Random().nextInt(grid.mapHeight - 5))).toDouble(),
                    if (i <= 2) player else gameObjects.last(),
                    Grid.cellSize * (Random().nextDouble() + (if (i <= 2) 2.0 else 0.5)))
            gameObjects.add(junker)
            junker.addEvents(gameScene.scene)
        }

        // add objects to list
        listOf(hud, effects, player, camera).forEach { o: IGameObject -> gameObjects.add(o) }

        // add events
        listOf(keybank, mousebank, effects).forEach { c: IController -> c.addEvents(scene) }
        player.addEvents(gameScene.scene)

        // set up update
        val kf = KeyFrame(Duration(1000 / FPS), EventHandler { update(); render() })

        loop = Timeline(kf)
        loop.cycleCount = Animation.INDEFINITE
    }

    override fun start(stage: Stage?) {
        loop.play()
        stage!!.scene = this.scene
        stage.width = WIDTH
        stage.height = HEIGHT + HUD.HEIGHT
        stage.show()
    }

    override fun stop() {
        loop.pause()
    }

    override fun render() {
        grid.render()
        super.render()
    }
}