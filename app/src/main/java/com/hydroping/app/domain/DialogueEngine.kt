package com.hydroping.app.domain

object DialogueEngine {

    fun getDialogue(
        userName: String = "Friend",
        characterId: String,
        personality: PersonalityType,
        mood: CharacterMood,
        percentProgress: Int,
        isBehindTarget: Boolean = false,
        deficitMl: Int = 0,
        remainingMl: Int = 0
    ): String {
        return when (characterId.lowercase()) {
            "jerry" -> getJerryDialogue(userName, mood, percentProgress, isBehindTarget, deficitMl, remainingMl)
            "whiskers" -> getWhiskersDialogue(userName, mood, percentProgress, isBehindTarget, deficitMl, remainingMl)
            else -> getPikachuDialogue(userName, mood, percentProgress, isBehindTarget, deficitMl, remainingMl)
        }
    }

    private fun getPikachuDialogue(
        userName: String,
        mood: CharacterMood,
        percentProgress: Int,
        isBehindTarget: Boolean,
        deficitMl: Int,
        remainingMl: Int
    ): String {
        if (isBehindTarget && deficitMl >= 150 && mood != CharacterMood.SLEEPING && mood != CharacterMood.HAPPY_CELEBRATING) {
            val variants = listOf(
                "Energy levels dropping, $userName! Feeling tired and drained... Please drink some water! ⚡💧",
                "Low power warning! We are $deficitMl ml behind target, $userName. Let's take a sip right now! ⚡",
                "I'm feeling so exhausted and thirsty, $userName! A fresh glass of water will recharge us! ⚡💧"
            )
            return variants[(deficitMl / 50) % variants.size]
        }

        return when (mood) {
            CharacterMood.SLEEPING -> "Resting peacefully... Recharging full electric power for tomorrow, $userName! 🌙⚡"
            CharacterMood.HYDRATED -> if (percentProgress >= 100) {
                "Target crushed! 100% hydration power achieved today, $userName! ⚡🏆✨"
            } else {
                "Great progress, $userName! We are $percentProgress% hydrated and feeling energized! ⚡💧"
            }
            CharacterMood.WAITING -> "Looking good, $userName! Only ${remainingMl} ml left to reach our full daily charge! ⚡"
            CharacterMood.A_LITTLE_THIRSTY -> "Energy dipping slightly, $userName. Time for a quick refreshing water break! ⚡🫧"
            CharacterMood.THIRSTY -> "Feeling tired and thirsty, $userName! Grab a fresh glass of water now! 🚰⚡"
            CharacterMood.VERY_THIRSTY -> "Emergency low battery! Need water urgently, $userName! Please drink up! 🆘⚡"
            CharacterMood.DRINKING_IN_PROGRESS -> "Drinking in progress... Recharging battery, $userName! ⏳⚡"
            CharacterMood.HAPPY_CELEBRATING -> "Super refreshing! Great progress on your hydration today, $userName! ⚡🎉"
        }
    }

    private fun getJerryDialogue(
        userName: String,
        mood: CharacterMood,
        percentProgress: Int,
        isBehindTarget: Boolean,
        deficitMl: Int,
        remainingMl: Int
    ): String {
        if (isBehindTarget && deficitMl >= 150 && mood != CharacterMood.SLEEPING && mood != CharacterMood.HAPPY_CELEBRATING) {
            val variants = listOf(
                "Whew, I'm out of breath and exhausted, $userName! Drink some water before Tom catches us! 🐭💧",
                "Feeling so tired and thirsty... We are $deficitMl ml behind target! Take a sip now, $userName! 🧀",
                "Running on empty, $userName! Grab a big glass of water right now to get our energy back! 💧"
            )
            return variants[(deficitMl / 50) % variants.size]
        }

        return when (mood) {
            CharacterMood.SLEEPING -> "Shh... Jerry is snoozing peacefully in the mousehole. Good night, $userName! 🌙🐭"
            CharacterMood.HYDRATED -> if (percentProgress >= 100) {
                "Woohoo! We outsmarted dehydration and conquered today's goal, $userName! 🏆🧀🎉"
            } else {
                "Great progress, $userName! Smooth sips keeping us ahead of the game! 🐭💧"
            }
            CharacterMood.WAITING -> "Looking sharp, $userName! We're $percentProgress% hydrated, only ${remainingMl} ml to go! 🧀✨"
            CharacterMood.A_LITTLE_THIRSTY -> "Throat feeling a bit dry, $userName... Time for some fresh water! 🐭🫧"
            CharacterMood.THIRSTY -> "So thirsty and panting, $userName! Let's drink a fresh cup right away! 🚰🐭"
            CharacterMood.VERY_THIRSTY -> "Completely parched and exhausted! Quick, $userName, log some water now! 🆘💧"
            CharacterMood.DRINKING_IN_PROGRESS -> "Gulp gulp! Delicious water going down, $userName... ⏳🧀"
            CharacterMood.HAPPY_CELEBRATING -> "That was amazing! Great job keeping us well hydrated, $userName! 🎉🐭"
        }
    }

