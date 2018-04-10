package games.support

import javafx.scene.Node
import javafx.scene.control.Alert

object DialogBuilder {

    fun build(prompt: Prompt): Iterable<Node> {
        return emptyList()
    }

    enum class PromptType {
        MESSAGE, NARRATION, THINKING, DIALOGUE, TERMINAL;
    }

    open class Prompt(val type: PromptType, val text: String) {
        open fun build(): Alert {
            val notification = Alert(Alert.AlertType.INFORMATION)
            notification.title = type.name.toLowerCase().capitalize()
            notification.headerText = if (type == PromptType.THINKING) "Dennis: (thinking)" else ""
            notification.contentText = text

            notification.dialogPane.style = ".dialog-pane > .content.label {-fx-font-style: italic;}"

            return notification
        }
    }

    open class DialogPrompt(val speaker: String, text: String) : Prompt(PromptType.DIALOGUE, text) {
        override fun build(): Alert {
            val alert = super.build()

            alert.headerText = "$speaker: "

            return alert
        }
    }


}