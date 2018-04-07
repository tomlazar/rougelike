package games.rougelike.objects

import games.support.Grid
import games.support.InputBinding
import games.support.InputManager
import games.support.LevelManager
import games.support.interfaces.IGameObject
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Equipment(gc: GraphicsContext, gridx: Int, gridy: Int, val type: EquipmentType, var pickupEvent: List<Events.GameEvent>? = null) : IGameObject(gc) {
    override var height: Double = Grid.cellSize / 2
    override var width: Double = Grid.cellSize / 2
    override var x: Double = Grid.mapFromGrid(gridx.toDouble()) + Grid.cellSize / 2 - width / 2
    override var y: Double = Grid.mapFromGrid(gridy.toDouble()) + Grid.cellSize / 2 - height / 2

    companion object {
        val acquiredEquipment = hashMapOf(*EquipmentType.values().map { t -> Pair(t, false) }.toTypedArray())

        init {
            //acquiredEquipment[EquipmentType.PUSH] = true
        }
    }

    enum class EquipmentType(val id: Int, val description: String) {
        PUSH(-1, "Push (${InputBinding.PUSH.input.first()})"),
        HACK(0, "Hack (${InputBinding.SET_TARGET.input.first()} + ${InputBinding.HACK.input.first()})"),
        GRENADE(1, "EMP Emitter (${InputBinding.GRENADE.input.first()})");
        companion object { fun fromId(id: Int) = EquipmentType.values().find { t: EquipmentType -> t.id == id } }
    }

    override fun render() {
        gc.fill = Color.GOLD
        gc.fillOval(x, y, width, height)
    }

    override fun update() {
        if (this.collidesWith(LevelManager.current.player)) {
            acquiredEquipment[this.type] = true
            this.dead = true
            pickupEvent?.forEach { e -> e.trigger() }
        }
    }


}