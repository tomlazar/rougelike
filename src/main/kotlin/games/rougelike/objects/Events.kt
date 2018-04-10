package games.rougelike.objects

import games.support.*
import games.support.DialogBuilder.PromptType.*

class Events {

    companion object {
        val triggeredEvents = hashMapOf(*GameEvent.values().map { t -> Pair(t, false) }.toTypedArray())
    }

    enum class GameEvent(val id: Int, val action: () -> Any) {
        TEST_PROMPT(-1, {
            LevelManager.current.showPrompts({ DialogBuilder.Prompt(MESSAGE, "This is a test prompt.") })
        }),
        PLAN_1_INTRO(10100, {
            LevelManager.current.showPrompts(
                    {
                        DialogBuilder.Prompt(NARRATION, "On a normal April day, Dennis Brylow finds himself in his office waiting for his bus-time.")
                    },
                    { DialogBuilder.Prompt(THINKING, "I suppose I can check some emails while I wait.") },
                    { DialogBuilder.Prompt(TERMINAL, "$ alpine --check-mail") },
                    { DialogBuilder.Prompt(TERMINAL, "> One new message from Slattery, Michael.") },
                    {
                        DialogBuilder.Prompt(TERMINAL, "> Dennis, \n" +
                                "> Are you still at Cudahy? If so, come quick! There are robot killing-machines rampaging through the halls! \n" +
                                "> I have one outside my door, I need you to lure it away or something! \n" +
                                "> \n" +
                                ">  - mikes")
                    },
                    { DialogBuilder.Prompt(THINKING, "That sounds terrible! I better get to his office, quick!") },
                    callback = {
                        HUD.objective = "Get to Dr. Slattery's office"
                        {}
                    }
            )
        }),
        PLAN_1B_RAILROAD(10121,
                {
                    if (!Events.triggeredEvents[PLAN_2_ENTER_SLATTERY_OFFICE]!!) {
                        LevelManager.current.showPrompts({
                            DialogBuilder.Prompt(THINKING, "I can't get distracted - Mike said he really needs my help in his office on the 3rd floor.")
                        })
                        false
                    } else
                        true
                }),
        PLAN_1B_SEE_TARGETING_BOT(10120,
                {
                    LevelManager.current.showPrompts(
                            {
                                DialogBuilder.Prompt(NARRATION, "Dennis stops in his tracks when he reaches the back hallway. A junkerbot is " +
                                        "continuously slamming itself against Mike's door.")
                            },
                            { DialogBuilder.Prompt(THINKING, "That must be the bot that Mike was talking about.") },
                            { DialogBuilder.Prompt(THINKING, "Hmm... This one is really fixated on Mike's office. I think I'll have to disable it somehow.") },
                            { DialogBuilder.Prompt(NARRATION, "An idea hit Dennis, his face widening in a smile.") },
                            { DialogBuilder.Prompt(THINKING, "I'll give this piece of junk what for! I just need to find a long drop...") },
                            {
                                Equipment.acquiredEquipment[Equipment.EquipmentType.PUSH] = true
                                DialogBuilder.Prompt(MESSAGE, "\"Push Junker\" ability unlocked: lure a robot directly adjacent to a ledge and " +
                                        "press ${InputBinding.PUSH.input.first()} when the icon appears.")
                            }
                    )
                }),
        PLAN_2_ENTER_SLATTERY_OFFICE(10200,
                {
                    games.support.LevelManager.current.showPrompts(
                            {
                                HUD.objective = ""
                                DialogBuilder.DialogPrompt("Dr. Slattery", " Dennis, thank God! That bot has had me locked in here for hours.")
                            },
                            { DialogBuilder.DialogPrompt("Dennis", " No problem, Mike. What on earth is going on?") },
                            {
                                DialogBuilder.DialogPrompt("Dr. Slattery", "You know almost as much as I do. These haywire robots roaming the halls, " +
                                        "all the phones are down, the front doors of Cudahy locked from both sides... It's chaos!")
                            },
                            {
                                DialogBuilder.DialogPrompt("Dr. Slattery", "I was just making some photocopies in the office when these bots started " +
                                        "coming up the main stairway. I've been scanning the network to see if there are any clues, " +
                                        "and there seems to be a lot of traffic coming out of the Wired Office workstations. I bet this " +
                                        "is one of their awful contraptions gone haywire.")
                            },
                            {
                                DialogBuilder.DialogPrompt("Dennis", "Yikes. You keep looking around for out-of-place traffic on the network. I'll go " +
                                        "check out the wired office.")
                            },
                            {
                                HUD.objective = "Investigate 2nd floor Wired Office"
                                DialogBuilder.DialogPrompt("Dr. Slattery", "Sounds good. Hey, I saw your research students using that system analyzer " +
                                        "you gave them to debug XINU earlier today. You should go get that; I bet it will work wonders on " +
                                        "these death machines rampaging through Cudahy.")
                            },
                            { DialogBuilder.DialogPrompt("Dennis", " Good call. See you, Mike.") }
                    )
                }),
        PLAN_3_ENTER_SYS_LAB(10300,
                {
                    LevelManager.current.showPrompts(
                            { DialogBuilder.DialogPrompt("Avery", " Hi, Dr. Brylow!") },
                            { DialogBuilder.DialogPrompt("Dennis", " Hello Avery. How's the OS assignment going? Must be pretty chaotic with these robots running around.") },
                            { DialogBuilder.DialogPrompt("Avery", " You know TA-Bot isn't working? Deathrobots or no deathrobots, it should still run. Can we have an extension? ") },
                            { DialogBuilder.DialogPrompt("Dennis", " *deep sigh*") },
                            { DialogBuilder.DialogPrompt("Dennis", " Yes, I suppose so. I'll make a note to have it run twice tomorrow. Let me just grab the system analyzer and I'll be on my way. Good Luck!") }
                    )
                }),
        PLAN_3_PICKUP(10301,
                {
                    LevelManager.current.showPrompts(
                            {
                                Equipment.acquiredEquipment[Equipment.EquipmentType.HACK] = true
                                DialogBuilder.Prompt(MESSAGE, "\"Hack\" ability unlocked: use ${InputBinding.SET_TARGET.input.first()} to target a junker, and hold " +
                                        "${InputBinding.HACK.input.first()} to hack. Limited range.")
                            }
                    )
                }),
        PLAN_4_ENTER_WIRED_OFFICE(10400,
                {
                    games.support.LevelManager.current.showPrompts(
                            {
                                DialogBuilder.Prompt(NARRATION, "As Dennis enters the Wired Office, a former student looks up from working over a pile of scraps " +
                                        "which looks like it used to be a junkerbot.")
                            },
                            { DialogBuilder.DialogPrompt("Julia", " Dr. Brylow! It's good to see you! Are you investigating the robot frenzy?") },
                            { DialogBuilder.DialogPrompt("Dennis", " You bet. What have you guys been up to down here?") },
                            {
                                DialogBuilder.DialogPrompt("Julia", "IT asked us to look into it, so we started running our new AI systems over the " +
                                        "security footage. About ten minutes ago a whole horde of bots came in through our side door and " +
                                        "we had to flee from the systems room.")
                            },
                            {
                                HUD.objective = ""
                                DialogBuilder.DialogPrompt("Julia", "The rest of the team has been working on clearing out the bots. They're in " +
                                        "the storage room right now but we really need to get to the security footage terminal if we want to " +
                                        "know what's going on.")
                            },
                            {
                                DialogBuilder.DialogPrompt("Dennis", "Not to worry. I'll see if I can get the footage analysis results. How have you " +
                                        "guys been dealing with the bots?")
                            },
                            {
                                HUD.objective = "Get the security data in the Wired Office back room"
                                DialogBuilder.DialogPrompt("Julia", "One of the guys rigged up some localized-EMP emitters. There's a stash of them by the " +
                                        "security terminal - feel free to grab it when you get past all those bots.")
                            }
                    )
                }),
        PLAN_4_RAILROAD(10402,
                {
                    if (!Events.triggeredEvents[PLAN_4_SECURITY_TERMINAL]!!) {
                        LevelManager.current.showPrompts({
                            DialogBuilder.Prompt(THINKING, "I can't get distracted - I need to find out what I can from the Wired Office on the 2nd floor.")
                        })
                        false
                    } else
                        true
                }),
        PLAN_4_SECURITY_TERMINAL(10401,
                {
                    games.support.LevelManager.current.showPrompts(
                            { DialogBuilder.Prompt(THINKING, "Wow, these makeshift grenades look pretty slick, given the circumstances.") },
                            {
                                Equipment.acquiredEquipment[Equipment.EquipmentType.GRENADE] = true
                                DialogBuilder.Prompt(MESSAGE, "\"EMP Grenade\" ability unlocked: press ${InputBinding.GRENADE.input.first()} to throw.")
                            },
                            { DialogBuilder.Prompt(THINKING, "And these warning messages must be the results of the AI protocols.") },
                            {
                                HUD.objective = ""
                                DialogBuilder.Prompt(TERMINAL, "ALL SYSTEMS NOMINAL. NO BREACHES DETECTED. WARNING: SOME MISSING FOOTAGE FROM CU001")
                            },
                            { DialogBuilder.Prompt(NARRATION, "Dennis pulls out his phone and dials a call.") },
                            {
                                DialogBuilder.DialogPrompt("Dennis", "Mike? It's me. The guys in the Wired Office are just as surprised as we are. " +
                                        "Their security footage system found some suspicious activity in the lower level lecture pit. I'm " +
                                        "going to check it out.")
                            },
                            callback = {
                                HUD.objective = "Investigate suspicious activity in the basement lecture pit"
                                {}
                            }
                    )
                }),
        PLAN_5_STAIRS_BLOCKED(10500,
                {
                    games.support.LevelManager.current.showPrompts(
                            { DialogBuilder.Prompt(THINKING, "Dang, there's no way I'm getting past this pileup of junkers.") },
                            {
                                HUD.objective = ""
                                DialogBuilder.DialogPrompt("Dennis", " Mike? It's me. I need another way down to the lower level. Any ideas?")
                            },
                            { DialogBuilder.DialogPrompt("Dr. Slattery", " You could try the elevator.") },
                            {
                                DialogBuilder.DialogPrompt("Dennis", "I actually called the elevator earlier. The doors wouldn't open all of the way. " +
                                        "Whoever's behind this really wants us to use the stairs for some reason.")
                            },
                            {
                                DialogBuilder.DialogPrompt("Dr. Slattery", "Oh? Well I have a way you could try to get it open. Steve found some old computers " +
                                        "sitting around to put into the 101 lab, but they were rusted shut somehow. Liam was heading down to the lab " +
                                        "with a crowbar after Games class.")
                            },
                            {
                                HUD.objective = "Borrow a crowbar from the 101 lab"
                                DialogBuilder.DialogPrompt("Dennis", " Sounds good. I'll go see if I can borrow it.")
                            }
                    )
                }),
        PLAN_6_ENTER_101(10600,
                {
                    games.support.LevelManager.current.showPrompts(
                            { DialogBuilder.DialogPrompt("Sam", " Hey, Dr. Brylow.") },
                            { DialogBuilder.DialogPrompt("Dennis", " Hi, Sam.") }
                    )
                }),
        PLAN_6_CROWBAR(10601,
                {
                    games.support.LevelManager.current.showPrompts(
                            { DialogBuilder.DialogPrompt("Liam", " Dr. B! What's poppin'?") },
                            { DialogBuilder.DialogPrompt("Dennis", " Good... Were these robots not bothering you?") },
                            {
                                DialogBuilder.DialogPrompt("Liam", "Nope! I saw them chasing Dr. Factor earlier today, but they've been ignoring me and the " +
                                        "other students. I've been working on these machines and they were just chilling out over there.")
                            },
                            { DialogBuilder.DialogPrompt("Dennis", " Huh.") },
                            { DialogBuilder.DialogPrompt("Dennis", " Well, are you done with that crowbar? I wanted to see if I could get the elevator open.") },
                            {
                                DialogBuilder.DialogPrompt("Liam", "Yeah, it's all yours. These machines honestly feel like they're welded shut; I don't " +
                                        "think anyone's gonna be able to get them open.")
                            },
                            {
                                HUD.objective = "Get to the basement lecture pit via the elevator"
                                Equipment.acquiredEquipment[Equipment.EquipmentType.CROWBAR] = true
                                DialogBuilder.Prompt(MESSAGE, "Picked up \"Crowbar\".")
                            }
                    )
                }),
        PLAN_7_ENTER_PIT(10700,
                {
                    LevelManager.current.showPrompts(
                            {
                                HUD.objective = ""
                                DialogBuilder.Prompt(NARRATION, "As Dennis enters the lecture room, the door gives an audible \"click!\" as it closes behind. " +
                                        "Dennis shakes the door, but it's futile. It's a trap!")
                            },
                            {
                                DialogBuilder.Prompt(NARRATION, "Looking into the room, Dennis sees an army of robots ambling around the rows of seats, " +
                                        "and someone moving amongst a pile of unfinished robots and construction equipment at the front of the room.")
                            },
                            { DialogBuilder.Prompt(THINKING, "Looks like I'm gonna have to fight my way out of this one.") },
                            callback = {
                                HUD.objective = "Confront the evil mastermind!"
                                {}
                            }
                    )
                }),
        PLAN_7_CONFRONTATION(10701,
                {
                    LevelManager.current.showPrompts(
                            {
                                HUD.objective = ""
                                DialogBuilder.Prompt(NARRATION, "The man climbs out of the scrap pile, Dennis now recognizing him as none other than Fr. Thomas Schwarz.")
                            },
                            { DialogBuilder.DialogPrompt("Dennis", " Schwarz! You're behind all of this?") },
                            { DialogBuilder.DialogPrompt("Fr. Schwarz", " Dennis. It's not what it looks like.") },
                            {
                                DialogBuilder.DialogPrompt("Fr. Schwarz", "See, I ran your TA-bot software you set up for my Computer Security class, but as " +
                                        "soon as I ran it, the program recognized I'm not you and went haywire! It ordered all this construction equipment " +
                                        "to be delivered and started to take over Cudahy!")
                            },
                            {
                                DialogBuilder.DialogPrompt("Fr. Schwarz", "I just finished disassembling one of " +
                                        "the bots and figured out how to cut off this terminal it's been running from.")
                            },
                            { DialogBuilder.Prompt(NARRATION, "Dennis shakes his head.") },
                            {
                                DialogBuilder.DialogPrompt("Dennis", "Well, at least you've gotten it to stop making new robots. Let's go clean up the rest " +
                                        "of this mess.")
                            }
                    )
                }),
        DOOR_LOCKED(201,
                {
                    LevelManager.current.showPrompts(
                            { DialogBuilder.Prompt(NARRATION, "The door is locked tight.") }
                    )
                }),
        ELEVATOR_JAMMED(202,
                {
                    games.support.LevelManager.current.showPrompts(
                            {
                                if (Equipment.acquiredEquipment[Equipment.EquipmentType.CROWBAR]!!)
                                    DialogBuilder.Prompt(NARRATION, "Prying open the doors with the crowbar, Dennis makes it into the elevator.")
                                else
                                    DialogBuilder.Prompt(NARRATION, "The elevator arrives, but the doors jam with a loud \"BANG!\" after opening only about two inches.")
                            }
                    )
                    Equipment.acquiredEquipment[Equipment.EquipmentType.CROWBAR]!!
                }),
        IT_JAB(203,
                {
                    LevelManager.current.showPrompts(
                            { DialogBuilder.Prompt(THINKING, "Oh my! That's the IT conference room. Have all of the IT staff been turned into robots?") },
                            { DialogBuilder.Prompt(THINKING, "...") },
                            { DialogBuilder.Prompt(THINKING, "I wonder if that means I'll actually get responses to my emails now.") }
                    )
                }),
        MSCS_CLOSED(204,
                {
                    LevelManager.current.showPrompts(
                            {
                                if (!Events.triggeredEvents[GameEvent.PLAN_2_ENTER_SLATTERY_OFFICE]!!)
                                    DialogBuilder.Prompt(NARRATION, "The office is closed up for the evening, but Dennis sees Fr. Schwarz's office has a light on.")
                                else
                                    DialogBuilder.Prompt(NARRATION, "The office is closed up for the evening. No lights are on inside.")
                            }
                    )
                }),
        GOD_MODE(300,
                {
                    LevelManager.current.showPrompts(
                            { DialogBuilder.Prompt(MESSAGE, "You would normally die here, but since you're in God mode, we'll reset your damage.") },
                            {
                                HUD.corruption = 0.0
                                DialogBuilder.Prompt(MESSAGE, "You have ${HUD.deaths} death${if (HUD.deaths == 1) "" else "s"}.")
                            }
                    )
                    Events.triggeredEvents[GOD_MODE] = false
                    {}
                }
        ),
        DIALOGUE_JOSIE(400, {
            LevelManager.current.showPrompts(
                    { DialogBuilder.DialogPrompt("Josie", "Dr. Brylow! What are you doing?!") },
                    { DialogBuilder.DialogPrompt("Dennis", "...") },
                    { DialogBuilder.DialogPrompt("Dennis", "I mean... I was going to use the elevator.") },
                    {
                        DialogBuilder.DialogPrompt("Josie", "I barricaded it on purpose! There are crazy machines everywhere! This is my " +
                                "hiding spot.")
                    },
                    {
                        DialogBuilder.DialogPrompt("Dennis", "Well, don't worry. I had to use this crowbar to get it open; I'm sure " +
                                "it'll jam again on my way out.")
                    },
                    { DialogBuilder.DialogPrompt("Josie", "If you say so...") }
            )
        }),
        DIALOGUE_ZAN(401, {
            LevelManager.current.showPrompts(
                    { DialogBuilder.DialogPrompt("Zan", "Hey, Dennis.") },
                    { DialogBuilder.DialogPrompt("Zan", "Want some pretzels?") },
                    { DialogBuilder.DialogPrompt("Dennis", "Uh... I think I'm alright. A bit busy at the moment.") },
                    { DialogBuilder.DialogPrompt("Rene", "Are you sure? The vending machine just gave us five extra.") },
                    { DialogBuilder.DialogPrompt("Dennis", "Nah. I'll see you guys later.") },
                    { DialogBuilder.DialogPrompt("Zan", "Well, more for me.") },
                    { DialogBuilder.DialogPrompt("Rene", "Hey! You're gonna share, right?") },
                    { DialogBuilder.DialogPrompt("Zan", "Nah...") },
                    { DialogBuilder.DialogPrompt("Rene", "Aww, screw you...") }
            )
        })
        ;

        fun trigger(): Boolean {
            if (!triggeredEvents[this]!!) {
                Events.triggeredEvents[this] = true
                val out = this.action()
                if (out is Boolean && out)
                    return true
            }
            return false
        }

        fun reset() {
            triggeredEvents[this] = false
        }

        companion object {
            fun fromId(id: Int) = GameEvent.values().find { t: GameEvent -> t.id == id }
        }
    }
}