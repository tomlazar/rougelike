package games.rougelike

import games.rougelike.levels.GameLevel
import games.support.LevelManager
import javafx.application.Application
import javafx.stage.Stage

const val FPS = 25.0

class Main : Application() {
    override fun start(stage: Stage?) {
        LevelManager.stage = stage
        stage!!.title = "ROUGELIKE THE MSCS ADVENTURE PARTY HAPPY FUNTIME GAME"
        LevelManager.current = GameLevel.getLevel("Pit")!!
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}