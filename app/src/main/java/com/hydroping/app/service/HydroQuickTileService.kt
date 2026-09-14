package com.hydroping.app.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.updateAll
import com.hydroping.app.data.DrinkLog
import com.hydroping.app.data.HydrationDatabase
import com.hydroping.app.data.UserPreferencesRepository
import com.hydroping.app.reminder.NotificationHelper
import com.hydroping.app.reminder.ReminderScheduler
import com.hydroping.app.widget.HydroCompanionWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class HydroQuickTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        CoroutineScope(Dispatchers.IO).launch {
            val db = HydrationDatabase.getDatabase(applicationContext)
            val prefs = UserPreferencesRepository(applicationContext)
            val favAmount = prefs.favoriteAmountFlow.first()

            db.drinkDao().insertDrink(
                DrinkLog(
                    amountMl = favAmount,
                    source = "QS_TILE"
                )
            )

            prefs.resetDismissalBackoff()

            val cal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val todayTotal = db.drinkDao().getTodayTotalMl(cal.timeInMillis, cal.timeInMillis + 86400000)
            val targetGoal = prefs.dailyGoalFlow.first()
            val schedule = prefs.scheduleProfileFlow.first()
            val smartPacing = prefs.smartPacingEnabledFlow.first()

            val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
            val isWeekend = (dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY)
            val wakeHour = if (isWeekend) schedule.weekendStartHour else schedule.weekdayStartHour
            val sleepHour = if (isWeekend) schedule.weekendEndHour else schedule.weekdayEndHour
            val baseInterval = if (isWeekend) schedule.weekendIntervalMins else schedule.weekdayIntervalMins

            val scheduler = ReminderScheduler(applicationContext)
            NotificationHelper.cancelNotification(applicationContext)

            if (todayTotal >= targetGoal && targetGoal > 0) {
                scheduler.scheduleWakeUpReminder(wakeHour, 0)
            } else {
                val pacing = com.hydroping.app.domain.AdaptiveReminderEngine().calculateNextReminder(
                    dailyTargetMl = targetGoal,
                    currentConsumedMl = todayTotal,
                    baseIntervalMins = baseInterval,
                    activeStartHour = wakeHour,
                    activeEndHour = sleepHour,
                    smartPacingEnabled = smartPacing,
                    consecutiveDismissals = 0,
                    userFavoriteAmount = favAmount,
                    scheduleProfile = schedule
                )
                if (pacing.isGoalCompleted) {
                    scheduler.scheduleWakeUpReminder(wakeHour, 0)
                } else {
                    scheduler.scheduleNextReminder(pacing.calculatedDelayMinutes)
                }
            }

            try {
                HydroCompanionWidget().updateAll(applicationContext)
            } catch (_: Exception) {}

            updateTileState()
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = UserPreferencesRepository(applicationContext)
            val favAmount = prefs.favoriteAmountFlow.first()

            tile.state = Tile.STATE_ACTIVE
            tile.label = "HydroPing"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = "+$favAmount ml"
            }
            tile.updateTile()
        }
    }
}
