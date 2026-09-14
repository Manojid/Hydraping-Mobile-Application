package com.hydroping.app.domain

import java.util.Calendar
import kotlin.math.max
import kotlin.math.min

data class ReminderPacingInfo(
    val calculatedDelayMinutes: Int,
    val isBehindSchedule: Boolean,
    val isAheadOfSchedule: Boolean,
    val isGoalCompleted: Boolean = false,
    val deficitMl: Int,
    val remainingMl: Int,
    val expectedMlAtThisHour: Int,
    val suggestedDrinkAmountMl: Int = 250,
    val pacingStatusText: String
)

class AdaptiveReminderEngine {

    companion object {
        const val MIN_INTERVAL_MINS = 20
        const val MAX_INTERVAL_MINS = 180
    }

    /**
     * Dynamically calculates the optimal time and gradual volume for the next reminder based on:
     * - Total daily water target
     * - Consumed water so far
     * - Base user-preferred reminder interval
     * - Time of day & remaining active day hours (wake up to sleep)
     * - Mode settings (Study Mode, Work Mode)
     * - Gradual distribution of remaining target across awake hours
     */
    fun calculateNextReminder(
        dailyTargetMl: Int,
        currentConsumedMl: Int,
        baseIntervalMins: Int,
        activeStartHour: Int = 7,  // e.g. 7 AM
        activeEndHour: Int = 23,   // e.g. 11 PM
        smartPacingEnabled: Boolean = true,
        consecutiveDismissals: Int = 0,
        userFavoriteAmount: Int = 250,
        scheduleProfile: ScheduleProfile? = null,
        now: Calendar = Calendar.getInstance()
    ): ReminderPacingInfo {
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentDayFraction = (currentHour + currentMinute / 60f)

        // Determine active hours and effective base interval considering schedule & modes
        var effectiveBaseInterval = baseIntervalMins

        if (scheduleProfile != null) {
            if (scheduleProfile.studyModeEnabled) {
                // Respect user-configured Study Mode interval
                effectiveBaseInterval = scheduleProfile.studyIntervalMins.coerceIn(20, 180)
            } else if (scheduleProfile.workModeEnabled) {
                // Work Mode: Gentle, less intrusive interval
                effectiveBaseInterval = (effectiveBaseInterval * 1.25f).toInt().coerceIn(MIN_INTERVAL_MINS, MAX_INTERVAL_MINS)
            }
        }

        val totalActiveHours = if (activeEndHour > activeStartHour) {
            (activeEndHour - activeStartHour).toFloat()
        } else {
            (24 - activeStartHour + activeEndHour).toFloat()
        }.coerceAtLeast(4f)

        val elapsedActiveHours = when {
            activeEndHour > activeStartHour -> {
                (currentDayFraction - activeStartHour).coerceIn(0f, totalActiveHours)
            }
            else -> {
                if (currentDayFraction >= activeStartHour) {
                    (currentDayFraction - activeStartHour)
                } else {
                    (24 - activeStartHour + currentDayFraction)
                }.coerceIn(0f, totalActiveHours)
            }
        }

        val remainingActiveHours = max(0.5f, totalActiveHours - elapsedActiveHours)
        val remainingMl = max(0, dailyTargetMl - currentConsumedMl)

        // Expected progress at current time
        val expectedProgressRatio = (elapsedActiveHours / totalActiveHours).coerceIn(0f, 1f)
        val expectedMl = (dailyTargetMl * expectedProgressRatio).toInt()
        val deficitMl = expectedMl - currentConsumedMl // Positive = behind, Negative = ahead

        // 1. GOAL COMPLETION INVARIANT:
        // When consumed >= target, stop daytime reminders for the rest of the day!
        if (remainingMl <= 0) {
            return ReminderPacingInfo(
                calculatedDelayMinutes = -1,
                isBehindSchedule = false,
                isAheadOfSchedule = true,
                isGoalCompleted = true,
                deficitMl = 0,
                remainingMl = 0,
                expectedMlAtThisHour = expectedMl,
                suggestedDrinkAmountMl = 0,
                pacingStatusText = "Goal completed! Daytime reminders finished. Great job! 🎉"
            )
        }

        // Calculate gradual distribution without arbitrary universal caps
        val estimatedRemainingReminders = max(1f, remainingActiveHours / (effectiveBaseInterval / 60f))
        val gradualPortion = (remainingMl / estimatedRemainingReminders).toInt()
        // Respect user's preferred amount as anchor, gently scaling if deficit is significant
        val suggestedAmount = when {
            deficitMl >= 300 -> {
                max(userFavoriteAmount, min(gradualPortion, userFavoriteAmount + 100))
            }
            deficitMl <= -200 -> {
                min(userFavoriteAmount, max(150, gradualPortion))
            }
            else -> {
                userFavoriteAmount
            }
        }

        if (!smartPacingEnabled) {
            // Fixed interval mode + backoff if dismissed
            val backoffMins = effectiveBaseInterval + (consecutiveDismissals * 15)
            val finalMins = min(MAX_INTERVAL_MINS, backoffMins)
            return ReminderPacingInfo(
                calculatedDelayMinutes = finalMins,
                isBehindSchedule = deficitMl > 250,
                isAheadOfSchedule = deficitMl < -200,
                isGoalCompleted = false,
                deficitMl = deficitMl,
                remainingMl = remainingMl,
                expectedMlAtThisHour = expectedMl,
                suggestedDrinkAmountMl = suggestedAmount,
                pacingStatusText = "Fixed reminder scheduled every $finalMins mins."
            )
        }

        // Dynamic Smart Pacing logic
        var calculatedMinutes: Int

        val isSignificantlyBehind = deficitMl >= 250
        val isAhead = deficitMl <= -200

        when {
            // 1. Behind target: Accelerate reminders with gradual catch-up distribution
            isSignificantlyBehind -> {
                val urgencyFactor = (deficitMl.toFloat() / 500f).coerceIn(1.0f, 1.6f)
                val shortened = (effectiveBaseInterval / urgencyFactor).toInt()
                calculatedMinutes = max(MIN_INTERVAL_MINS, shortened)
            }

            // 2. Ahead of target: Relax interval so companion doesn't interrupt unnecessarily
            isAhead -> {
                val relaxed = (effectiveBaseInterval * 1.35f).toInt()
                calculatedMinutes = min(MAX_INTERVAL_MINS, relaxed)
            }

            // 3. On track: Pace evenly across remaining active hours
            else -> {
                calculatedMinutes = effectiveBaseInterval
            }
        }

        // Apply gentle backoff if user repeatedly dismissed without drinking
        if (consecutiveDismissals > 0) {
            calculatedMinutes = min(MAX_INTERVAL_MINS, calculatedMinutes + (consecutiveDismissals * 15))
        }

        val pacingText = when {
            scheduleProfile?.studyModeEnabled == true ->
                "📚 Study Focus Active: Reminders aligned to ${scheduleProfile.studyIntervalMins}m focus blocks."
            scheduleProfile?.workModeEnabled == true ->
                "💼 Work Mode Active: Gentle low-interruption reminders."
            isSignificantlyBehind ->
                "⚡ Catch-up mode active: $deficitMl ml behind pace. Reminding sooner ($calculatedMinutes min)."
            isAhead ->
                "✨ Great pace! You are ahead of schedule. Relaxed interval ($calculatedMinutes min)."
            else ->
                "💧 Perfect pace: On track to reach $dailyTargetMl ml today."
        }

        return ReminderPacingInfo(
            calculatedDelayMinutes = calculatedMinutes,
            isBehindSchedule = isSignificantlyBehind,
            isAheadOfSchedule = isAhead,
            isGoalCompleted = false,
            deficitMl = deficitMl,
            remainingMl = remainingMl,
            expectedMlAtThisHour = expectedMl,
            suggestedDrinkAmountMl = suggestedAmount,
            pacingStatusText = pacingText
        )
    }
}
