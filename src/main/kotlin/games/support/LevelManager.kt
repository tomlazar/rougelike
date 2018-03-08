package games.support

import games.support.interfaces.ILevel
import javafx.stage.Stage

class LevelManager {

    companion object {
        var stage: Stage? = null

        private var level: ILevel? = null

        var current: ILevel
            get() = level!!
            set(value) {
                level = value
                level!!.buildScene(stage)
            }
    }
}