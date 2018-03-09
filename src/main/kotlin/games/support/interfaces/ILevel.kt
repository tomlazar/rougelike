package games.support.interfaces

import javafx.stage.Stage

abstract class ILevel {

    abstract val WIDTH: Double
    abstract val HEIGHT: Double

    protected var gameObjects = mutableListOf<IGameObject>()
    protected var controllers = mutableListOf<IController>()
    protected var addLaterQueue = mutableListOf<IGameObject>()

    fun addLater(o: IGameObject) {
        addLaterQueue.add(o)
    }

    abstract fun buildScene(stage: Stage?)

    open fun render() {
        gameObjects.forEach({ c: IGameObject ->
            c.gc.save()
            c.render()
            c.gc.restore()
        })
    }

    open fun update() {
        val removeLaterQueue = mutableListOf<IGameObject>()
        gameObjects.forEach({ o: IGameObject ->
            o.update()
            if (o.dead)
                removeLaterQueue.add(o)
        })

        addLaterQueue.forEach { o: IGameObject -> gameObjects.add(o) }
        addLaterQueue.map { o: IGameObject -> o as? IController }
                .filter { c: IController? -> c != null }
                .forEach { c: IController? -> controllers.add(c!!) }
        addLaterQueue.clear()

        removeLaterQueue.forEach { o: IGameObject -> gameObjects.remove(o) }
    }
}