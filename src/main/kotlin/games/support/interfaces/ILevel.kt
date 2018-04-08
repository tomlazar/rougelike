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
        suspend()
        var active = prompts.count()
        for (prompt in prompts) {
            val notification = Alert(Alert.AlertType.INFORMATION)
            notification.headerText = null

//                when(prompt.type) {
////                    MESSAGE -> TODO()
////                    NARRATION ->
////                    THINKING ->
////                    DIALOGUE ->
////                    TERMINAL ->
////                }

            notification.contentText = prompt.text
            notification.dialogPane.children.addAll(DialogBuilder.build())

            notification.show()
            notification.setOnCloseRequest {
                active--

                if (active > 0)
                    return@setOnCloseRequest

                callback?.invoke()
                resume()
            }
        }
    }
}