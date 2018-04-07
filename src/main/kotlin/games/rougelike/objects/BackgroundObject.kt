package games.rougelike.objects

import games.support.Grid
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class BackgroundObject(var type: BackgroundType = BackgroundType.GAP) {

    var teleporter: TeleportLocation? = null

    class TeleportLocation(val level: String, val gridx: Double, val gridy: Double)

    var isPlayerSpawn = false

    var junkerSpawnType: JunkerType? = null

    var equipmentSpawn: Equipment.EquipmentType? = null

    var orientations = mutableListOf<Orientation>()

    var push: PushLocation? = null

    class PushLocation(val gridx: Double, val gridy: Double)

    enum class BackgroundType(val id: Int, val traversable: Boolean, val fill: Color, val fill2: Color = Color.TRANSPARENT) {
        DEFAULT(-1, true, Color.PINK),
        GAP(0, false, Color.WHITE),
        FLOOR(1, true, Color.GREEN),
        WALL(2, false, Color.BLACK),
        STAIR_DOWN(4, true, Color.AZURE),
        STAIR_UP(5, true, Color.AZURE),
        DOOR(6, true, GAP.fill, Color.AQUA),
        BANISTER(10, false, Color.TAN, Color.SADDLEBROWN.darker()),
        WINDOW(11, false, FLOOR.fill, Color.SILVER),
        WALL_WINDOW(12, false, FLOOR.fill, Color.SILVER),
        DESK(13, false, Color.LIGHTSLATEGRAY, Color.DIMGRAY)
        ;

        companion object {
            fun fromId(id: Int) = BackgroundType.values().find { t: BackgroundType -> t.id == id }
        }
    }

    enum class BackgroundOption(val id: Int) {
        TELEPORTER(0),
        JUNKER_SPAWN(1),
        PLAYER_SPAWN(2),
        EQUIPMENT_SPAWN(3),
        ORIENTATION(4),
        PUSHABLE(5),
        ;

        companion object {
            fun fromId(id: Int) = BackgroundOption.values().find { t: BackgroundOption -> t.id == id }
        }
    }

    enum class JunkerType(val id: Int) {
        NORMAL(0), SHIELD(1);

        companion object {
            fun fromId(id: Int) = JunkerType.values().find { t: JunkerType -> t.id == id }
        }
    }

    enum class Orientation(val id: Int) {
        NORTH(0), EAST(1), SOUTH(2), WEST(3);

        companion object {
            fun fromId(id: Int) = Orientation.values().find { t: Orientation -> t.id == id }
        }
    }

    companion object {
        fun fromCode(code: String): BackgroundObject {
            val split = code.split(';')
            val type = BackgroundType.fromId(split[0].toInt()) ?: BackgroundType.DEFAULT
            val it = BackgroundObject(type)

            split.slice(1 until split.size).forEach { property ->
                val split = property.split('_')
                when (BackgroundOption.fromId(split[0].toInt())) {
                    BackgroundOption.TELEPORTER -> { // teleporter tile
                        it.teleporter = TeleportLocation(split[1], split[2].toDouble(), split[3].toDouble())
                    }
                    BackgroundOption.JUNKER_SPAWN -> {
                        it.junkerSpawnType = JunkerType.fromId(split[1].toInt())
                    }
                    BackgroundOption.PLAYER_SPAWN -> {
                        it.isPlayerSpawn = true
                    }
                    BackgroundOption.EQUIPMENT_SPAWN -> {
                        it.equipmentSpawn = Equipment.EquipmentType.fromId(split[1].toInt())
                    }
                    BackgroundOption.ORIENTATION -> {
                        it.orientations.addAll(
                                split.slice(1 until split.size)
                                        .map({ x -> Orientation.fromId(x.toInt())!! })
                        )
                    }
                    BackgroundOption.PUSHABLE -> {
                        it.push = PushLocation(split[1].toDouble(), split[2].toDouble())
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

        //if (this.teleporter != null)
        //    gc.fill = Color.AQUA

        gc.fillRect(x, y, Grid.cellSize, Grid.cellSize)
        if (this.type.traversable)
            gc.strokeRect(x, y, Grid.cellSize, Grid.cellSize)


        // second layer
        gc.fill = this.type.fill2
        when (this.type) {
            BackgroundType.BANISTER, BackgroundType.WINDOW -> {
                if (orientations.size > 0)
                    gc.fillRect(x + Grid.cellSize / 4, y + Grid.cellSize / 4, Grid.cellSize / 2, Grid.cellSize / 2)
                for (o in orientations) {
                    when (o) {
                        Orientation.NORTH -> gc.fillRect(x + Grid.cellSize / 4, y, Grid.cellSize / 2, Grid.cellSize / 2)
                        Orientation.EAST -> gc.fillRect(x + Grid.cellSize / 2, y + Grid.cellSize / 4, Grid.cellSize / 2, Grid.cellSize / 2)
                        Orientation.SOUTH -> gc.fillRect(x + Grid.cellSize / 4, y + Grid.cellSize / 2, Grid.cellSize / 2, Grid.cellSize / 2)
                        Orientation.WEST -> gc.fillRect(x, y + Grid.cellSize / 4, Grid.cellSize / 2, Grid.cellSize / 2)
                    }
                }
            }
            BackgroundType.WALL_WINDOW -> {
                for (o in orientations) {
                    when (o) {
                        Orientation.NORTH -> {
                            gc.fillRect(x, y + Grid.cellSize / 4, Grid.cellSize, Grid.cellSize / 2)
                            gc.fill = BackgroundType.GAP.fill
                            gc.fillRect(x, y, Grid.cellSize, Grid.cellSize / 4)
                        }
                        Orientation.EAST -> {
                            gc.fillRect(x + Grid.cellSize / 4, y, Grid.cellSize / 2, Grid.cellSize)
                            gc.fill = BackgroundType.GAP.fill
                            gc.fillRect(x, y + Grid.cellSize * 3 / 4, Grid.cellSize / 4, Grid.cellSize)
                        }
                        Orientation.SOUTH -> {
                            gc.fillRect(x, y + Grid.cellSize / 4, Grid.cellSize, Grid.cellSize / 2)
                            gc.fill = BackgroundType.GAP.fill
                            gc.fillRect(x + Grid.cellSize * 3 / 4, y, Grid.cellSize, Grid.cellSize / 4)
                        }
                        Orientation.WEST -> {
                            gc.fillRect(x + Grid.cellSize / 4, y, Grid.cellSize / 2, Grid.cellSize)
                            gc.fill = BackgroundType.GAP.fill
                            gc.fillRect(x, y + Grid.cellSize, Grid.cellSize / 4, Grid.cellSize)
                        }
                    }
                }
            }
            BackgroundType.DOOR -> {
                if (orientations.isEmpty())
                    gc.fillRect(x, y, Grid.cellSize, Grid.cellSize)
                else {
                    val doorwidth = Grid.cellSize / 6
                    for (o in orientations) {
                        when (o) {
                            Orientation.NORTH -> gc.fillRect(x, y, Grid.cellSize, doorwidth)
                            Orientation.EAST -> gc.fillRect(x + Grid.cellSize - doorwidth, y, doorwidth, Grid.cellSize)
                            Orientation.SOUTH -> gc.fillRect(x, y + Grid.cellSize - doorwidth, Grid.cellSize, doorwidth)
                            Orientation.WEST -> gc.fillRect(x, y, doorwidth, Grid.cellSize)
                        }
                    }
                }
            }
            else -> {

            }
        }

    }

}