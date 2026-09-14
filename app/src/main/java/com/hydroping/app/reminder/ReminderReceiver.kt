package com.hydroping.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import com.hydroping.app.data.HydrationDatabase
import com.hydroping.app.data.UserPreferencesRepository
import com.hydroping.app.domain.AdaptiveReminderEngine
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.DialogueEngine
import com.hydroping.app.domain.PersonalityType
import com.hydroping.app.utils.SoundEffectHelper
import com.hydroping.app.widget.HydroCompanionWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = HydrationDatabase.getDatabase(context)
                val prefs = UserPreferencesRepository(context)

                // 1. Check Focus Mode (DND timer)
                val focusUntilMs = prefs.focusUntilMsFlow.first()
                val nowMs = System.currentTimeMillis()
                if (focusUntilMs > nowMs) {
                    // Muted during Focus Mode. Reschedule for when Focus Mode expires.
                    val remainingMins = ((focusUntilMs - nowMs) / (60 * 1000L)).toInt().coerceAtLeast(5)
                    ReminderScheduler(context).scheduleNextReminder(remainingMins)
                    return@launch
                } else if (focusUntilMs > 0L) {
                    prefs.clearFocusMode()
                }

                // 2. Check Sleep / Quiet hours and Weekday vs Weekend Profile
                val schedule = prefs.scheduleProfileFlow.first()
                val calNow = Calendar.getInstance()
                val dayOfWeek = calNow.get(Calendar.DAY_OF_WEEK)
                val isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
                val wakeHour: Int = if (isWeekend) schedule.weekendStartHour else schedule.weekdayStartHour
                val sleepHour: Int = if (isWeekend) schedule.weekendEndHour else schedule.weekdayEndHour
                val baseInterval: Int = if (isWeekend) schedule.weekendIntervalMins else schedule.weekdayIntervalMins

                val currentHour: Int = calNow.get(Calendar.HOUR_OF_DAY)
                val isQuietHour: Boolean = if (sleepHour > wakeHour) {
                    currentHour >= sleepHour || currentHour < wakeHour
                } else {
                    currentHour >= sleepHour && currentHour < wakeHour
                }

                if (isQuietHour) {
                    NotificationHelper.cancelReminderNotification(context)
                    // Schedule next reminder for morning wake-up hour
                    ReminderScheduler(context).scheduleWakeUpReminder(wakeHour, 0)
                    return@launch
                }

                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val todayTotal = db.drinkDao().getTodayTotalMl(cal.timeInMillis, cal.timeInMillis + 86400000)
                val targetGoal = prefs.dailyGoalFlow.first()
                val smartPacing = prefs.smartPacingEnabledFlow.first()
                val dismissals = prefs.consecutiveDismissalsFlow.first()
                val userName = prefs.userNameFlow.first()

                // Goal Completion Invariant: stop daytime reminders once 100% daily target is reached
                if (todayTotal >= targetGoal && targetGoal > 0) {
                    NotificationHelper.cancelReminderNotification(context)
                    ReminderScheduler(context).scheduleWakeUpReminder(wakeHour, 0)
                    return@launch
                }

                val characterId = prefs.characterIdFlow.first()
                val favoriteAmount = prefs.favoriteAmountFlow.first()
                val character = CharacterCatalog.fromId(characterId)

                val pacing = AdaptiveReminderEngine().calculateNextReminder(
                    dailyTargetMl = targetGoal,
                    currentConsumedMl = todayTotal,
                    baseIntervalMins = baseInterval,
                    activeStartHour = wakeHour,
                    activeEndHour = sleepHour,
                    smartPacingEnabled = smartPacing,
                    consecutiveDismissals = dismissals,
                    userFavoriteAmount = favoriteAmount,
                    scheduleProfile = schedule
                )

                if (pacing.isGoalCompleted) {
                    NotificationHelper.cancelReminderNotification(context)
                    ReminderScheduler(context).scheduleWakeUpReminder(wakeHour, 0)
                    return@launch
                }

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

                val reminderSound = prefs.reminderSoundFlow.first()
                SoundEffectHelper.playSound(context, reminderSound)

                ReminderScheduler(context).scheduleNextReminder(pacing.calculatedDelayMinutes)

                try {
                    HydroCompanionWidget().updateAll(context)
                } catch (_: Exception) {}
            } finally {
                pendingResult.finish()
            }
        }
    }
}
