package games.support.interfaces

import javafx.stage.Stage

abstract class ILevel {

    abstract val WIDTH: Double
    abstract val HEIGHT: Double

    protected var gameObjects = mutableListOf<IGameObject>()
    protected var controllers = mutableListOf<IController>()
    protected var addLaterQueue = mutableListOf<IGameObject>()

    abstract fun buildScene(stage: Stage?)

    open fun render() {
        gameObjects.map { c: IGameObject -> c.render() }
    }

    open fun update() {
        val removeLaterQueue = mutableListOf<IGameObject>()
        gameObjects.forEach({ o: IGameObject ->
            o.update()
            if (o.dead)
                removeLaterQueue.add(o)
        })

        addLaterQueue.forEach { o: IGameObject -> gameObjects.add(o) }
        addLaterQueue.clear()

        removeLaterQueue.forEach { o: IGameObject -> gameObjects.remove(o) }    }
}