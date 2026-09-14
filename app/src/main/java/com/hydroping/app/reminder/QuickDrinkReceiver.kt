package com.hydroping.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import com.hydroping.app.data.DrinkLog
import com.hydroping.app.data.HydrationDatabase
import com.hydroping.app.data.UserPreferencesRepository
import com.hydroping.app.domain.AdaptiveReminderEngine
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.DialogueEngine
import com.hydroping.app.widget.HydroCompanionWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class QuickDrinkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = HydrationDatabase.getDatabase(context)
                val prefs = UserPreferencesRepository(context)
                val intentCharId = intent.getStringExtra(NotificationHelper.EXTRA_CHARACTER_ID)
                val characterId = if (!intentCharId.isNullOrBlank()) intentCharId else prefs.characterIdFlow.first()
                val character = CharacterCatalog.fromId(characterId)

                if (!intentCharId.isNullOrBlank() && intentCharId != prefs.characterIdFlow.first()) {
                    prefs.setCharacterAndPersonality(character.id, character.defaultPersonality.name)
                }

                val defaultAmount = prefs.favoriteAmountFlow.first()
                val amountMl = intent.getIntExtra(NotificationHelper.EXTRA_AMOUNT_ML, defaultAmount)

                val schedule = prefs.scheduleProfileFlow.first()
                val calNow = Calendar.getInstance()
                val dayOfWeek = calNow.get(Calendar.DAY_OF_WEEK)
                val isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
                val wakeHour = if (isWeekend) schedule.weekendStartHour else schedule.weekdayStartHour
                val sleepHour = if (isWeekend) schedule.weekendEndHour else schedule.weekdayEndHour
                val baseInterval = if (isWeekend) schedule.weekendIntervalMins else schedule.weekdayIntervalMins

                when (action) {
                    NotificationHelper.ACTION_QUICK_DRINK, NotificationHelper.ACTION_CONFIRM_DONE -> {
                        // 1. Atomically record the water intake in Room
                        db.drinkDao().insertDrink(
                            DrinkLog(
                                amountMl = amountMl,
                                source = "NOTIFICATION"
                            )
                        )

                        // 2. Re-read the current authoritative hydration state from Room
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val todayStartMs = cal.timeInMillis
                        val todayEndMs = todayStartMs + 86400000
                        val todayTotal = db.drinkDao().getTodayTotalMl(todayStartMs, todayEndMs)
                        val targetGoal = prefs.dailyGoalFlow.first()
                        val smartPacing = prefs.smartPacingEnabledFlow.first()
                        val userName = prefs.userNameFlow.first()

                        // Sync DailyGoal in Room
                        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                        db.drinkDao().insertOrUpdateDailyGoal(
                            com.hydroping.app.data.DailyGoal(
                                dateString = todayStr,
                                targetMl = targetGoal,
                                totalConsumedMl = todayTotal,
                                streakCount = 0
                            )
                        )

                        // Reset backoff counter
                        prefs.resetDismissalBackoff()

                        // 3. Recalculate adaptive pacing
                        val pacing = AdaptiveReminderEngine().calculateNextReminder(
                            dailyTargetMl = targetGoal,
                            currentConsumedMl = todayTotal,
                            baseIntervalMins = baseInterval,
                            activeStartHour = wakeHour,
                            activeEndHour = sleepHour,
                            smartPacingEnabled = smartPacing,
                            consecutiveDismissals = 0,
                            userFavoriteAmount = defaultAmount,
                            scheduleProfile = schedule
                        )

                        // 4. Cancel/invalidate the obsolete reminder notification and alarm
                        NotificationHelper.cancelReminderNotification(context)
                        val scheduler = ReminderScheduler(context)
                        scheduler.cancelReminder()

                        // 5 & 6. Check if daily goal complete
                        val isGoalCompleted = (todayTotal >= targetGoal && targetGoal > 0) || pacing.isGoalCompleted
                        if (isGoalCompleted) {
                            // Goal complete:
                            // Remove the active reminder (already cancelled above)
                            // Do not schedule another daytime reminder. Schedule tomorrow morning wake-up:
                            scheduler.scheduleWakeUpReminder(wakeHour, 0)
                        } else {
                            // Goal NOT complete:
                            // Generate new notification from NEW Room state:
                            val prompt = DialogueEngine.getNotificationPrompt(
                                userName = userName,
                                characterId = character.id,
                                amountMl = pacing.suggestedDrinkAmountMl,
                                isBehindTarget = pacing.isBehindSchedule,
                                remainingMl = pacing.remainingMl
                            )
                            NotificationHelper.showHydrationReminder(
                                context = context,
                                characterName = character.name,
                                message = prompt,
                                amountMl = pacing.suggestedDrinkAmountMl,
                                userName = userName,
                                consumedMl = todayTotal,
                                targetMl = targetGoal,
                                characterId = character.id
                            )
                            // Schedule exactly ONE valid next reminder:
                            scheduler.scheduleNextReminder(pacing.calculatedDelayMinutes)
                        }

                        // 7. Show success confirmation notification using the updated state on NOTIFICATION_ID_CONFIRMATION
                        NotificationHelper.showCelebrationNotification(
                            context = context,
                            characterName = character.name,
                            amountMl = amountMl,
                            consumedMl = todayTotal,
                            targetMl = targetGoal,
                            characterId = character.id
                        )

                        // 8. Update Glance widgets
                        try {
                            HydroCompanionWidget().updateAll(context)
                        } catch (_: Exception) {}
                    }

                    NotificationHelper.ACTION_DRINKING_NOW -> {
                        // Switch to waiting state
                        NotificationHelper.showDrinkingInProgressNotification(context, character.name, amountMl)
                    }

                    NotificationHelper.ACTION_REMIND_LATER -> {
                        // 1. Immediately dismiss all reminder notifications
                        NotificationHelper.cancelReminderNotification(context)
                        NotificationHelper.cancelConfirmationNotification(context)

                        // 2. Invalidate obsolete scheduled alarms
                        val scheduler = ReminderScheduler(context)
                        scheduler.cancelReminder()

                        // 3. Record dismissal backoff
                        prefs.recordDismissalAndIncrementBackoff()

                        // 4. Calculate next reminder using configured snooze duration
                        val snoozeMins = prefs.snoozeMinsFlow.first()

                        // 5. Schedule exactly ONE snoozed reminder
                        scheduler.scheduleNextReminder(snoozeMins)
                    }

                    NotificationHelper.ACTION_SKIP_REMINDER -> {
                        // 1. Immediately dismiss all reminder notifications
                        NotificationHelper.cancelReminderNotification(context)
                        NotificationHelper.cancelConfirmationNotification(context)

                        // 2. Invalidate obsolete scheduled alarms
                        val scheduler = ReminderScheduler(context)
                        scheduler.cancelReminder()

                        // 3. Re-read current Room state
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val todayTotal = db.drinkDao().getTodayTotalMl(cal.timeInMillis, cal.timeInMillis + 86400000)
                        val targetGoal = prefs.dailyGoalFlow.first()
                        val smartPacing = prefs.smartPacingEnabledFlow.first()

                        if (todayTotal >= targetGoal && targetGoal > 0) {
                            scheduler.scheduleWakeUpReminder(wakeHour, 0)
                        } else {
                            val pacing = AdaptiveReminderEngine().calculateNextReminder(
                                dailyTargetMl = targetGoal,
                                currentConsumedMl = todayTotal,
                                baseIntervalMins = baseInterval,
                                activeStartHour = wakeHour,
                                activeEndHour = sleepHour,
                                smartPacingEnabled = smartPacing,
                                consecutiveDismissals = 0,
                                userFavoriteAmount = defaultAmount,
                                scheduleProfile = schedule
                            )

                            if (pacing.isGoalCompleted) {
                                scheduler.scheduleWakeUpReminder(wakeHour, 0)
                            } else {
                                scheduler.scheduleNextReminder(pacing.calculatedDelayMinutes)
                            }
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
