package games.support

import games.support.interfaces.ILevel
import javafx.scene.Node

object DialogBuilder {

    fun build(prompt: ILevel.Prompt): Iterable<Node> {
        return emptyList()
    }

    enum class PromptType {
        MESSAGE, NARRATION, THINKING, DIALOGUE, TERMINAL;
    }
}