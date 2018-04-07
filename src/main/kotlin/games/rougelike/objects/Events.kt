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
        }),
        PLAN_2_ENTER_SLATTERY_OFFICE(10200, {
            games.support.LevelManager.current.showPrompts(
                    Prompt(DIALOGUE, "Slattery: Dennis, thank God! That bot has had me locked in here for hours."),
                    Prompt(DIALOGUE, "Dennis: No problem, Mike. What on earth is going on?"),
                    Prompt(DIALOGUE, "Slattery: You know almost as much as I do. These haywire robots roaming the halls, " +
                            "all the phones are down, the front doors of Cudahy locked from both sides... It's chaos!"),
                    Prompt(DIALOGUE, "Slattery: I was just making some photocopies in the office when these bots started " +
                            "coming up the main stairway. I've been scanning the network to see if there are any clues, " +
                            "and there seems to be a lot of traffic coming out of the Wired Office workstations. I bet this " +
                            "is one of their awful contraptions gone haywire."),
                    Prompt(DIALOGUE, "Dennis: Yikes. You keep looking around for out-of-place traffic on the network. I'll go " +
                            "check out the wired office."),
                    Prompt(DIALOGUE, "Slattery: Sounds good. Hey, I saw your research students using that system analyzer " +
                            "you gave them to debug XINU earlier today. You should go get that; I bet it will work wonders on" +
                            "these death machines rampaging through Cudahy."),
                    Prompt(DIALOGUE, "Dennis: Good call. See you, Mike.")
            )
        }),
        PLAN_3_ENTER_SYS_LAB(10300, {
            LevelManager.current.showPrompts(
                    Prompt(THINKING, "Huh. Empty. No matter, looks like Tom left the analyzer at Mawdryn."),
                    Prompt(NARRATION, "..."),
                    Prompt(THINKING, "Still, I wonder why nobody is here..."),
                    Prompt(THINKING, "The OS students better not think that death robots are a good reason for a deadline extension...")
            )
        }),
        PLAN_4_ENTER_WIRED_OFFICE(10400, {
            games.support.LevelManager.current.showPrompts(
                    Prompt(NARRATION, "As Dennis enters the Wired Office, a student looks up from working over a pile of scraps " +
                            "which looks like it used to be a junkerbot."),
                    Prompt(DIALOGUE, "Tim: Dr. Brylow! It's good to see you! Are you investigating the robot frenzy?"),
                    Prompt(DIALOGUE, "Dennis: You bet. What have you guys been up to down here?"),
                    Prompt(DIALOGUE, "Tim: IT asked us to look into it, so we started running our new AI systems over the " +
                            "security footage. About ten minutes ago a whole horde of bots came in through our side door and " +
                            "we had to flee the systems room."),
                    Prompt(DIALOGUE, "Tim: The rest of the team has been working on clearing out the bots. They're in " +
                            "the storage room right now but we really need to get to the security footage terminal if we want to " +
                            "know what's going on."),
                    Prompt(DIALOGUE, "Dennis: Not to worry. I'll see if I can get the footage analysis results. How have you " +
                            "guys been dealing with the bots?"),
                    Prompt(DIALOGUE, "Tim: One of the guys rigged up some localized-EMP emitters. There's a stash of them by the " +
                            "security terminal - feel free to grab it when you can get past all those bots.")
            )
        }),
        PLAN_4_SECURITY_TERMINAL(10401, {
            games.support.LevelManager.current.showPrompts(
                    Prompt(THINKING, "Wow, these makeshift grenades look pretty slick, given the circumstances."),
                    Prompt(MESSAGE, "\"EMP Grenade\" ability unlocked: press ${InputBinding.GRENADE.input.first()} to throw."),
                    Prompt(THINKING, "And these warning messages must be the results of the AI protocols."),
                    Prompt(TERMINAL, "ALL SYSTEMS NOMINAL. NO BREACHES DETECTED. WARNING: SOME MISSING FOOTAGE FROM CU001"),
                    Prompt(NARRATION, "Dennis pulls out his phone and dials a call."),
                    Prompt(DIALOGUE, "Dennis: Mike? It's me. The guys in the Wired Office are just as surprised as we are. " +
                            "Their security footage system found some suspicious activity in the lower level lecture pit. I'm " +
                            "going to check it out.")
            )
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