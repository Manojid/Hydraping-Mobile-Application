package com.hydroping.app.domain

import java.util.Calendar

class CharacterStateMachine {

    fun determineMood(
        lastDrinkTimeMs: Long?,
        currentTotalMl: Int,
        targetDailyMl: Int,
        quietStartHour: Int,
        quietEndHour: Int,
        isDrinkingNow: Boolean = false,
        isRecentlyCelebrated: Boolean = false
    ): CharacterMood {
        if (isRecentlyCelebrated) {
            return CharacterMood.HAPPY_CELEBRATING
        }
        if (isDrinkingNow) {
            return CharacterMood.DRINKING_IN_PROGRESS
        }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isQuietHour = if (quietStartHour > quietEndHour) {
            currentHour >= quietStartHour || currentHour < quietEndHour
        } else {
            currentHour in quietStartHour until quietEndHour
        }

        if (isQuietHour) {
            return CharacterMood.SLEEPING
        }

        if (currentTotalMl >= targetDailyMl && targetDailyMl > 0) {
            return CharacterMood.HYDRATED
        }

        if (lastDrinkTimeMs == null || lastDrinkTimeMs == 0L) {
            return CharacterMood.A_LITTLE_THIRSTY
        }

        val elapsedMinutes = (System.currentTimeMillis() - lastDrinkTimeMs) / (1000 * 60)

        return when {
            elapsedMinutes < 45 -> CharacterMood.HYDRATED
            elapsedMinutes < 75 -> CharacterMood.WAITING
            elapsedMinutes < 120 -> CharacterMood.A_LITTLE_THIRSTY
            elapsedMinutes < 180 -> CharacterMood.THIRSTY
            else -> CharacterMood.VERY_THIRSTY
        }
    }
}
