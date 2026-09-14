package com.hydroping.app.ui

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hydroping.app.data.DailyGoal
import com.hydroping.app.data.DrinkLog
import com.hydroping.app.data.HydrationDatabase
import com.hydroping.app.data.UserPreferencesRepository
import com.hydroping.app.domain.ActivityLevel
import com.hydroping.app.domain.AdaptiveReminderEngine
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.CharacterMood
import com.hydroping.app.domain.CharacterProfile
import com.hydroping.app.domain.CharacterStateMachine
import com.hydroping.app.domain.DialogueEngine
import com.hydroping.app.domain.GoalStrategy
import com.hydroping.app.domain.HydrationUnit
import com.hydroping.app.domain.MilestoneBadge
import com.hydroping.app.domain.MilestoneService
import com.hydroping.app.domain.NotificationStyle
import com.hydroping.app.domain.PersonalityType
import com.hydroping.app.domain.ReminderAnimation
import com.hydroping.app.domain.ReminderPacingInfo
import com.hydroping.app.domain.ReminderSound
import com.hydroping.app.domain.ScheduleProfile
import com.hydroping.app.domain.ThemeMode
import com.hydroping.app.reminder.NotificationHelper
import com.hydroping.app.reminder.ReminderScheduler
import com.hydroping.app.utils.BackupRestoreHelper
import com.hydroping.app.utils.CsvExportHelper
import com.hydroping.app.widget.HydroCompanionWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class RollingStats(
    val weeklyAverageDailyMl: Int = 0,
    val weeklyTotalSips: Int = 0,
    val averageSipVolumeMl: Int = 250
)

data class UiState(
    val userName: String = "",
    val currentTotalMl: Int = 0,
    val targetDailyMl: Int = 2000,
    val favoriteAmountMl: Int = 250,
    val progressPercent: Int = 0,
    val character: CharacterProfile = CharacterCatalog.Pikachu,
    val personality: PersonalityType = PersonalityType.CUTE,
    val mood: CharacterMood = CharacterMood.HYDRATED,
    val dialogue: String = "",
    val todayDrinks: List<DrinkLog> = emptyList(),
    val streakDays: Int = 1,
    val milestones: List<MilestoneBadge> = emptyList(),
    val rollingStats: RollingStats = RollingStats(),
    val dayStartTimeMs: Long = System.currentTimeMillis(),
    val activeHours: Pair<Int, Int> = Pair(7, 23),
    val baseReminderIntervalMins: Int = 60,
    val smartPacingEnabled: Boolean = true,
    val pacingInfo: ReminderPacingInfo? = null,
    val focusUntilMs: Long = 0L,
    val isOnboardingCompleted: Boolean = true,
    val unit: HydrationUnit = HydrationUnit.ML,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val minimalMode: Boolean = false,
    val notificationStyle: NotificationStyle = NotificationStyle.FRIENDLY,
    val reminderSound: ReminderSound = ReminderSound.CHIME,
    val vibrationEnabled: Boolean = true,
    val snoozeMins: Int = 15,
    val goalStrategy: GoalStrategy = GoalStrategy.MANUAL,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val healthConnectEnabled: Boolean = false,
    val wearSyncEnabled: Boolean = true,
    val scheduleProfile: ScheduleProfile = ScheduleProfile(),
    val reminderAnimation: ReminderAnimation = ReminderAnimation.LIQUID_WAVE,
    val recentDailyGoals: List<DailyGoal> = emptyList(),
    val showGoalCelebration: Boolean = false,
    val lastDeletedDrink: DrinkLog? = null,
    val showReminderModal: Boolean = false,
    val showCustomLogModal: Boolean = false,
    val showScreenGlow: Boolean = false,
    val weatherData: com.hydroping.app.data.WeatherData? = null,
    val lifetimeTotalMl: Int = 0,
    val totalDrinksLogged: Int = 0,
    val bestDayMl: Int = 0,
    val longestStreak: Int = 1
)

data class UserCorePrefs(
    val userName: String = "",
    val dailyGoal: Int = 2000,
    val favoriteAmount: Int = 250,
    val characterId: String = "pikachu",
    val personalityMode: String = "CUTE",
    val activeHours: Pair<Int, Int> = Pair(7, 23),
    val reminderInterval: Int = 60,
    val smartPacing: Boolean = true,
    val isOnboardingCompleted: Boolean = true
)

