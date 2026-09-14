package com.hydroping.app.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.hydroping.app.data.DrinkLog
import com.hydroping.app.data.HydrationDatabase
import com.hydroping.app.data.UserPreferencesRepository
import com.hydroping.app.domain.AdaptiveReminderEngine
import com.hydroping.app.reminder.NotificationHelper
import com.hydroping.app.reminder.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

class WidgetDrinkActionCallback : ActionCallback {

    companion object {
        val KEY_AMOUNT_ML = ActionParameters.Key<Int>("key_amount_ml")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = UserPreferencesRepository(context)
        val defaultAmount = prefs.favoriteAmountFlow.first()
        val amount = parameters[KEY_AMOUNT_ML] ?: defaultAmount

        withContext(Dispatchers.IO) {
            val db = HydrationDatabase.getDatabase(context)
            db.drinkDao().insertDrink(
                DrinkLog(
                    amountMl = amount,
                    source = "WIDGET"
                )
            )

            prefs.resetDismissalBackoff()

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
            NotificationHelper.cancelNotification(context)

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

            // Update all widgets
            HydroCompanionWidget().updateAll(context)
        }
    }
}
