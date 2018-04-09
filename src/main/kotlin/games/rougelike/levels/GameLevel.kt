package games.rougelike.levels

import games.rougelike.FPS
import games.rougelike.objects.*
import games.rougelike.objects.enemies.Junker
import games.rougelike.objects.enemies.ShieldJunker
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
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Duration
import java.io.File
import java.util.*

class GameLevel : ILevel() {

    companion object {
        val HEIGHT = 800.0
        val WIDTH = 600.0

        const val NAME: String = ""
        val LEVEL_REGEX = ".*Level([^\\.]+)\\.csv".toRegex()

        val levels = File("Levels").listFiles().filter { f: File -> f.isFile && LEVEL_REGEX.matches(f.name) }.map { f: File ->
            val it = GameLevel()
            it.build(f.canonicalPath)
            it
        }

        fun getLevel(id: String) = levels.find { l: GameLevel -> l.levelId.equals(id) }
    }

    var levelId: String = ""

    fun build(gridfile: String) {
        println("Loading level: $gridfile")

        // Create the main game window
        levelId = LEVEL_REGEX.matchEntire(gridfile)!!.groupValues[1]
        val map = Util.transpose(Util.readCsv(gridfile))
                .map { row: Array<String> ->
                    row.map { cell: String ->
                        BackgroundObject.fromCode(cell)
                    }.toTypedArray()
                }.toTypedArray()
        val gameCanvas = Canvas(Grid.mapFromGrid(map.size.toDouble()), Grid.mapFromGrid(if (map.isEmpty()) 0.0 else map[0].size.toDouble()))
        val gameScene = SubScene(Group(gameCanvas), WIDTH, HEIGHT)
        player = Player(gameCanvas.graphicsContext2D)
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

        // apply grid properties
        for (gridx in 0 until grid.map.size) {
            for (gridy in 0 until grid.map[gridx].size) {
                val gridcell = grid.map[gridx][gridy]
                if (gridcell.isPlayerSpawn) {
                    player.gridx = gridx.toDouble()
                    player.gridy = gridy.toDouble()
                }
                if (gridcell.junkerSpawnType != null) {
                    val speed = Grid.cellSize * (Random().nextDouble() + 1.5)
                    val junker =
                            when (gridcell.junkerSpawnType!!) {
                                BackgroundObject.JunkerType.SHIELD ->
                                    ShieldJunker(gameCanvas.graphicsContext2D,
                                            gridx.toDouble(), gridy.toDouble(),
                                            player, speed / 3)
                                BackgroundObject.JunkerType.NORMAL ->
                                    Junker(gameCanvas.graphicsContext2D,
                                            gridx.toDouble(), gridy.toDouble(),
                                            player, speed)
                            }

                    gameObjects.add(junker)
                    junker.addEvents(gameScene.scene)
                }
                if (gridcell.equipmentSpawn != null) {
                    gameObjects.add(Equipment(gameCanvas.graphicsContext2D, gridx, gridy, gridcell.eventTriggers))
                    gridcell.eventTriggers = mutableListOf()
                }
            }
        }

        // add objects to list
        listOf(hud, effects, player, camera).forEach { o: IGameObject -> gameObjects.add(o) }

        // add events
        listOf(LevelManager.inputManager, effects).forEach { c: IController -> c.addEvents(gameScene.scene) }
        player.addEvents(gameScene.scene)

        // set up update
        val kf = KeyFrame(Duration(1000 / FPS), EventHandler { update(); render() })

        loop = Timeline(kf)
        loop.cycleCount = Animation.INDEFINITE
    }

    override fun start(stage: Stage?) {
        stage!!.scene = this.scene
        stage.width = WIDTH
        stage.height = HEIGHT + HUD.HEIGHT
        this.update()
        this.render()
        stage.show()
        loop.play()
    }

    override fun stop() {
        loop.pause()
    }

    override fun render() {
        grid.render()
        super.render()
    }
}