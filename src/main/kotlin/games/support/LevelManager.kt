package games.support

import games.support.interfaces.ILevel
import javafx.stage.Stage

class LevelManager {

    companion object {
        val inputManager = InputManager()

        var stage: Stage? = null

        private var level: ILevel? = null

        var current: ILevel
            get() = level!!
            set(value) {
                if (level != null) {
                    level!!.stop()
                }
                level = value
                value.start(stage)
            }
    }
}