    private fun getWhiskersDialogue(
        userName: String,
        mood: CharacterMood,
        percentProgress: Int,
        isBehindTarget: Boolean,
        deficitMl: Int,
        remainingMl: Int
    ): String {
        if (isBehindTarget && deficitMl >= 150 && mood != CharacterMood.SLEEPING && mood != CharacterMood.HAPPY_CELEBRATING) {
            val variants = listOf(
                "Look at me, I'm completely drained and grumpy. Drink some water already, $userName! 😼💧",
                "We are $deficitMl ml behind schedule, $userName. Stop slacking and hydrate! 🐾",
                "A bit behind target, $userName. Even royal cats need steady hydration breaks. 😼"
            )
            return variants[(deficitMl / 50) % variants.size]
        }

        return when (mood) {
            CharacterMood.SLEEPING -> "Curled up for my beauty sleep. Don't wake me unless you're drinking water. 🌙🐾"
            CharacterMood.HYDRATED -> if (percentProgress >= 100) {
                "Daily goal annihilated! You actually did it, $userName! Champion status! 🏆🕶️"
            } else {
                "Purr-fect progress, $userName. Keeping things sleek and well hydrated! 🐾💧"
            }
            CharacterMood.WAITING -> "Solid work, $userName. $percentProgress% completed without drama. Keep going! 😼"
            CharacterMood.A_LITTLE_THIRSTY -> "Water, $userName. Soon. Don't test my royal patience. 🕶️"
            CharacterMood.THIRSTY -> "Water. Now. Please, $userName. I am not asking twice. 🥤😼"
            CharacterMood.VERY_THIRSTY -> "Hey $userName, did you forget how water works? Drink or I knock the glass over! 🐾💥"
            CharacterMood.DRINKING_IN_PROGRESS -> "Gulping, $userName? Good. Don't spill a drop. ⏳"
            CharacterMood.HAPPY_CELEBRATING -> "Top tier sip, $userName. Keep that winning streak alive! ✨"
        }
    }

    fun getMissedDayRecoveryDialogue(userName: String = "Friend", characterId: String): String {
        val name = if (userName.isNotBlank() && userName != "Friend") userName else "friend"
        return when (characterId.lowercase()) {
            "jerry" -> "Yesterday is in the past, $name! Today we outsmart thirst from the very first sip! 🐭🧀💧"
            "whiskers" -> "Yesterday is history, $name. Today is a fresh bowl of water. Begin. 🐾💧"
            else -> "Yesterday's behind us, $name! Ready to charge up full power today! ⚡💧"
        }
    }

    fun getNotificationPrompt(
        userName: String = "Friend",
        characterId: String,
        amountMl: Int,
        isBehindTarget: Boolean = false,
        remainingMl: Int = 0
    ): String {
        val greeting = if (userName.isNotEmpty() && userName != "Friend") "Hey $userName" else "Hey friend"
        return when (characterId.lowercase()) {
            "jerry" -> if (isBehindTarget) {
                "$greeting, Jerry is tired and needs hydration! Drink $amountMl ml now! 🐭💧"
            } else {
                "$greeting, Jerry says it's time for a refreshing drink ($amountMl ml)! 🧀💧"
            }
            "whiskers" -> if (isBehindTarget) {
                "$greeting! Pacing is behind. Drink $amountMl ml to keep up! 🐾"
            } else {
                "$greeting! Water time. Drink $amountMl ml please. 😼"
            }
            else -> if (isBehindTarget && remainingMl > 0) {
                "$greeting, feeling drained! Let's recharge with $amountMl ml! ⚡💧"
            } else {
                "$greeting, time for fresh water ($amountMl ml)! ⚡💧"
            }
        }
    }
}
