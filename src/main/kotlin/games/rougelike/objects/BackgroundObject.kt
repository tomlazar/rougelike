package games.rougelike.objects

import games.support.Grid
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class BackgroundObject(var type: BackgroundType = BackgroundType.GAP) {

    var teleporter: TeleportLocation? = null

    class TeleportLocation(val level: String, val gridx: Double, val gridy: Double)

    var isPlayerSpawn = false

    var isJunkerSpawn = false
    var isShieldJunkerSpawn = false

    var equipmentSpawn: Equipment.EquipmentType? = null


    enum class BackgroundType(var id: Int, var traversable: Boolean, var fill: Color) {
        DEFAULT(-1, true, Color.PINK),
        GAP(0, false, Color.WHITE),
        FLOOR(1, true, Color.GREEN),
        WALL(2, false, Color.BLACK),
        STAIR_DOWN(4, true, Color.AZURE),
        STAIR_UP(5, true, Color.AZURE),
        DOOR(6, true, Color.AZURE),
        BANISTER_XY(10, false, Color.DARKSLATEGRAY),
        BANISTER_X(11, false, Color.DARKSLATEGRAY),
        BANISTER_Y(12, false, Color.DARKSLATEGRAY)
        ;
    }

    companion object {
        fun fromCode(code: String): BackgroundObject {
            val split = code.split(';')
            val id = split[0].toInt()
            val type_ = BackgroundType.values().find { t: BackgroundType -> t.id == split[0].toInt() }
            val type = if (type_ != null) type_ else BackgroundType.DEFAULT
            val it = BackgroundObject(type)

            split.slice(1 until split.size).forEach { property ->
                val split = property.split('_')
                when (split[0].toInt()) {
                    0 -> { // teleporter tile
                        it.teleporter = TeleportLocation(split[1], split[2].toDouble(), split[3].toDouble())
                    }
                    1 -> {
                        it.isJunkerSpawn = true
                        if (split[1].toInt() == 1)
                            it.isShieldJunkerSpawn = true
                    }
                    2 -> {
                        it.isPlayerSpawn = true
                    }
                    3 -> {
                        when (split[1].toInt()){
                            0 -> it.equipmentSpawn = Equipment.EquipmentType.HACK
                            1 -> it.equipmentSpawn = Equipment.EquipmentType.GRENADE
                        }
                    }
                }
            }

            return it
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

        if (this.teleporter != null)
            gc.fill = Color.AQUA

        gc.fillRect(x, y, Grid.cellSize, Grid.cellSize)
        gc.strokeRect(x, y, Grid.cellSize, Grid.cellSize)
    }

}