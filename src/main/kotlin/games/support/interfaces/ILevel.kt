package games.support.interfaces

import games.rougelike.objects.Effects
import games.rougelike.objects.HUD
import games.rougelike.objects.Player
import games.support.DialogBuilder
import games.support.Grid
import games.support.LevelManager
import games.support.TrackingCamera
import javafx.animation.Timeline
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.stage.Stage

abstract class ILevel {

    protected var gameObjects = mutableListOf<IGameObject>()
    val currentGameObjects get() = gameObjects.toMutableList()
    protected var controllers = mutableListOf<IController>()
    protected var addLaterQueue = mutableListOf<IGameObject>()

    lateinit var player: Player
    lateinit var hud: HUD
    lateinit var effects: Effects
    lateinit var grid: Grid
    lateinit var camera: TrackingCamera
    lateinit var scene: Scene
    lateinit var loop: Timeline

    fun addLater(o: IGameObject) {
        addLaterQueue.add(o)
    }

    abstract fun start(stage: Stage?)
    abstract fun stop()

    var isSuspended = false

    open fun render() {
        gameObjects.forEach({ c: IGameObject ->
            c.gc.save()
            c.render()
            c.gc.restore()
        })
    }

    open fun update() {
        val removeLaterQueue = mutableListOf<IGameObject>()
        if (!isSuspended) {
            gameObjects.forEach({ o: IGameObject ->
                o.update()
                if (o.dead)
                    removeLaterQueue.add(o)
            })
        }

        addLaterQueue.forEach { o: IGameObject -> gameObjects.add(o) }
        addLaterQueue.map { o: IGameObject -> o as? IController }
                .filter { c: IController? -> c != null }
                .forEach { c: IController? -> controllers.add(c!!) }
        addLaterQueue.clear()

        removeLaterQueue.forEach { o: IGameObject -> gameObjects.remove(o) }
    }

    fun suspend() {
        LevelManager.inputManager.clear()
        loop.pause()
        isSuspended = true
    }

    fun resume() {
        loop.play()
        isSuspended = false
    }

    fun toggleSuspended() {
        isSuspended = !isSuspended
    }

    data class Prompt(val type: DialogBuilder.PromptType, val text: String)

    fun showPrompts(vararg prompts: Prompt, callback: (() -> Unit)? = null) {
        if (prompts.isEmpty())
            return

        suspend()
        var firstAlert : Alert? = null
        var prevAlert : Alert? = null
        for (prompt in prompts) {
            val notification = Alert(Alert.AlertType.INFORMATION)
            notification.title = prompt.type.name.toLowerCase().capitalize()
            notification.headerText = when(prompt.type) {
                DialogBuilder.PromptType.DIALOGUE -> {
                     prompt.text.substring(0, prompt.text.indexOf(':') + 1)
                }
                DialogBuilder.PromptType.THINKING -> "Dennis: (thinking)"
                else -> ""
            }
            notification.contentText = when (prompt.type) {
                DialogBuilder.PromptType.DIALOGUE -> prompt.text.substring(prompt.text.indexOf(':') + 2)
                else -> prompt.text
            }
            notification.dialogPane.style = ".dialog-pane > .content.label {-fx-font-style: italic;}"
            //notification.dialogPane.children.addAll(DialogBuilder.build())

            if (firstAlert == null)
                firstAlert = notification
            if (prevAlert != null) {
                prevAlert.setOnCloseRequest {
                    notification.show()
                    return@setOnCloseRequest
                }
            }
            prevAlert = notification
        }
        // last alert
        prevAlert!!.setOnCloseRequest {
            callback?.invoke()
            resume()
            return@setOnCloseRequest
        }

        // start the chain
        firstAlert!!.show()
    }
}