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
import javafx.scene.paint.Color
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


    var starting: Boolean? = false
    var fadeOut = 1.0

    lateinit var gameCanvas: Canvas
    lateinit var gameScene: SubScene

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
        gameCanvas = Canvas(Grid.mapFromGrid(map.size.toDouble()), Grid.mapFromGrid(if (map.isEmpty()) 0.0 else map[0].size.toDouble()))
        gameScene = SubScene(Group(gameCanvas), WIDTH, HEIGHT)
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
                    val target =
                            if (gridcell.targetName != null) {
                                val it = gameObjects.find { o -> o is Person && (o as Person).name.equals(gridcell.targetName) }!! as Person
                                it.speed = speed
                                it
                            } else
                                player

                    val junker =
                            when (gridcell.junkerSpawnType!!) {
                                BackgroundObject.JunkerType.SHIELD ->
                                    ShieldJunker(gameCanvas.graphicsContext2D,
                                            gridx.toDouble(), gridy.toDouble(),
                                            target, speed / 3)
                                BackgroundObject.JunkerType.NORMAL ->
                                    Junker(gameCanvas.graphicsContext2D,
                                            gridx.toDouble(), gridy.toDouble(),
                                            target, speed)
                            }

                    gameObjects.add(junker)
                    junker.addEvents(gameScene.scene)
                }
                if (gridcell.personSpawnName != null) {
                    val person =
                            if (gridcell.patrolPoints.isEmpty())
                                Person(gameCanvas.graphicsContext2D, gridcell.personSpawnName!!)
                            else
                                PatrolPerson(gameCanvas.graphicsContext2D, gridcell.personSpawnName!!, gridcell.patrolPoints.toTypedArray())
                    gameObjects.add(person)
                    person.gridx = gridx.toDouble()
                    person.gridy = gridy.toDouble()
                }
                if (gridcell.isEquipmentSpawn) {
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
        suspend()
    }

    override fun start(stage: Stage?) {
        stage!!.scene = this.scene
        stage.width = WIDTH
        stage.height = HEIGHT + HUD.HEIGHT
        this.render()
        stage.show()
        this.update()
        starting = true
        loop.play()
    }

    var stopCallback = {}
    override fun stop(callback: () -> Unit) {
        suspend()
        starting = false
        stopCallback = callback
    }

    override fun render() {
        val fadeColor = Color(0.0, 0.0, 0.0, fadeOut)
        scene.fill = fadeColor
        gameScene.fill = fadeColor

        val gc = gameCanvas.graphicsContext2D
        gc.fill = fadeColor
        gc.fillRect(0.0, 0.0, grid.mapWidth * Grid.cellSize, grid.mapHeight * Grid.cellSize)

        grid.render(fadeOut)
        super.render()

        scene.fill = fadeColor
        gameScene.fill = fadeColor

        gc.fill = fadeColor
        gc.fillRect(0.0, 0.0, grid.mapWidth * Grid.cellSize, grid.mapHeight * Grid.cellSize)
    }

    override fun update() {
        super.update()
        if (starting != null) {
            if (starting!!) {
                fadeOut -= 1.0 / FPS
                if (fadeOut <= 0.0) {
                    fadeOut = 0.0
                    resume()
                    starting = null
                }
            } else {
                fadeOut += 1.0 / FPS
                if (fadeOut >= 1.0) {
                    fadeOut = 1.0
                    starting = null
                    loop.pause()
                    stopCallback.invoke()
                }
            }
        }
    }
}