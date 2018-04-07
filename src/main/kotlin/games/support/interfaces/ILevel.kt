package games.support.interfaces

import games.rougelike.objects.Effects
import games.rougelike.objects.HUD
import games.rougelike.objects.Player
import games.support.Grid
import games.support.TrackingCamera
import javafx.animation.Timeline
import javafx.scene.Scene
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

        // TODO: Add the "auxillary updates" for the dialog system here
    }

    fun suspend() {
        isSuspended = true
    }

    fun resume() {
        isSuspended = false
    }

    fun toggleSuspended() {
        isSuspended = !isSuspended
    }

    enum class PromptType {
        MESSAGE, NARRATION, THINKING, DIALOGUE, TERMINAL;
    }

    class Prompt(val type: PromptType, val text: String)

    fun showPrompts(vararg prompts: Prompt, callback: (() -> Unit)? = null) {
        suspend()

        Thread({
            // TODO: display prompts, and wait for them to be done
            for (prompt in prompts) {
                println("${prompt.type.name.toLowerCase().capitalize()}: ${prompt.text}")
                Thread.sleep(1000)
            }
            Thread.sleep(1000)

            callback?.invoke()
            resume()
        }).start()
    }
}