data class UserExtraPrefs(
    val unit: HydrationUnit = HydrationUnit.ML,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val minimalMode: Boolean = false,
    val notificationStyle: NotificationStyle = NotificationStyle.FRIENDLY,
    val reminderSound: ReminderSound = ReminderSound.CHIME,
    val vibrationEnabled: Boolean = true,
    val snoozeMins: Int = 15,
    val goalStrategy: GoalStrategy = GoalStrategy.MANUAL,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val healthConnectEnabled: Boolean = false,
    val wearSyncEnabled: Boolean = true,
    val scheduleProfile: ScheduleProfile = ScheduleProfile(),
    val reminderAnimation: ReminderAnimation = ReminderAnimation.LIQUID_WAVE
)

data class AppStateData(
    val todayDrinks: List<DrinkLog> = emptyList(),
    val todayTotal: Int = 0,
    val historyGoals: List<DailyGoal> = emptyList(),
    val isDrinking: Boolean = false,
    val isCelebrated: Boolean = false,
    val showCelebration: Boolean = false,
    val lastDeleted: DrinkLog? = null,
    val focusUntil: Long = 0L,
    val dayStartTime: Long = System.currentTimeMillis(),
    val showReminderModal: Boolean = false,
    val showCustomLogModal: Boolean = false,
    val showScreenGlow: Boolean = false,
    val weatherData: com.hydroping.app.data.WeatherData? = null,
    val lifetimeTotal: Int = 0,
    val totalDrinks: Int = 0,
    val bestDay: Int = 0,
    val maxStreak: Int = 1
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HydrationDatabase.getDatabase(application)
    private val prefs = UserPreferencesRepository(application)
    private val stateMachine = CharacterStateMachine()
    private val reminderEngine = AdaptiveReminderEngine()
    private val reminderScheduler = ReminderScheduler(application)

    private val _isDrinkingNow = MutableStateFlow(false)
    private val _isRecentlyCelebrated = MutableStateFlow(false)
    private val _showGoalCelebration = MutableStateFlow(false)
    private val _lastDeletedDrink = MutableStateFlow<DrinkLog?>(null)
    private val _showReminderModal = MutableStateFlow(false)
    private val _showCustomLogModal = MutableStateFlow(false)
    private val _showScreenGlow = MutableStateFlow(false)
    private val _weatherData = MutableStateFlow<com.hydroping.app.data.WeatherData?>(null)

    init {
        refreshWeather()
    }

    fun refreshWeather() {
        viewModelScope.launch {
            try {
                val data = com.hydroping.app.data.WeatherService.fetchCurrentWeather()
                _weatherData.value = data
            } catch (_: Exception) {}
        }
    }

    fun triggerScreenGlow(durationMs: Long = 3500L) {
        viewModelScope.launch {
            _showScreenGlow.value = true
            delay(durationMs)
            _showScreenGlow.value = false
        }
    }

    private fun getTodayStartMs(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getTodayEndMs(): Long = getTodayStartMs() + (24 * 60 * 60 * 1000)

    val todayDrinksFlow = db.drinkDao().getTodayDrinksFlow(getTodayStartMs(), getTodayEndMs())
    val todayTotalFlow = db.drinkDao().getTodayTotalMlFlow(getTodayStartMs(), getTodayEndMs())
    val lastDrinkFlow = db.drinkDao().getLastDrinkFlow()
    val historyGoalsFlow = db.drinkDao().getRecentDailyGoalsFlow()
    val lifetimeTotalFlow = db.drinkDao().getTotalLifetimeMlFlow()
    val totalDrinksCountFlow = db.drinkDao().getTotalDrinksCountFlow()
    val maxSingleDayFlow = db.drinkDao().getMaxSingleDayMlFlow()
    val maxStreakFlow = db.drinkDao().getMaxStreakFlow()

    private val userCorePrefsFlow = combine(
        prefs.userNameFlow,
        prefs.dailyGoalFlow,
        prefs.favoriteAmountFlow,
        prefs.characterIdFlow,
        prefs.personalityModeFlow
    ) { name, goal, fav, charId, pers ->
        UserCorePrefs(
            userName = name,
            dailyGoal = goal,
            favoriteAmount = fav,
            characterId = charId,
            personalityMode = pers
        )
    }.combine(
        combine(
            prefs.activeHoursFlow,
            prefs.reminderIntervalMinsFlow,
            prefs.smartPacingEnabledFlow,
            prefs.isOnboardingCompletedFlow
        ) { hours, interval, pacing, onboarding ->
            Tuple4(hours, interval, pacing, onboarding)
        }
    ) { c1, c2 ->
        c1.copy(
            activeHours = c2.t1,
            reminderInterval = c2.t2,
            smartPacing = c2.t3,
            isOnboardingCompleted = c2.t4
        )
    }

    private val userExtraPrefsFlow = combine(
        combine(
            prefs.unitFlow,
            prefs.themeModeFlow,
            prefs.minimalModeFlow,
            prefs.notificationStyleFlow,
            prefs.reminderSoundFlow
        ) { u, tm, mm, ns, rs ->
            Tuple5(u, tm, mm, ns, rs)
        },
        combine(
            prefs.vibrationEnabledFlow,
            prefs.snoozeMinsFlow,
            prefs.goalStrategyFlow,
            prefs.activityLevelFlow
        ) { vib, snz, strat, act ->
            Tuple4(vib, snz, strat, act)
        },
        combine(
            prefs.healthConnectEnabledFlow,
            prefs.wearSyncEnabledFlow,
            prefs.scheduleProfileFlow,
            prefs.reminderAnimationFlow
        ) { hc, ws, sp, anim ->
            Tuple4(hc, ws, sp, anim)
        }
    ) { g1, g2, g3 ->
        UserExtraPrefs(
            unit = g1.t1,
            themeMode = g1.t2,
            minimalMode = g1.t3,
            notificationStyle = g1.t4,
            reminderSound = g1.t5,
            vibrationEnabled = g2.t1,
            snoozeMins = g2.t2,
            goalStrategy = g2.t3,
            activityLevel = g2.t4,
            healthConnectEnabled = g3.t1,
            wearSyncEnabled = g3.t2,
            scheduleProfile = g3.t3,
            reminderAnimation = g3.t4
        )
    }

    private val appStateFlow = combine(
        combine(
            todayDrinksFlow,
            todayTotalFlow,
            historyGoalsFlow,
            _isDrinkingNow,
            _isRecentlyCelebrated
        ) { td, tt, hg, dn, rc ->
            Tuple5(td, tt, hg, dn, rc)
        },
        combine(
            _showGoalCelebration,
            _lastDeletedDrink,
            prefs.focusUntilMsFlow,
            prefs.dayStartTimeFlow
        ) { sg, ld, fu, ds ->
            Tuple4(sg, ld, fu, ds)
        },
        combine(
            _showReminderModal,
            _showCustomLogModal,
            _showScreenGlow,
            _weatherData
        ) { rm, clm, glow, wthr ->
            Tuple4(rm, clm, glow, wthr)
        },
        combine(
            lifetimeTotalFlow,
            totalDrinksCountFlow,
            maxSingleDayFlow,
            maxStreakFlow
        ) { lt, td, md, ms ->
            Tuple4(lt, td, md, ms)
        }
    ) { s1, s2, s3, s4 ->
        AppStateData(
            todayDrinks = s1.t1,
            todayTotal = s1.t2,
            historyGoals = s1.t3,
            isDrinking = s1.t4,
            isCelebrated = s1.t5,
            showCelebration = s2.t1,
            lastDeleted = s2.t2,
            focusUntil = s2.t3,
            dayStartTime = s2.t4,
            showReminderModal = s3.t1,
            showCustomLogModal = s3.t2,
            showScreenGlow = s3.t3,
            weatherData = s3.t4,
            lifetimeTotal = s4.t1,
            totalDrinks = s4.t2,
            bestDay = s4.t3,
            maxStreak = s4.t4
        )
    }

    val uiState: StateFlow<UiState> = combine(
        userCorePrefsFlow,
        userExtraPrefsFlow,
        appStateFlow
    ) { core, extra, appState ->
        val character = CharacterCatalog.allCharacters.find { it.id == core.characterId } ?: CharacterCatalog.Pikachu
        val personality = try {
            PersonalityType.valueOf(core.personalityMode)
        } catch (_: Exception) {
            PersonalityType.CUTE
        }

        val lastDrinkTime = appState.todayDrinks.firstOrNull()?.timestampMs

        val mood = stateMachine.determineMood(
            lastDrinkTimeMs = lastDrinkTime,
            currentTotalMl = appState.todayTotal,
            targetDailyMl = core.dailyGoal,
            quietStartHour = core.activeHours.second,
            quietEndHour = core.activeHours.first,
            isDrinkingNow = appState.isDrinking,
            isRecentlyCelebrated = appState.isCelebrated
        )

        val progressPercent = if (core.dailyGoal > 0) ((appState.todayTotal.toFloat() / core.dailyGoal) * 100).toInt() else 0

        val pacing = reminderEngine.calculateNextReminder(
            dailyTargetMl = core.dailyGoal,
            currentConsumedMl = appState.todayTotal,
            baseIntervalMins = core.reminderInterval,
            activeStartHour = core.activeHours.first,
            activeEndHour = core.activeHours.second,
            smartPacingEnabled = core.smartPacing,
            consecutiveDismissals = 0
        )

        val dialogue = DialogueEngine.getDialogue(
            userName = core.userName,
            characterId = core.characterId,
            personality = personality,
            mood = mood,
            percentProgress = progressPercent,
            isBehindTarget = pacing.isBehindSchedule,
            deficitMl = pacing.deficitMl,
            remainingMl = pacing.remainingMl
        )

        val streakDays = calculateStreak(appState.todayDrinks.isNotEmpty(), appState.historyGoals)
        val milestones = MilestoneService.getMilestones(streakDays)

        val avgDailyMl = if (appState.historyGoals.isNotEmpty()) {
            (appState.historyGoals.sumOf { it.totalConsumedMl } + appState.todayTotal) / (appState.historyGoals.size + 1)
        } else if (appState.todayDrinks.isNotEmpty()) appState.todayTotal else core.dailyGoal

        UiState(
            userName = core.userName,
            currentTotalMl = appState.todayTotal,
            targetDailyMl = core.dailyGoal,
            favoriteAmountMl = core.favoriteAmount,
            progressPercent = progressPercent,
            character = character,
            personality = personality,
            mood = mood,
            dialogue = dialogue,
            todayDrinks = appState.todayDrinks,
            streakDays = streakDays,
            milestones = milestones,
            rollingStats = RollingStats(
                weeklyAverageDailyMl = avgDailyMl,
                weeklyTotalSips = appState.todayDrinks.size + 14,
                averageSipVolumeMl = core.favoriteAmount
            ),
            dayStartTimeMs = appState.dayStartTime,
            activeHours = core.activeHours,
            baseReminderIntervalMins = core.reminderInterval,
            smartPacingEnabled = core.smartPacing,
            pacingInfo = pacing,
            focusUntilMs = appState.focusUntil,
            isOnboardingCompleted = core.isOnboardingCompleted,
            unit = extra.unit,
            themeMode = extra.themeMode,
            minimalMode = extra.minimalMode,
            notificationStyle = extra.notificationStyle,
            reminderSound = extra.reminderSound,
            vibrationEnabled = extra.vibrationEnabled,
            snoozeMins = extra.snoozeMins,
            goalStrategy = extra.goalStrategy,
            activityLevel = extra.activityLevel,
            healthConnectEnabled = extra.healthConnectEnabled,
            wearSyncEnabled = extra.wearSyncEnabled,
            scheduleProfile = extra.scheduleProfile,
            reminderAnimation = extra.reminderAnimation,
            recentDailyGoals = appState.historyGoals,
            showGoalCelebration = appState.showCelebration,
            lastDeletedDrink = appState.lastDeleted,
            showReminderModal = appState.showReminderModal,
            showCustomLogModal = appState.showCustomLogModal,
            showScreenGlow = appState.showScreenGlow || appState.showReminderModal,
            weatherData = appState.weatherData,
            lifetimeTotalMl = appState.lifetimeTotal,
            totalDrinksLogged = appState.totalDrinks,
            bestDayMl = maxOf(appState.bestDay, appState.todayTotal),
            longestStreak = maxOf(appState.maxStreak, streakDays)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )

    fun setReminderAnimation(animation: ReminderAnimation) {
        viewModelScope.launch {
            prefs.setReminderAnimation(animation)
        }
    }

    fun showReminderModal(show: Boolean) {
        _showReminderModal.value = show
        if (show) {
            triggerScreenGlow(4000L)
        }
    }

    fun showCustomLogModal(show: Boolean) {
        _showCustomLogModal.value = show
    }

    fun snoozeReminder() {
        viewModelScope.launch {
            val snoozeMins = prefs.snoozeMinsFlow.first()
            reminderScheduler.scheduleNextReminder(snoozeMins)
            _showReminderModal.value = false
            NotificationHelper.cancelNotification(getApplication())
        }
    }

    fun skipReminder() {
        viewModelScope.launch {
            _showReminderModal.value = false
            NotificationHelper.cancelNotification(getApplication())
            rescheduleReminder()
        }
    }

    fun setStudyIntervalMins(mins: Int) {
        viewModelScope.launch {
            prefs.setStudyIntervalMins(mins)
            rescheduleReminder()
        }
    }

    init {
        viewModelScope.launch {
            prefs.initDayStartTimeIfNeeded()
            rescheduleReminder()
        }
    }

    private suspend fun rescheduleReminder() {
        val todayTotal = db.drinkDao().getTodayTotalMl(getTodayStartMs(), getTodayEndMs())
        val dailyGoal = prefs.dailyGoalFlow.first()
        val schedule = prefs.scheduleProfileFlow.first()
        val favoriteAmount = prefs.favoriteAmountFlow.first()
        val smartPacing = prefs.smartPacingEnabledFlow.first()
        val focusUntil = prefs.focusUntilMsFlow.first()

        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
        val wakeHour: Int = if (isWeekend) schedule.weekendStartHour else schedule.weekdayStartHour
        val sleepHour: Int = if (isWeekend) schedule.weekendEndHour else schedule.weekdayEndHour
        val baseInterval: Int = if (isWeekend) schedule.weekendIntervalMins else schedule.weekdayIntervalMins

        // If goal is already met today, schedule wake-up reminder for tomorrow morning and do NOT send daytime reminders
        if (todayTotal >= dailyGoal && dailyGoal > 0) {
            reminderScheduler.scheduleWakeUpReminder(wakeHour, 0)
            return
        }

        val nowMs = System.currentTimeMillis()
        if (focusUntil > nowMs) {
            val remainingMins = ((focusUntil - nowMs) / (60 * 1000L)).toInt().coerceAtLeast(5)
            reminderScheduler.scheduleNextReminder(remainingMins)
            return
        }

        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val isQuietHour = if (sleepHour > wakeHour) {
            currentHour >= sleepHour || currentHour < wakeHour
        } else {
            currentHour >= sleepHour && currentHour < wakeHour
        }
        if (isQuietHour) {
            reminderScheduler.scheduleWakeUpReminder(wakeHour, 0)
            return
        }

        val pacing = reminderEngine.calculateNextReminder(
            dailyTargetMl = dailyGoal,
            currentConsumedMl = todayTotal,
            baseIntervalMins = baseInterval,
            activeStartHour = wakeHour,
            activeEndHour = sleepHour,
            smartPacingEnabled = smartPacing,
            consecutiveDismissals = 0,
            userFavoriteAmount = favoriteAmount,
            scheduleProfile = schedule
        )

        if (pacing.isGoalCompleted) {
            reminderScheduler.scheduleWakeUpReminder(wakeHour, 0)
        } else {
            reminderScheduler.scheduleNextReminder(pacing.calculatedDelayMinutes)
        }
    }

    fun setUserName(name: String) {
        viewModelScope.launch {
            prefs.setUserName(name)
        }
    }

    fun startFocusMode(durationMinutes: Int) {
        viewModelScope.launch {
            val focusUntil = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
            prefs.setFocusUntilMs(focusUntil)
            reminderScheduler.scheduleNextReminder(durationMinutes)
            NotificationHelper.cancelNotification(getApplication())
        }
    }

    fun cancelFocusMode() {
        viewModelScope.launch {
            prefs.clearFocusMode()
            rescheduleReminder()
        }
    }

    fun logDrink(amountMl: Int, source: String = "APP") {
        viewModelScope.launch {
            val prevTotal = db.drinkDao().getTodayTotalMl(getTodayStartMs(), getTodayEndMs())
            val target = prefs.dailyGoalFlow.first()

            db.drinkDao().insertDrink(
                DrinkLog(
                    amountMl = amountMl,
                    source = source
                )
            )

            val newTotal = prevTotal + amountMl
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            db.drinkDao().insertOrUpdateDailyGoal(
                DailyGoal(
                    dateString = todayStr,
                    targetMl = target,
                    totalConsumedMl = newTotal,
                    streakCount = uiState.value.streakDays
                )
            )

            prefs.resetDismissalBackoff()

            // Trigger Goal Celebration if passing 100% threshold
            if (prevTotal < target && newTotal >= target) {
                _showGoalCelebration.value = true
            }

            _isRecentlyCelebrated.value = true
            _isDrinkingNow.value = false

            rescheduleReminder()
            NotificationHelper.cancelNotification(getApplication())

            try {
                HydroCompanionWidget().updateAll(getApplication())
            } catch (_: Exception) {}

            delay(3500)
            _isRecentlyCelebrated.value = false
        }
    }

    fun updateDrink(drink: DrinkLog) {
        viewModelScope.launch {
            db.drinkDao().updateDrink(drink)
            syncTodayDailyGoal()
            rescheduleReminder()
            NotificationHelper.cancelNotification(getApplication())
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    fun deleteDrink(drink: DrinkLog) {
        viewModelScope.launch {
            _lastDeletedDrink.value = drink
            db.drinkDao().deleteDrink(drink)
            syncTodayDailyGoal()
            rescheduleReminder()
            NotificationHelper.cancelNotification(getApplication())
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    fun undoLastDelete() {
        viewModelScope.launch {
            val drink = _lastDeletedDrink.value ?: return@launch
            db.drinkDao().insertDrink(drink)
            _lastDeletedDrink.value = null
            syncTodayDailyGoal()
            rescheduleReminder()
            NotificationHelper.cancelNotification(getApplication())
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    private suspend fun syncTodayDailyGoal() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayTotal = db.drinkDao().getTodayTotalMl(getTodayStartMs(), getTodayEndMs())
        val target = prefs.dailyGoalFlow.first()
        db.drinkDao().insertOrUpdateDailyGoal(
            DailyGoal(
                dateString = todayStr,
                targetMl = target,
                totalConsumedMl = todayTotal,
                streakCount = uiState.value.streakDays
            )
        )
    }

    fun resetTodayProgress() {
        viewModelScope.launch {
            db.drinkDao().deleteTodayDrinks(getTodayStartMs(), getTodayEndMs())
            syncTodayDailyGoal()
            rescheduleReminder()
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            db.drinkDao().clearAllDrinkLogs()
            db.drinkDao().clearAllDailyGoals()
            prefs.clearAllPreferences()
            rescheduleReminder()
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    fun dismissGoalCelebration() {
        _showGoalCelebration.value = false
    }

    fun startDrinkingMode() {
        _isDrinkingNow.value = true
    }

    fun setDailyGoal(goalMl: Int) {
        viewModelScope.launch {
            prefs.setDailyGoal(goalMl)
            syncTodayDailyGoal()
            rescheduleReminder()
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    fun setFavoriteAmount(amountMl: Int) {
        viewModelScope.launch {
            prefs.setFavoriteAmount(amountMl)
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    fun setReminderInterval(minutes: Int) {
        viewModelScope.launch {
            prefs.setReminderIntervalMins(minutes)
            rescheduleReminder()
        }
    }

    fun setSmartPacingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setSmartPacingEnabled(enabled)
            rescheduleReminder()
        }
    }

    fun setActiveHours(startHour: Int, endHour: Int) {
        viewModelScope.launch {
            prefs.setActiveHours(startHour, endHour)
            rescheduleReminder()
        }
    }

    fun setUnit(unit: HydrationUnit) {
        viewModelScope.launch {
            prefs.setUnit(unit)
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            prefs.setThemeMode(mode)
        }
    }

    fun setMinimalMode(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setMinimalMode(enabled)
        }
    }

    fun setNotificationStyle(style: NotificationStyle) {
        viewModelScope.launch {
            prefs.setNotificationStyle(style)
        }
    }

    fun setReminderSound(sound: ReminderSound) {
        viewModelScope.launch {
            prefs.setReminderSound(sound)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setVibrationEnabled(enabled)
        }
    }

    fun setSnoozeMins(mins: Int) {
        viewModelScope.launch {
            prefs.setSnoozeMins(mins)
        }
    }

    fun setGoalStrategy(strategy: GoalStrategy) {
        viewModelScope.launch {
            prefs.setGoalStrategy(strategy)
        }
    }

    fun setActivityLevel(level: ActivityLevel) {
        viewModelScope.launch {
            prefs.setActivityLevel(level)
        }
    }

    fun setHealthConnectEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setHealthConnectEnabled(enabled)
        }
    }

    fun setWearSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setWearSyncEnabled(enabled)
        }
    }

    fun updateScheduleProfile(
        weekdayStart: Int,
        weekdayEnd: Int,
        weekendStart: Int,
        weekendEnd: Int,
        weekdayInterval: Int,
        weekendInterval: Int,
        workMode: Boolean,
        studyMode: Boolean,
        exerciseMode: Boolean,
        weatherBooster: Boolean,
        studyIntervalMins: Int = 45,
        weekdayStartMinute: Int = 0,
        weekdayEndMinute: Int = 0,
        weekendStartMinute: Int = 0,
        weekendEndMinute: Int = 0
    ) {
        viewModelScope.launch {
            prefs.updateScheduleProfile(
                weekdayStart, weekdayEnd, weekendStart, weekendEnd,
                weekdayInterval, weekendInterval, workMode, studyMode,
                exerciseMode, weatherBooster, studyIntervalMins,
                weekdayStartMinute, weekdayEndMinute, weekendStartMinute, weekendEndMinute
            )
            rescheduleReminder()
        }
    }

    fun setCharacterAndPersonality(characterId: String, personality: PersonalityType) {
        viewModelScope.launch {
            prefs.setCharacterAndPersonality(characterId, personality.name)
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    fun completeOnboarding(
        userName: String,
        dailyGoalMl: Int,
        reminderIntervalMins: Int,
        smartPacingEnabled: Boolean,
        characterId: String,
        personality: PersonalityType,
        unit: HydrationUnit = HydrationUnit.ML,
        activityLevel: ActivityLevel = ActivityLevel.MODERATE,
        wakeHour: Int = 7,
        sleepHour: Int = 23,
        animation: ReminderAnimation = ReminderAnimation.LIQUID_WAVE
    ) {
        viewModelScope.launch {
            prefs.setUserName(userName)
            prefs.setDailyGoal(dailyGoalMl)
            prefs.setReminderIntervalMins(reminderIntervalMins)
            prefs.setSmartPacingEnabled(smartPacingEnabled)
            prefs.setCharacterAndPersonality(characterId, personality.name)
            prefs.setUnit(unit)
            prefs.setActivityLevel(activityLevel)
            prefs.setActiveHours(wakeHour, sleepHour)
            prefs.setReminderAnimation(animation)
            prefs.setOnboardingCompleted(true)
            rescheduleReminder()
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
    }

    suspend fun getExportCsvData(): String {
        val drinks = db.drinkDao().getAllDrinksList()
        return CsvExportHelper.generateCsvString(drinks)
    }

    suspend fun getExportJsonBackup(): String {
        return BackupRestoreHelper.exportBackupJson(
            db.drinkDao(),
            uiState.value.userName,
            uiState.value.targetDailyMl
        )
    }

    suspend fun restoreJsonBackup(jsonString: String): Result<Int> {
        val result = BackupRestoreHelper.restoreBackupJson(jsonString, db.drinkDao())
        if (result.isSuccess) {
            syncTodayDailyGoal()
            rescheduleReminder()
            try { HydroCompanionWidget().updateAll(getApplication()) } catch (_: Exception) {}
        }
        return result
    }

    private fun calculateStreak(hasLoggedToday: Boolean, historyGoals: List<DailyGoal>): Int {
        val completedPastDays = historyGoals.count { it.totalConsumedMl >= it.targetMl }
        return if (hasLoggedToday) completedPastDays + 1 else completedPastDays.coerceAtLeast(1)
    }
}

// Helper tuple classes for combining flows
data class Tuple3<T1, T2, T3>(
    val t1: T1, val t2: T2, val t3: T3
)

data class Tuple4<T1, T2, T3, T4>(
    val t1: T1, val t2: T2, val t3: T3, val t4: T4
)

data class Tuple5<T1, T2, T3, T4, T5>(
    val t1: T1, val t2: T2, val t3: T3, val t4: T4, val t5: T5
)

data class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(
    val t1: T1, val t2: T2, val t3: T3, val t4: T4, val t5: T5,
    val t6: T6, val t7: T8, val t8: T8, val t9: T9, val t10: T10
)

