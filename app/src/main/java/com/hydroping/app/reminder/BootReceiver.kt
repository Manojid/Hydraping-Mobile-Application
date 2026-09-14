package com.hydroping.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hydroping.app.data.HydrationDatabase
import com.hydroping.app.data.UserPreferencesRepository
import com.hydroping.app.domain.AdaptiveReminderEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    NotificationHelper.cancelAllNotifications(context)

                    val db = HydrationDatabase.getDatabase(context)
                    val prefs = UserPreferencesRepository(context)

                    val cal = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val todayTotal = db.drinkDao().getTodayTotalMl(cal.timeInMillis, cal.timeInMillis + 86400000)
                    val targetGoal = prefs.dailyGoalFlow.first()
                    val schedule = prefs.scheduleProfileFlow.first()
                    val smartPacing = prefs.smartPacingEnabledFlow.first()
                    val defaultAmount = prefs.favoriteAmountFlow.first()

                    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                    val isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
                    val wakeHour = if (isWeekend) schedule.weekendStartHour else schedule.weekdayStartHour
                    val sleepHour = if (isWeekend) schedule.weekendEndHour else schedule.weekdayEndHour
                    val baseInterval = if (isWeekend) schedule.weekendIntervalMins else schedule.weekdayIntervalMins

                    val scheduler = ReminderScheduler(context)

                    val calNow = Calendar.getInstance()
                    val currentHour = calNow.get(Calendar.HOUR_OF_DAY)
                    val isQuietHour = if (sleepHour > wakeHour) {
                        currentHour >= sleepHour || currentHour < wakeHour
                    } else {
                        currentHour >= sleepHour && currentHour < wakeHour
                    }

                    if (isQuietHour || (todayTotal >= targetGoal && targetGoal > 0)) {
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
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
