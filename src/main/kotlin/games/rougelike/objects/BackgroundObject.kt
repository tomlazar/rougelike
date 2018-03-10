package games.rougelike.objects

import games.support.Grid
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class BackgroundObject(type: BackgroundType = BackgroundType.GAP) {

    var type = type

    enum class BackgroundType(var id: Int, var traversable: Boolean, var fill: Color) {
        DEFAULT(-1, true, Color.PINK),
        GAP(0, false, Color.WHITE),
        FLOOR(1, true, Color.GREEN),
        WALL(2, false, Color.BLACK),
        STAIR_UP(4, true, Color.AQUA),
        STAIR_DOWN(5, true, Color.AZURE),
        DOOR(6, true, Color.AQUA),
        BANISTER_XY(10, false, Color.DARKSLATEGRAY),
        BANISTER_X(11, false, Color.DARKSLATEGRAY),
        BANISTER_Y(12, false, Color.DARKSLATEGRAY)
        ;

        companion object {
            fun fromId(id: Int): BackgroundType {
                var it = BackgroundType.values().find { t: BackgroundType -> t.id == id }
                return if (it != null) it else BackgroundType.DEFAULT
            }
        }
    }

    fun render(gc: GraphicsContext, x: Double, y: Double) {
        gc.fill = this.type.fill
        when (this.type) {
            BackgroundType.GAP -> {
                gc.stroke = Color.TRANSPARENT
            }
            else -> gc.stroke = Color.BLACK
        }

        gc.fillRect(x, y, Grid.cellSize, Grid.cellSize)
        gc.strokeRect(x, y, Grid.cellSize, Grid.cellSize)
    }

}