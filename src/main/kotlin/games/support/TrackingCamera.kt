package games.support

import javafx.scene.*
import javafx.scene.canvas.GraphicsContext
import games.rougelike.*
import javafx.geometry.Point3D

class TrackingCamera(trackingObject: IGameObject? = null) : IGameController() {

    val sceneCamera = PerspectiveCamera()
    var trackingObject: IGameObject? = trackingObject

    init {
        sceneCamera.rotationAxis = Point3D(1.0, 0.0, 0.0)
        sceneCamera.rotate = 35.0
    }

    override fun render(gc: GraphicsContext) {}

    val zoomSpeed = 200.0

    override fun update() {
        if (trackingObject != null) {
            sceneCamera.translateX = trackingObject!!.x - (GAME_WIDTH / 2)
            sceneCamera.translateY = trackingObject!!.y - (GAME_HEIGHT / 2)
            sceneCamera.translateZ += zoomSpeed / FPS * keybank.keyNegPos(key_zoom_out, key_zoom_in)
        }
    }

}