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
    abstract fun stop(callback: () -> Unit)

    private var suspendCounter = 0
    val isSuspended get() = suspendCounter > 0

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
        } else
            gameObjects.forEach { o -> (o as? TrackingCamera)?.update() }

        addLaterQueue.forEach { o: IGameObject -> gameObjects.add(o) }
        addLaterQueue.map { o: IGameObject -> o as? IController }
                .filter { c: IController? -> c != null }
                .forEach { c: IController? -> controllers.add(c!!) }
        addLaterQueue.clear()

        removeLaterQueue.forEach { o: IGameObject -> gameObjects.remove(o) }
    }

    fun suspend() {
        LevelManager.inputManager.clear()
        suspendCounter++
    }

    fun resume() {
        suspendCounter--
    }

    fun toggleSuspended() {
        if (isSuspended)
            resume()
        else
            suspend()
    }

    fun showPrompts(vararg prompts: () -> DialogBuilder.Prompt, callback: (() -> Any)? = null) {
        if (prompts.isEmpty())
            return

        suspend()
        showPromptsStartingWith(0, prompts, {
            callback?.invoke()
            resume()
        })
    }

    private fun showPromptsStartingWith(i: Int, prompts: Array<out () -> DialogBuilder.Prompt>, callback: (() -> Any)) {
        if (i >= prompts.size) {
            callback.invoke()
        } else {
            val prompt = prompts[i].invoke()
            val notification = prompt.build()

            notification.setOnCloseRequest {
                showPromptsStartingWith(i + 1, prompts, callback)
                return@setOnCloseRequest
            }

            notification.show()
        }
    }
}