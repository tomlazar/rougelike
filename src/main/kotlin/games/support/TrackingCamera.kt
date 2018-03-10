package games.support

import javafx.scene.*
import games.rougelike.*
import games.rougelike.levels.GameLevel
import games.support.interfaces.IGameObject
import javafx.geometry.Point3D
import javafx.scene.canvas.GraphicsContext

class TrackingCamera(gc: GraphicsContext, private var trackingObject: IGameObject? = null) : IGameObject(gc) {
    override var height = 0.0
    override var width = 0.0
    override var x = 0.0
    override var y = 0.0

    override fun render() {}

    val sceneCamera = PerspectiveCamera()
    val zoomSpeed = 200.0

    init {
//        sceneCamera.rotationAxis = Point3D(1.0, 0.0, 0.0)
//        sceneCamera.rotate = 35.0
    }

    override fun update() {
        if (trackingObject != null) {
            sceneCamera.translateX = trackingObject!!.x - (GameLevel.WIDTH / 2)
            sceneCamera.translateY = trackingObject!!.y - (GameLevel.HEIGHT / 2)
            sceneCamera.translateZ += zoomSpeed / FPS * GameLevel.keybank.keyNegPos(key_zoom_out, key_zoom_in)
        }
    }

}