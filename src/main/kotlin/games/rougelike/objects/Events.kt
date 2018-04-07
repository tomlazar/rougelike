package games.rougelike.objects

import games.support.LevelManager

class Events {

    companion object {
        val triggeredEvents = hashMapOf(*GameEvent.values().map { t -> Pair(t, false) }.toTypedArray())
    }

    enum class GameEvent(val id: Int, val action: () -> Unit) {
        TEST_PROMPT(-1, {
            LevelManager.current.showPrompts("This is a test prompt.")
        }),
        ;

        fun trigger() {
            if (!triggeredEvents[this]!!) {
                this.action()
                Events.triggeredEvents[this] = true
            }
        }

        companion object {
            fun fromId(id: Int) = GameEvent.values().find { t: GameEvent -> t.id == id }
        }
    }
}