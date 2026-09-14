package com.hydroping.app.domain

data class MilestoneBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val requiredDays: Int,
    val isUnlocked: Boolean
)

object MilestoneService {

    fun getMilestones(currentStreakDays: Int): List<MilestoneBadge> {
        return listOf(
            MilestoneBadge(
                id = "flame_3",
                title = "3-Day Spark",
                description = "Completed 3 consecutive hydration days.",
                iconEmoji = "🔥",
                requiredDays = 3,
                isUnlocked = currentStreakDays >= 3
            ),
            MilestoneBadge(
                id = "silver_7",
                title = "7-Day Silver Wave",
                description = "One full week of healthy water intake.",
                iconEmoji = "🥈",
                requiredDays = 7,
                isUnlocked = currentStreakDays >= 7
            ),
            MilestoneBadge(
                id = "gold_14",
                title = "14-Day Gold Medal",
                description = "Two weeks of dedicated companion care.",
                iconEmoji = "🥇",
                requiredDays = 14,
                isUnlocked = currentStreakDays >= 14
            ),
            MilestoneBadge(
                id = "trophy_30",
                title = "30-Day Hydro Master",
                description = "A whole month of perfect hydration habit.",
                iconEmoji = "🏆",
                requiredDays = 30,
                isUnlocked = currentStreakDays >= 30
            )
        )
    }
}
