package com.hydroping.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hydroping.app.domain.ActivityLevel
import com.hydroping.app.domain.GoalStrategy
import com.hydroping.app.domain.HydrationUnit
import com.hydroping.app.domain.NotificationStyle
import com.hydroping.app.domain.ReminderAnimation
import com.hydroping.app.domain.ReminderSound
import com.hydroping.app.domain.ScheduleProfile
import com.hydroping.app.domain.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hydroping_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_DAILY_GOAL = intPreferencesKey("daily_goal_ml")
        val KEY_FAVORITE_AMOUNT = intPreferencesKey("favorite_amount_ml")
        val KEY_REMINDER_INTERVAL_MINS = intPreferencesKey("reminder_interval_mins")
        val KEY_SMART_PACING_ENABLED = booleanPreferencesKey("smart_pacing_enabled")
        val KEY_ACTIVE_START_HOUR = intPreferencesKey("active_start_hour") // 7 AM
        val KEY_ACTIVE_END_HOUR = intPreferencesKey("active_end_hour")     // 11 PM (23)
        val KEY_CHARACTER_ID = stringPreferencesKey("character_id")       // "droppy", "whiskers"
        val KEY_PERSONALITY_MODE = stringPreferencesKey("personality_mode") // "CUTE", "SARCASTIC", "MINIMAL"
        val KEY_CONSECUTIVE_DISMISSALS = intPreferencesKey("consecutive_dismissals")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val KEY_DAY_START_TIME = longPreferencesKey("day_start_time")
        val KEY_FOCUS_UNTIL_MS = longPreferencesKey("focus_until_ms")

        // New Advanced Feature Keys
        val KEY_UNIT = stringPreferencesKey("hydration_unit")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_MINIMAL_MODE = booleanPreferencesKey("minimal_mode_enabled")
        val KEY_NOTIFICATION_STYLE = stringPreferencesKey("notification_style")
        val KEY_REMINDER_SOUND = stringPreferencesKey("reminder_sound")
        val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val KEY_SNOOZE_MINS = intPreferencesKey("default_snooze_mins")
        val KEY_WEEKDAY_START_HOUR = intPreferencesKey("weekday_start_hour")
        val KEY_WEEKDAY_START_MINUTE = intPreferencesKey("weekday_start_minute")
        val KEY_WEEKDAY_END_HOUR = intPreferencesKey("weekday_end_hour")
        val KEY_WEEKDAY_END_MINUTE = intPreferencesKey("weekday_end_minute")
        val KEY_WEEKEND_START_HOUR = intPreferencesKey("weekend_start_hour")
        val KEY_WEEKEND_START_MINUTE = intPreferencesKey("weekend_start_minute")
        val KEY_WEEKEND_END_HOUR = intPreferencesKey("weekend_end_hour")
        val KEY_WEEKEND_END_MINUTE = intPreferencesKey("weekend_end_minute")
        val KEY_WEEKDAY_INTERVAL_MINS = intPreferencesKey("weekday_interval_mins")
        val KEY_WEEKEND_INTERVAL_MINS = intPreferencesKey("weekend_interval_mins")
        val KEY_WORK_MODE = booleanPreferencesKey("work_mode_enabled")
        val KEY_STUDY_MODE = booleanPreferencesKey("study_mode_enabled")
        val KEY_EXERCISE_MODE = booleanPreferencesKey("exercise_mode_enabled")
        val KEY_WEATHER_BOOSTER = booleanPreferencesKey("weather_booster_enabled")
        val KEY_GOAL_STRATEGY = stringPreferencesKey("goal_strategy")
        val KEY_ACTIVITY_LEVEL = stringPreferencesKey("activity_level")
        val KEY_HEALTH_CONNECT = booleanPreferencesKey("health_connect_enabled")
        val KEY_WEAR_SYNC = booleanPreferencesKey("wear_sync_enabled")
        val KEY_STUDY_INTERVAL_MINS = intPreferencesKey("study_interval_mins")
        val KEY_REMINDER_ANIMATION = stringPreferencesKey("reminder_animation")
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        val saved = prefs[KEY_USER_NAME] ?: ""
        if (saved == "Alex") "" else saved
    }

    val dailyGoalFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_DAILY_GOAL] ?: 2000
    }

    val favoriteAmountFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_FAVORITE_AMOUNT] ?: 250
    }

    val reminderIntervalMinsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_REMINDER_INTERVAL_MINS] ?: 60
    }

    val smartPacingEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SMART_PACING_ENABLED] ?: true
    }

    val activeHoursFlow: Flow<Pair<Int, Int>> = context.dataStore.data.map { prefs ->
        Pair(prefs[KEY_ACTIVE_START_HOUR] ?: 7, prefs[KEY_ACTIVE_END_HOUR] ?: 23)
    }

    val characterIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        val id = prefs[KEY_CHARACTER_ID] ?: "pikachu"
        if (id.equals("droppy", ignoreCase = true)) "pikachu" else id
    }

    val personalityModeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_PERSONALITY_MODE] ?: "CUTE"
    }

    val isOnboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val hapticsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAPTICS_ENABLED] ?: true
    }

    val dayStartTimeFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_DAY_START_TIME] ?: System.currentTimeMillis()
    }

    val consecutiveDismissalsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_CONSECUTIVE_DISMISSALS] ?: 0
    }

    val focusUntilMsFlow: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_FOCUS_UNTIL_MS] ?: 0L
    }

    val unitFlow: Flow<HydrationUnit> = context.dataStore.data.map { prefs ->
        val str = prefs[KEY_UNIT] ?: "ML"
        try { HydrationUnit.valueOf(str) } catch (_: Exception) { HydrationUnit.ML }
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val str = prefs[KEY_THEME_MODE] ?: "DARK"
        try { ThemeMode.valueOf(str) } catch (_: Exception) { ThemeMode.DARK }
    }

    val minimalModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_MINIMAL_MODE] ?: false
    }

    val notificationStyleFlow: Flow<NotificationStyle> = context.dataStore.data.map { prefs ->
        val str = prefs[KEY_NOTIFICATION_STYLE] ?: "FRIENDLY"
        try { NotificationStyle.valueOf(str) } catch (_: Exception) { NotificationStyle.FRIENDLY }
    }

    val reminderSoundFlow: Flow<ReminderSound> = context.dataStore.data.map { prefs ->
        val str = prefs[KEY_REMINDER_SOUND] ?: "CHIME"
        try { ReminderSound.valueOf(str) } catch (_: Exception) { ReminderSound.CHIME }
    }

    val vibrationEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_VIBRATION_ENABLED] ?: true
    }

    val snoozeMinsFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_SNOOZE_MINS] ?: 15
    }

    val goalStrategyFlow: Flow<GoalStrategy> = context.dataStore.data.map { prefs ->
        val str = prefs[KEY_GOAL_STRATEGY] ?: "MANUAL"
        try { GoalStrategy.valueOf(str) } catch (_: Exception) { GoalStrategy.MANUAL }
    }

    val activityLevelFlow: Flow<ActivityLevel> = context.dataStore.data.map { prefs ->
        val str = prefs[KEY_ACTIVITY_LEVEL] ?: "MODERATE"
        try { ActivityLevel.valueOf(str) } catch (_: Exception) { ActivityLevel.MODERATE }
    }

    val healthConnectEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HEALTH_CONNECT] ?: false
    }

    val wearSyncEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_WEAR_SYNC] ?: true
    }

    val scheduleProfileFlow: Flow<ScheduleProfile> = context.dataStore.data.map { prefs ->
        ScheduleProfile(
            weekdayStartHour = prefs[KEY_WEEKDAY_START_HOUR] ?: 7,
            weekdayStartMinute = prefs[KEY_WEEKDAY_START_MINUTE] ?: 0,
            weekdayEndHour = prefs[KEY_WEEKDAY_END_HOUR] ?: 23,
            weekdayEndMinute = prefs[KEY_WEEKDAY_END_MINUTE] ?: 0,
            weekendStartHour = prefs[KEY_WEEKEND_START_HOUR] ?: 8,
            weekendStartMinute = prefs[KEY_WEEKEND_START_MINUTE] ?: 0,
            weekendEndHour = prefs[KEY_WEEKEND_END_HOUR] ?: 23,
            weekendEndMinute = prefs[KEY_WEEKEND_END_MINUTE] ?: 0,
            weekdayIntervalMins = prefs[KEY_WEEKDAY_INTERVAL_MINS] ?: (prefs[KEY_REMINDER_INTERVAL_MINS] ?: 60),
            weekendIntervalMins = prefs[KEY_WEEKEND_INTERVAL_MINS] ?: 90,
            studyIntervalMins = prefs[KEY_STUDY_INTERVAL_MINS] ?: 45,
            workModeEnabled = prefs[KEY_WORK_MODE] ?: false,
            studyModeEnabled = prefs[KEY_STUDY_MODE] ?: false,
            exerciseModeEnabled = prefs[KEY_EXERCISE_MODE] ?: false,
            weatherBoosterEnabled = prefs[KEY_WEATHER_BOOSTER] ?: false
        )
    }

    suspend fun setUserName(name: String) {
        val trimmed = name.trim()
        context.dataStore.edit { it[KEY_USER_NAME] = trimmed }
    }

    suspend fun setDailyGoal(goalMl: Int) {
        context.dataStore.edit { it[KEY_DAILY_GOAL] = goalMl }
    }

    suspend fun setFavoriteAmount(amountMl: Int) {
        context.dataStore.edit { it[KEY_FAVORITE_AMOUNT] = amountMl }
    }

    suspend fun setReminderIntervalMins(mins: Int) {
        context.dataStore.edit { it[KEY_REMINDER_INTERVAL_MINS] = mins }
    }

    suspend fun setSmartPacingEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SMART_PACING_ENABLED] = enabled }
    }

    suspend fun setActiveHours(startHour: Int, endHour: Int) {
        context.dataStore.edit {
            it[KEY_ACTIVE_START_HOUR] = startHour
            it[KEY_ACTIVE_END_HOUR] = endHour
        }
    }

    suspend fun setCharacterAndPersonality(characterId: String, personality: String) {
        context.dataStore.edit {
            it[KEY_CHARACTER_ID] = characterId
            it[KEY_PERSONALITY_MODE] = personality
        }
    }

    suspend fun setUnit(unit: HydrationUnit) {
        context.dataStore.edit { it[KEY_UNIT] = unit.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setMinimalMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_MINIMAL_MODE] = enabled }
    }

    suspend fun setNotificationStyle(style: NotificationStyle) {
        context.dataStore.edit { it[KEY_NOTIFICATION_STYLE] = style.name }
    }

    suspend fun setReminderSound(sound: ReminderSound) {
        context.dataStore.edit { it[KEY_REMINDER_SOUND] = sound.name }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION_ENABLED] = enabled }
    }

    suspend fun setSnoozeMins(mins: Int) {
        context.dataStore.edit { it[KEY_SNOOZE_MINS] = mins }
    }

    suspend fun setGoalStrategy(strategy: GoalStrategy) {
        context.dataStore.edit { it[KEY_GOAL_STRATEGY] = strategy.name }
    }

    suspend fun setActivityLevel(level: ActivityLevel) {
        context.dataStore.edit { it[KEY_ACTIVITY_LEVEL] = level.name }
    }

    val reminderAnimationFlow: Flow<ReminderAnimation> = context.dataStore.data.map { prefs ->
        val str = prefs[KEY_REMINDER_ANIMATION] ?: "LIQUID_WAVE"
        try { ReminderAnimation.valueOf(str) } catch (_: Exception) { ReminderAnimation.LIQUID_WAVE }
    }

    suspend fun setReminderAnimation(animation: ReminderAnimation) {
        context.dataStore.edit { it[KEY_REMINDER_ANIMATION] = animation.name }
    }

    suspend fun setHealthConnectEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HEALTH_CONNECT] = enabled }
    }

    suspend fun setWearSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_WEAR_SYNC] = enabled }
    }

    suspend fun updateScheduleProfile(
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
        context.dataStore.edit {
            it[KEY_WEEKDAY_START_HOUR] = weekdayStart
            it[KEY_WEEKDAY_START_MINUTE] = weekdayStartMinute
            it[KEY_WEEKDAY_END_HOUR] = weekdayEnd
            it[KEY_WEEKDAY_END_MINUTE] = weekdayEndMinute
            it[KEY_WEEKEND_START_HOUR] = weekendStart
            it[KEY_WEEKEND_START_MINUTE] = weekendStartMinute
            it[KEY_WEEKEND_END_HOUR] = weekendEnd
            it[KEY_WEEKEND_END_MINUTE] = weekendEndMinute
            it[KEY_WEEKDAY_INTERVAL_MINS] = weekdayInterval
            it[KEY_WEEKEND_INTERVAL_MINS] = weekendInterval
            it[KEY_STUDY_INTERVAL_MINS] = studyIntervalMins
            it[KEY_WORK_MODE] = workMode
            it[KEY_STUDY_MODE] = studyMode
            it[KEY_EXERCISE_MODE] = exerciseMode
            it[KEY_WEATHER_BOOSTER] = weatherBooster
        }
    }

    suspend fun setStudyIntervalMins(mins: Int) {
        context.dataStore.edit { it[KEY_STUDY_INTERVAL_MINS] = mins }
    }

    suspend fun setFocusUntilMs(timestampMs: Long) {
        context.dataStore.edit { it[KEY_FOCUS_UNTIL_MS] = timestampMs }
    }

    suspend fun clearFocusMode() {
        context.dataStore.edit { it[KEY_FOCUS_UNTIL_MS] = 0L }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HAPTICS_ENABLED] = enabled }
    }

    suspend fun recordDismissalAndIncrementBackoff() {
        context.dataStore.edit {
            val count = (it[KEY_CONSECUTIVE_DISMISSALS] ?: 0) + 1
            it[KEY_CONSECUTIVE_DISMISSALS] = count
        }
    }

    suspend fun resetDismissalBackoff() {
        context.dataStore.edit { it[KEY_CONSECUTIVE_DISMISSALS] = 0 }
    }

    suspend fun initDayStartTimeIfNeeded() {
        context.dataStore.edit {
            if (!it.contains(KEY_DAY_START_TIME)) {
                it[KEY_DAY_START_TIME] = System.currentTimeMillis()
            }
        }
    }

    suspend fun clearAllPreferences() {
        context.dataStore.edit { it.clear() }
    }
}

