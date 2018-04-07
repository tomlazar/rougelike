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

    var orientations = mutableListOf<Orientation>()


    enum class BackgroundType(val id: Int, val traversable: Boolean, val fill: Color, val fill2: Color = Color.TRANSPARENT) {
        DEFAULT(-1, true, Color.PINK),
        GAP(0, false, Color.WHITE),
        FLOOR(1, true, Color.GREEN),
        WALL(2, false, Color.BLACK),
        STAIR_DOWN(4, true, Color.AZURE),
        STAIR_UP(5, true, Color.AZURE),
        DOOR(6, true, Color.AZURE),
        BANISTER(10, false, Color.TAN, Color.SADDLEBROWN.darker()),
        DESK(13, false, Color.LIGHTSLATEGRAY, Color.DIMGRAY)
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
                        when (split[1].toInt()) {
                            0 -> it.equipmentSpawn = Equipment.EquipmentType.HACK
                            1 -> it.equipmentSpawn = Equipment.EquipmentType.GRENADE
                        }
                    }
                    4 -> {
                        for (i in split.slice(1 until split.size)) {
                            val o = Orientation.values().find { o -> o.id == i.toInt() }
                            if (o != null)
                                it.orientations.add(o)
                        }
                    }
                }
            }

            return it
        }
    }

    enum class Orientation(val id: Int) {
        NORTH(0), EAST(1), SOUTH(2), WEST(3), NORTH_EAST(4), SOUTH_EAST(5), SOUTH_WEST(6), NORTH_WEST(7);
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


        // second layer
        gc.fill = this.type.fill2
        when (this.type) {
            BackgroundType.BANISTER -> {
                if (orientations.size > 0)
                    gc.fillRect(x + Grid.cellSize / 4, y + Grid.cellSize / 4, Grid.cellSize / 2, Grid.cellSize / 2)
                for (o in orientations) {
                    when (o) {
                        Orientation.NORTH -> gc.fillRect(x + Grid.cellSize / 4, y, Grid.cellSize / 2, Grid.cellSize / 2)
                        Orientation.EAST -> gc.fillRect(x + Grid.cellSize / 2, y + Grid.cellSize / 4, Grid.cellSize / 2, Grid.cellSize / 2)
                        Orientation.SOUTH -> gc.fillRect(x + Grid.cellSize / 4, y + Grid.cellSize / 2, Grid.cellSize / 2, Grid.cellSize / 2)
                        Orientation.WEST -> gc.fillRect(x, y + Grid.cellSize / 4, Grid.cellSize / 2, Grid.cellSize / 2)
                        else -> {
                        }
                    }
                }
            }
            else -> {
            }
        }

    }

}