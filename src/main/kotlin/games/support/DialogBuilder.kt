package games.support

import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import java.io.File
import javax.imageio.ImageIO

object DialogBuilder {
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

        companion object {
            val personcache = mutableMapOf<String, ImageView>()

            fun generateperson(person: String): ImageView {
                val speakerPath = person.removePrefix("Dr. ")

                if (personcache.containsKey(speakerPath))
                    return personcache[speakerPath]!!

                val image = SwingFXUtils.toFXImage(ImageIO.read(File("Images/Head$speakerPath.png")), null)
                val imv = ImageView(image)

                imv.isPreserveRatio = true
                imv.fitHeight = 75.0

                personcache[speakerPath] = imv
                return imv
            }
        }

        override fun build(): Alert {
            val alert = super.build()


            alert.headerText = null
            alert.contentText = null

            val header = Text("$speaker: ")
            header.font = Font.font(24.0)

            val content = Text(text)
            content.wrappingWidth = 200.0

            val textGroup = VBox(header, content)
            val group = HBox(generateperson(speaker), textGroup)

            textGroup.padding = Insets(20.0)
            group.padding = Insets(20.0)

            alert.dialogPane.children.addAll(HBox(generateperson(speaker), textGroup))

            return alert
        }
    }


}