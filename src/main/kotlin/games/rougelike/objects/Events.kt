package games.rougelike.objects

import games.support.*
import games.support.interfaces.ILevel.*
import games.support.interfaces.ILevel.PromptType.*

class Events {

    companion object {
        val triggeredEvents = hashMapOf(*GameEvent.values().map { t -> Pair(t, false) }.toTypedArray())
    }

    enum class GameEvent(val id: Int, val action: () -> Unit) {
        TEST_PROMPT(-1, {
            LevelManager.current.showPrompts(Prompt(MESSAGE, "This is a test prompt."))
        }),
        PLAN_1B_SEE_TARGETING_BOT(10120, {
            LevelManager.current.showPrompts(
                    Prompt(NARRATION, "Dennis stops in his tracks when he reaches the back hallway. A junkerbot is " +
                            "continuously slamming itself against Mike's door."),
                    Prompt(THINKING, "That must be the bot that Mike was talking about."),
                    Prompt(THINKING, "Hmm... It doesn't look as mindless as the others. I think I'll have to disable it somehow."),
                    Prompt(NARRATION, "An idea hit Dennis, his face widening in a smile."),
                    Prompt(THINKING, "I'll give this piece of junk what for! I just need to find a long drop..."),
                    Prompt(MESSAGE, "\"Push Junker\" ability unlocked: press ${InputBinding.PUSH.input.first()} when " +
                            "near a junker that is near a ledge."),
                    callback = {
                        Equipment.acquiredEquipment[Equipment.EquipmentType.PUSH] = true
                    }
            )
        })
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