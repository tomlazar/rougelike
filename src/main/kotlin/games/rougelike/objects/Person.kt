package games.rougelike.objects

import games.rougelike.FPS
import games.support.Grid
import games.support.interfaces.IGameObject
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.transform.Rotate
import javax.imageio.ImageIO
import java.io.*
import java.lang.Math.pow
import kotlin.math.abs

open class Person(gc: GraphicsContext, val name: String, val bodyColor: Color = Color.PEACHPUFF) : IGameObject(gc) {
    var speed = Grid.cellSize * 4

    override var height: Double = 30.0
    override var width: Double = 15.0
    override var x: Double = 0.0
    override var y: Double = 0.0
    val headWidth = 25.0
    val headHeight = 30.0

    val head: Image? = try {
        SwingFXUtils.toFXImage(ImageIO.read(File("Images/Head$name.png")), null)
    } catch (e: IOException) {
        null
    }

    var bounceProgress = 0.0
    val bounceSpeed = (1 / 0.3) / FPS

    val bounce = 15.0
    var rotatingRight = true
    val rotateDistance = 10.0

    var moving = false

    override fun render() {
        val r = Rotate(bounceProgress * rotateDistance, this.x + width / 2, this.y + height)
        gc.setTransform(r.mxx, r.myx, r.mxy, r.myy, r.tx, r.ty - bounce * (1.0 - pow(1.0 - (abs(bounceProgress) / rotateDistance), 2.0)))

        gc.fill = bodyColor
        gc.fillRoundRect(x, y - 10.0, width, height + 10.0, 10.0, 10.0)
        if (head != null) {
            val realHeight = head.height * (headWidth / head.width)
            gc.drawImage(head, x + width / 2 - headWidth / 2, y - 10.0 - realHeight / 2, headWidth, realHeight)
        } else {
            gc.fill = Color.GOLD
            gc.stroke = bodyColor.darker()
            gc.lineWidth = 3.0
            gc.fillOval(x + width / 2 - headWidth / 2, y - 10.0 - headHeight / 2, headWidth, headHeight)
            gc.strokeOval(x + width / 2 - headWidth / 2, y - 10.0 - headHeight / 2, headWidth, headHeight)
        }
    }

    override fun update() {
        if (moving) {
            bounceProgress += (if (rotatingRight) 1.0 else -1.0) * bounceSpeed * 2.0
            if (abs(bounceProgress) >= 1.0) {
                bounceProgress /= abs(bounceProgress)
                rotatingRight = !rotatingRight
            }
        } else
            bounceProgress = 0.0
    }
}