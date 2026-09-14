package com.hydroping.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hydroping.app.domain.ActivityLevel
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.CharacterProfile
import com.hydroping.app.domain.GoalStrategy
import com.hydroping.app.domain.HydrationUnit
import com.hydroping.app.domain.HydrationUnitHelper
import com.hydroping.app.domain.NotificationStyle
import com.hydroping.app.domain.PersonalityType
import com.hydroping.app.domain.ReminderSound
import com.hydroping.app.domain.ThemeMode
import com.hydroping.app.reminder.NotificationHelper
import com.hydroping.app.utils.SoundEffectHelper
import com.hydroping.app.domain.ReminderAnimation
import com.hydroping.app.ui.UiState
import com.hydroping.app.ui.components.AnimationPreviewDialog
import com.hydroping.app.ui.components.CustomFlexibleTimePickerDialog
import com.hydroping.app.ui.components.CustomNumberInputDialog
import com.hydroping.app.ui.components.FocusModeDialog
import com.hydroping.app.ui.components.HydraPingReminderDialog
import com.hydroping.app.ui.components.ResetConfirmDialog
import com.hydroping.app.ui.components.ResetType
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.OceanDeep
import com.hydroping.app.ui.theme.OceanSurface
import com.hydroping.app.ui.theme.SuccessGreen
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary
import com.hydroping.app.ui.theme.UrgentOrange

@Composable
fun SettingsScreen(
    uiState: UiState,
    onUserNameChanged: (String) -> Unit,
    onDailyGoalChanged: (Int) -> Unit,
    onFavoriteAmountChanged: (Int) -> Unit,
    onReminderIntervalChanged: (Int) -> Unit,
    onSmartPacingToggled: (Boolean) -> Unit,
    onActiveHoursChanged: (Int, Int) -> Unit,
    onCharacterSelected: (String, PersonalityType) -> Unit,
    onReminderAnimationChanged: (ReminderAnimation) -> Unit,
    onUnitChanged: (HydrationUnit) -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit = {},
    onMinimalModeChanged: (Boolean) -> Unit,
    onNotificationStyleChanged: (NotificationStyle) -> Unit,
    onReminderSoundChanged: (ReminderSound) -> Unit,
    onVibrationToggled: (Boolean) -> Unit,
    onSnoozeMinsChanged: (Int) -> Unit,
    onGoalStrategyChanged: (GoalStrategy) -> Unit,
    onActivityLevelChanged: (ActivityLevel) -> Unit,
    onHealthConnectToggled: (Boolean) -> Unit,
    onWearSyncToggled: (Boolean) -> Unit,
    onScheduleProfileUpdated: (Int, Int, Int, Int, Int, Int, Boolean, Boolean, Boolean, Boolean) -> Unit,
    onStudyIntervalChanged: (Int) -> Unit = {},
    onStartFocusMode: (Int) -> Unit,
    onExportCsv: () -> Unit,
    onExportJson: () -> Unit,
    onRestoreJson: (String) -> Unit,
    onResetToday: () -> Unit,
    onResetAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prof = uiState.scheduleProfile

    var goalSliderValue by remember(uiState.targetDailyMl) {
        mutableFloatStateOf(uiState.targetDailyMl.toFloat())
    }
    var previewCharacter by remember { mutableStateOf<CharacterProfile?>(null) }
    var previewAnimation by remember { mutableStateOf<ReminderAnimation?>(null) }
    var showFocusDialog by remember { mutableStateOf(false) }
    var showResetTodayDialog by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showCustomGoalDialog by remember { mutableStateOf(false) }
    var showCustomIntervalDialog by remember { mutableStateOf(false) }
    var showCustomSnoozeDialog by remember { mutableStateOf(false) }
    var showWeekdayWakeDialog by remember { mutableStateOf(false) }
    var showWeekdaySleepDialog by remember { mutableStateOf(false) }
    var showWeekendWakeDialog by remember { mutableStateOf(false) }
    var showWeekendSleepDialog by remember { mutableStateOf(false) }
    var restoreJsonText by remember { mutableStateOf("") }
    var userNameInput by remember { mutableStateOf(uiState.userName) }
    var hasInitializedName by remember { mutableStateOf(uiState.userName.isNotBlank()) }

    LaunchedEffect(uiState.userName) {
        if (!hasInitializedName && uiState.userName.isNotBlank()) {
            userNameInput = uiState.userName
            hasInitializedName = true
        }
    }

    // True accordion state: strictly only ONE section can be expanded at any time (all closed by default)
    var expandedSection by remember { mutableStateOf<SettingsSection?>(null) }

    // In-app Animation preview dialog for character
    if (previewCharacter != null) {
        AnimationPreviewDialog(
            character = previewCharacter!!,
            userName = uiState.userName,
            onDismiss = { previewCharacter = null }
        )
    }

    // Dynamic HydraPing Reminder & Animation Preview Dialog
    if (previewAnimation != null) {
        HydraPingReminderDialog(
            character = uiState.character,
            userName = uiState.userName,
            animationType = previewAnimation!!,
            currentConsumedMl = uiState.currentTotalMl,
            targetDailyMl = uiState.targetDailyMl,
            requiredAmountMl = uiState.favoriteAmountMl,
            onDrinkLogged = {
                Toast.makeText(context, "HydraPing Preview: Drank $it ml! 💧", Toast.LENGTH_SHORT).show()
                previewAnimation = null
            },
            onLogCustomClick = {
                Toast.makeText(context, "HydraPing Preview: Log Water opened! 💧", Toast.LENGTH_SHORT).show()
                previewAnimation = null
            },
            onSnoozeClick = {
                Toast.makeText(context, "HydraPing Preview: Snoozed for 30m! 💧", Toast.LENGTH_SHORT).show()
                previewAnimation = null
            },
            onSkipClick = {
                Toast.makeText(context, "HydraPing Preview: Skipped reminder! 💧", Toast.LENGTH_SHORT).show()
                previewAnimation = null
            },
            onDismiss = { previewAnimation = null }
        )
    }

    if (showFocusDialog) {
        FocusModeDialog(
            onDismiss = { showFocusDialog = false },
            onStartFocus = { mins ->
                onStartFocusMode(mins)
                showFocusDialog = false
            }
        )
    }

    if (showResetTodayDialog) {
        ResetConfirmDialog(
            type = ResetType.TODAY_ONLY,
            onDismiss = { showResetTodayDialog = false },
            onConfirm = onResetToday
        )
    }

    if (showResetAllDialog) {
        ResetConfirmDialog(
            type = ResetType.ALL_DATA,
            onDismiss = { showResetAllDialog = false },
            onConfirm = onResetAllData
        )
    }

    if (showRestoreDialog) {
        Dialog(onDismissRequest = { showRestoreDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = OceanCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HydroCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Restore JSON Backup", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = "Paste your exported HydraPing JSON backup below:",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = restoreJsonText,
                        onValueChange = { restoreJsonText = it },
                        placeholder = { Text("{\"app\": \"HydraPing\", ...}", color = TextMuted, fontSize = 11.sp) },
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HydroCyan,
                            unfocusedBorderColor = Color(0xFF2E3E6E),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showRestoreDialog = false }, shape = RoundedCornerShape(10.dp)) {
                            Text("Cancel", color = TextMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (restoreJsonText.isNotBlank()) {
                                    onRestoreJson(restoreJsonText)
                                    showRestoreDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HydroCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Restore Now", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showCustomGoalDialog) {
        CustomNumberInputDialog(
            title = "Custom Daily Target",
            subtitle = "Set exact personalized hydration target in ml",
            initialValue = uiState.targetDailyMl,
            unitSuffix = "ml",
            minValue = 500,
            maxValue = 8000,
            onDismiss = { showCustomGoalDialog = false },
            onConfirm = { customMl ->
                goalSliderValue = customMl.toFloat()
                onDailyGoalChanged(customMl)
                showCustomGoalDialog = false
            }
        )
    }

    if (showCustomIntervalDialog) {
        CustomNumberInputDialog(
            title = "Custom Reminder Interval",
            subtitle = "Set exact cadence in minutes between hydration reminders",
            initialValue = uiState.baseReminderIntervalMins,
            unitSuffix = "min",
            minValue = 15,
            maxValue = 240,
            onDismiss = { showCustomIntervalDialog = false },
            onConfirm = { customMins ->
                onReminderIntervalChanged(customMins)
                showCustomIntervalDialog = false
            }
        )
    }

    if (showCustomSnoozeDialog) {
        CustomNumberInputDialog(
            title = "Custom Snooze Duration",
            subtitle = "Set exact snooze duration in minutes",
            initialValue = uiState.snoozeMins,
            unitSuffix = "min",
            minValue = 5,
            maxValue = 120,
            onDismiss = { showCustomSnoozeDialog = false },
            onConfirm = { customMins ->
                onSnoozeMinsChanged(customMins)
                showCustomSnoozeDialog = false
            }
        )
    }

    if (showWeekdayWakeDialog) {
        CustomFlexibleTimePickerDialog(
            title = "Weekday Wake-Up Time",
            subtitle = "Set exact time to start weekday reminders",
            initialHour = prof.weekdayStartHour,
            initialMinute = prof.weekdayStartMinute,
            onDismiss = { showWeekdayWakeDialog = false },
            onConfirm = { h, m ->
                onScheduleProfileUpdated(
                    h, prof.weekdayEndHour, prof.weekendStartHour, prof.weekendEndHour,
                    prof.weekdayIntervalMins, prof.weekendIntervalMins,
                    prof.workModeEnabled, prof.studyModeEnabled,
                    prof.exerciseModeEnabled, prof.weatherBoosterEnabled
                )
                showWeekdayWakeDialog = false
            }
        )
    }

    if (showWeekdaySleepDialog) {
        CustomFlexibleTimePickerDialog(
            title = "Weekday Sleep Time",
            subtitle = "Set exact time to end weekday reminders",
            initialHour = prof.weekdayEndHour,
            initialMinute = prof.weekdayEndMinute,
            onDismiss = { showWeekdaySleepDialog = false },
            onConfirm = { h, m ->
                onScheduleProfileUpdated(
                    prof.weekdayStartHour, h, prof.weekendStartHour, prof.weekendEndHour,
                    prof.weekdayIntervalMins, prof.weekendIntervalMins,
                    prof.workModeEnabled, prof.studyModeEnabled,
                    prof.exerciseModeEnabled, prof.weatherBoosterEnabled
                )
                showWeekdaySleepDialog = false
            }
        )
    }

    if (showWeekendWakeDialog) {
        CustomFlexibleTimePickerDialog(
            title = "Weekend Wake-Up Time",
            subtitle = "Set exact time to start weekend reminders",
            initialHour = prof.weekendStartHour,
            initialMinute = prof.weekendStartMinute,
            onDismiss = { showWeekendWakeDialog = false },
            onConfirm = { h, m ->
                onScheduleProfileUpdated(
                    prof.weekdayStartHour, prof.weekdayEndHour, h, prof.weekendEndHour,
                    prof.weekdayIntervalMins, prof.weekendIntervalMins,
                    prof.workModeEnabled, prof.studyModeEnabled,
                    prof.exerciseModeEnabled, prof.weatherBoosterEnabled
                )
                showWeekendWakeDialog = false
            }
        )
    }

    if (showWeekendSleepDialog) {
        CustomFlexibleTimePickerDialog(
            title = "Weekend Sleep Time",
            subtitle = "Set exact time to end weekend reminders",
            initialHour = prof.weekendEndHour,
            initialMinute = prof.weekendEndMinute,
            onDismiss = { showWeekendSleepDialog = false },
            onConfirm = { h, m ->
                onScheduleProfileUpdated(
                    prof.weekdayStartHour, prof.weekdayEndHour, prof.weekendStartHour, h,
                    prof.weekdayIntervalMins, prof.weekendIntervalMins,
                    prof.workModeEnabled, prof.studyModeEnabled,
                    prof.exerciseModeEnabled, prof.weatherBoosterEnabled
                )
                showWeekendSleepDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(OceanDeep, OceanSurface, OceanDeep)))
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Configure targets, reminders, sounds & data",
            fontSize = 13.sp,
            color = TextSecondary
        )

        val notifsEnabled = androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (!notifsEnabled) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF3B1E2B))
                    .border(1.dp, UrgentOrange, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚠️ Notifications Disabled",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = UrgentOrange
                        )
                        Text(
                            text = "HydraPing reminders cannot appear on lock screen or status bar.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UrgentOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Enable", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // GROUP 1: PROFILE & HYDRATION TARGET
        // ==========================================
        CompactGroupCard(
            title = "Profile & Hydration Target",
            icon = Icons.Default.Person,
            summary = "${uiState.userName.ifBlank { "Manoj" }} • ${uiState.targetDailyMl} ml",
            isExpanded = expandedSection == SettingsSection.PROFILE,
            onToggle = {
                expandedSection = if (expandedSection == SettingsSection.PROFILE) null else SettingsSection.PROFILE
            }
        ) {
            // User Name
            Text(text = "Your Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = userNameInput,
                onValueChange = {
                    userNameInput = it
                    onUserNameChanged(it)
                },
                placeholder = { Text("Manoj", color = TextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HydroCyan,
                    unfocusedBorderColor = Color(0xFF2E3E6E),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = HydroCyan
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Daily Target Slider & Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Daily Target", fontSize = 13.sp, color = TextSecondary)
                Text(
                    text = "${goalSliderValue.toInt()} ml",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HydroCyan
                )
            }

            Slider(
                value = goalSliderValue,
                onValueChange = { goalSliderValue = it },
                onValueChangeFinished = { onDailyGoalChanged(goalSliderValue.toInt()) },
                valueRange = 1000f..4500f,
                steps = 34,
                colors = SliderDefaults.colors(
                    thumbColor = HydroCyan,
                    activeTrackColor = HydroBlue,
                    inactiveTrackColor = Color(0xFF0F172A)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(1500, 2000, 2500, 3000, 3500).forEach { preset ->
                    val isCurrent = goalSliderValue.toInt() == preset
                    Text(
                        text = "${preset}ml",
                        fontSize = 11.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) HydroCyan else TextMuted,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrent) HydroBlue.copy(alpha = 0.3f) else Color.Transparent)
                            .clickable {
                                goalSliderValue = preset.toFloat()
                                onDailyGoalChanged(preset)
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "Custom...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroCyan,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HydroCyan.copy(alpha = 0.15f))
                        .clickable { showCustomGoalDialog = true }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Level (Clean Simple Terms)
            Text(text = "Daily Activity Level", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ActivityLevel.values().forEach { level ->
                    val isSel = uiState.activityLevel == level
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) HydroBlue else Color(0xFF0F172A))
                            .border(1.dp, if (isSel) HydroCyan else Color(0xFF2E3E6E), RoundedCornerShape(10.dp))
                            .clickable { onActivityLevelChanged(level) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = level.simpleTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else TextPrimary
                            )
                            Text(
                                text = if (level.extraMl > 0) "+${level.extraMl}ml" else "Base",
                                fontSize = 9.sp,
                                color = if (isSel) HydroCyan else TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // GROUP 2: REMINDERS & SMART SCHEDULE
        // ==========================================
        CompactGroupCard(
            title = "Reminders & Schedule",
            icon = Icons.Default.Alarm,
            summary = "Every ${uiState.baseReminderIntervalMins}m • Wake ${prof.weekdayStartHour}:${String.format("%02d", prof.weekdayStartMinute)} - ${prof.weekdayEndHour}:${String.format("%02d", prof.weekdayEndMinute)}",
            isExpanded = expandedSection == SettingsSection.REMINDERS,
            onToggle = {
                expandedSection = if (expandedSection == SettingsSection.REMINDERS) null else SettingsSection.REMINDERS
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Reminder Interval", fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = "Custom: ${uiState.baseReminderIntervalMins}m",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroCyan,
                    modifier = Modifier.clickable { showCustomIntervalDialog = true }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(20, 30, 45, 60, 90, 120).forEach { mins ->
                    val isSel = uiState.baseReminderIntervalMins == mins
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) HydroBlue else Color(0xFF0F172A))
                            .border(1.dp, if (isSel) HydroCyan else Color(0xFF2E3E6E), RoundedCornerShape(8.dp))
                            .clickable { onReminderIntervalChanged(mins) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${mins}m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSel) Color.White else TextMuted
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HydroCyan.copy(alpha = 0.15f))
                        .border(1.dp, HydroCyan, RoundedCornerShape(8.dp))
                        .clickable { showCustomIntervalDialog = true }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Edit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HydroCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Smart Pacing toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Adaptive Catch-Up", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Adjusts reminder timing if behind target pace", fontSize = 11.sp, color = TextSecondary)
                }
                Switch(
                    checked = uiState.smartPacingEnabled,
                    onCheckedChange = onSmartPacingToggled,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HydroBlue)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Snooze picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Snooze Duration", fontSize = 13.sp, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf(10, 15, 20, 30).forEach { sMins ->
                        val isSel = uiState.snoozeMins == sMins
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) HydroBlue else Color(0xFF0F172A))
                                .border(1.dp, if (isSel) HydroCyan else Color(0xFF2E3E6E), RoundedCornerShape(8.dp))
                                .clickable { onSnoozeMinsChanged(sMins) }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(text = "${sMins}m", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextMuted)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HydroCyan.copy(alpha = 0.15f))
                            .border(1.dp, HydroCyan, RoundedCornerShape(8.dp))
                            .clickable { showCustomSnoozeDialog = true }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(text = "...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HydroCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Flexible Weekday vs Weekend schedule differentiation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Flexible Schedule Times", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                Text(text = "Tap cards to edit hours:minutes", fontSize = 10.sp, color = HydroCyan)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Weekday card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(10.dp))
                        .clickable { showWeekdayWakeDialog = true }
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Weekdays", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HydroCyan)
                            Text("Edit ✏️", fontSize = 9.sp, color = HydroCyan)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format("%02d:%02d - %02d:%02d", prof.weekdayStartHour, prof.weekdayStartMinute, prof.weekdayEndHour, prof.weekdayEndMinute),
                            fontSize = 10.sp,
                            color = TextPrimary
                        )
                        Text("Cadence: ${prof.weekdayIntervalMins}m", fontSize = 10.sp, color = TextMuted)
                    }
                }

                // Weekend card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(10.dp))
                        .clickable { showWeekendWakeDialog = true }
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Weekends", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldCelebration)
                            Text("Edit ✏️", fontSize = 9.sp, color = GoldCelebration)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format("%02d:%02d - %02d:%02d", prof.weekendStartHour, prof.weekendStartMinute, prof.weekendEndHour, prof.weekendEndMinute),
                            fontSize = 10.sp,
                            color = TextPrimary
                        )
                        Text("Cadence: ${prof.weekendIntervalMins}m", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // GROUP 3: NOTIFICATIONS & CHARACTERS
        // ==========================================
        CompactGroupCard(
            title = "Notifications & Characters",
            icon = Icons.Default.Notifications,
            summary = "${uiState.character.name} • ${uiState.reminderSound.title.split(" ").first()}",
            isExpanded = expandedSection == SettingsSection.NOTIFICATIONS,
            onToggle = {
                expandedSection = if (expandedSection == SettingsSection.NOTIFICATIONS) null else SettingsSection.NOTIFICATIONS
            }
        ) {
            // Character & Realtime Animation Preview
            Text(text = "Characters & Reminder Style", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CharacterCatalog.allCharacters.forEach { charProfile ->
                    val isSelected = uiState.character.id == charProfile.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF162544) else Color(0xFF0F172A))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) HydroCyan else Color(0xFF2E3E6E),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onCharacterSelected(charProfile.id, charProfile.defaultPersonality) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = charProfile.previewRes),
                                contentDescription = charProfile.name,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = charProfile.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Preview Button: Selects companion, shows in-app animation preview AND posts a realtime notification!
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HydroCyan.copy(alpha = 0.18f))
                                    .border(1.dp, HydroCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        onCharacterSelected(charProfile.id, charProfile.defaultPersonality)
                                        previewAnimation = uiState.reminderAnimation
                                        NotificationHelper.showPreviewNotification(
                                            context = context,
                                            animationName = "${charProfile.name} • ${uiState.reminderAnimation.title}",
                                            userName = uiState.userName,
                                            amountMl = uiState.favoriteAmountMl,
                                            consumedMl = uiState.currentTotalMl,
                                            targetMl = uiState.targetDailyMl,
                                            characterId = charProfile.id
                                        )
                                        Toast.makeText(context, "${charProfile.name} selected & notification sent! 💧", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "Preview",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HydroCyan,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dashboard Animation Style Section (3-card visual row)
            Text(text = "Dashboard Character Animation Style", fontSize = 12.sp, color = TextMuted)
            Text(
                text = "Controls the live background water effect behind your character on the main Dashboard.",
                fontSize = 10.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReminderAnimation.values().forEach { anim ->
                    val isSelected = uiState.reminderAnimation == anim
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF162544) else Color(0xFF0F172A))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) HydroCyan else Color(0xFF2E3E6E),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onReminderAnimationChanged(anim) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) HydroCyan.copy(alpha = 0.2f) else OceanCard)
                                    .border(1.5.dp, if (isSelected) HydroCyan else Color(0xFF2E3E6E), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = anim.iconEmoji,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = anim.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) HydroCyan else TextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Preview Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HydroCyan.copy(alpha = 0.18f))
                                    .border(1.dp, HydroCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        onReminderAnimationChanged(anim)
                                        previewAnimation = anim
                                        NotificationHelper.showPreviewNotification(
                                            context = context,
                                            animationName = "${uiState.character.name} • ${anim.title}",
                                            userName = uiState.userName,
                                            amountMl = uiState.favoriteAmountMl,
                                            consumedMl = uiState.currentTotalMl,
                                            targetMl = uiState.targetDailyMl,
                                            characterId = uiState.character.id
                                        )
                                        Toast.makeText(context, "${anim.title} preview opened! 💧", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "Preview",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HydroCyan,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notification Tone / Style (Visual Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Notification Message Tone", fontSize = 12.sp, color = TextMuted)
                Text(text = "Tap cards to select & test", fontSize = 10.sp, color = HydroCyan)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NotificationStyle.values().forEach { style ->
                    val isSel = uiState.notificationStyle == style
                    Box(
                        modifier = Modifier
                            .width(135.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSel) Color(0xFF162544) else Color(0xFF0F172A))
                            .border(
                                width = if (isSel) 2.dp else 1.dp,
                                color = if (isSel) HydroCyan else Color(0xFF2E3E6E),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onNotificationStyleChanged(style) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) HydroCyan.copy(alpha = 0.2f) else OceanCard)
                                    .border(1.5.dp, if (isSel) HydroCyan else Color(0xFF2E3E6E), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = style.iconEmoji,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = style.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) HydroCyan else TextPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "\"${style.sampleQuote}\"",
                                fontSize = 9.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                lineHeight = 11.sp,
                                modifier = Modifier.height(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Preview / Test Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HydroCyan.copy(alpha = 0.18f))
                                    .border(1.dp, HydroCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        onNotificationStyleChanged(style)
                                        NotificationHelper.showHydrationReminder(
                                            context = context,
                                            characterName = uiState.character.name,
                                            message = style.sampleQuote.ifBlank { "Time to hydrate with ${style.title}!" },
                                            amountMl = uiState.favoriteAmountMl,
                                            userName = uiState.userName,
                                            consumedMl = uiState.currentTotalMl,
                                            targetMl = uiState.targetDailyMl,
                                            characterId = uiState.character.id
                                        )
                                        Toast.makeText(context, "${style.title} tone preview sent! 💧", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "Preview",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HydroCyan,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reminder Sound (Real-Time Playback on Click)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Reminder Sound", fontSize = 12.sp, color = TextMuted)
                Text(text = "Tap to listen in real-time 🔊", fontSize = 10.sp, color = HydroCyan)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReminderSound.values().forEach { snd ->
                    val isSel = uiState.reminderSound == snd
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) Color(0xFF162544) else Color(0xFF0F172A))
                            .border(
                                width = if (isSel) 1.5.dp else 1.dp,
                                color = if (isSel) HydroCyan else Color(0xFF2E3E6E),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onReminderSoundChanged(snd)
                                SoundEffectHelper.playSound(context, snd)
                                val toastMsg = if (snd == ReminderSound.SILENT) {
                                    "${snd.iconEmoji} ${snd.title} selected (Haptic Vibration)"
                                } else {
                                    "${snd.iconEmoji} Playing ${snd.title} in real-time 🔊"
                                }
                                Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = snd.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = snd.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) HydroCyan else TextPrimary
                                )
                                if (snd.description.isNotBlank()) {
                                    Text(
                                        text = snd.description,
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vibration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Vibration", fontSize = 12.sp, color = TextSecondary)
                Switch(
                    checked = uiState.vibrationEnabled,
                    onCheckedChange = onVibrationToggled,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HydroBlue)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // GROUP 4: SMART MODES & WELLNESS
        // ==========================================
        val activeModesCount = listOf(prof.workModeEnabled, prof.studyModeEnabled, prof.exerciseModeEnabled, prof.weatherBoosterEnabled).count { it }
        CompactGroupCard(
            title = "Smart Modes & Wellness",
            icon = Icons.Default.Work,
            summary = if (activeModesCount > 0) "$activeModesCount active modes" else "Standard mode",
            isExpanded = expandedSection == SettingsSection.MODES,
            onToggle = {
                expandedSection = if (expandedSection == SettingsSection.MODES) null else SettingsSection.MODES
            }
        ) {
            ToggleSettingRow(
                title = "Work Mode",
                subtitle = "Quieter, non-intrusive reminders",
                icon = Icons.Default.Work,
                checked = prof.workModeEnabled,
                onCheckedChange = {
                    onScheduleProfileUpdated(
                        prof.weekdayStartHour, prof.weekdayEndHour,
                        prof.weekendStartHour, prof.weekendEndHour,
                        prof.weekdayIntervalMins, prof.weekendIntervalMins,
                        it, prof.studyModeEnabled, prof.exerciseModeEnabled, prof.weatherBoosterEnabled
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            ToggleSettingRow(
                title = "Study Mode",
                subtitle = "Gentle pings for deep focus",
                icon = Icons.Default.School,
                checked = prof.studyModeEnabled,
                onCheckedChange = {
                    onScheduleProfileUpdated(
                        prof.weekdayStartHour, prof.weekdayEndHour,
                        prof.weekendStartHour, prof.weekendEndHour,
                        prof.weekdayIntervalMins, prof.weekendIntervalMins,
                        prof.workModeEnabled, it, prof.exerciseModeEnabled, prof.weatherBoosterEnabled
                    )
                }
            )

            if (prof.studyModeEnabled) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Study Focus Interval", fontSize = 11.sp, color = HydroCyan)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(25, 30, 45, 60, 90).forEach { sMins ->
                            val isSel = prof.studyIntervalMins == sMins
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) HydroBlue else Color(0xFF0F172A))
                                    .border(1.dp, if (isSel) HydroCyan else Color(0xFF2E3E6E), RoundedCornerShape(6.dp))
                                    .clickable { onStudyIntervalChanged(sMins) }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${sMins}m",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else TextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            ToggleSettingRow(
                title = "Exercise Booster",
                subtitle = "+350 ml and higher reminder cadence",
                icon = Icons.Default.FitnessCenter,
                checked = prof.exerciseModeEnabled,
                onCheckedChange = {
                    onScheduleProfileUpdated(
                        prof.weekdayStartHour, prof.weekdayEndHour,
                        prof.weekendStartHour, prof.weekendEndHour,
                        prof.weekdayIntervalMins, prof.weekendIntervalMins,
                        prof.workModeEnabled, prof.studyModeEnabled, it, prof.weatherBoosterEnabled
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            ToggleSettingRow(
                title = "Weather Booster",
                subtitle = "Extra recommendations on hot days",
                icon = Icons.Default.WbSunny,
                checked = prof.weatherBoosterEnabled,
                onCheckedChange = {
                    onScheduleProfileUpdated(
                        prof.weekdayStartHour, prof.weekdayEndHour,
                        prof.weekendStartHour, prof.weekendEndHour,
                        prof.weekdayIntervalMins, prof.weekendIntervalMins,
                        prof.workModeEnabled, prof.studyModeEnabled, prof.exerciseModeEnabled, it
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            ToggleSettingRow(
                title = "Health Connect Sync",
                subtitle = "Google Fit & Android Health sync",
                icon = Icons.Default.HealthAndSafety,
                checked = uiState.healthConnectEnabled,
                onCheckedChange = onHealthConnectToggled
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // GROUP 5: BACKUP & PREFERENCES
        // ==========================================
        CompactGroupCard(
            title = "Data Backup & Preferences",
            icon = Icons.Default.Security,
            summary = "Backup Ready • Room Local Storage",
            isExpanded = expandedSection == SettingsSection.DATA,
            onToggle = {
                expandedSection = if (expandedSection == SettingsSection.DATA) null else SettingsSection.DATA
            }
        ) {

            // Minimal mode toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Minimal Dashboard Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(text = "Clutter-free simple layout", fontSize = 10.sp, color = TextMuted)
                }
                Switch(
                    checked = uiState.minimalMode,
                    onCheckedChange = onMinimalModeChanged,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HydroBlue)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Export & Restore Actions
            Text(text = "Data Management", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onExportCsv,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(13.dp))
                    Text(text = "CSV", fontSize = 11.sp, color = TextPrimary, modifier = Modifier.padding(start = 2.dp))
                }

                Button(
                    onClick = onExportJson,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = GoldCelebration, modifier = Modifier.size(13.dp))
                    Text(text = "Backup", fontSize = 11.sp, color = TextPrimary, modifier = Modifier.padding(start = 2.dp))
                }

                Button(
                    onClick = { showRestoreDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(13.dp))
                    Text(text = "Restore", fontSize = 11.sp, color = TextPrimary, modifier = Modifier.padding(start = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reset & Erase buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showResetTodayDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(13.dp))
                    Text(text = "Reset Today", fontSize = 11.sp, color = HydroCyan, modifier = Modifier.padding(start = 2.dp))
                }

                OutlinedButton(
                    onClick = { showResetAllDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentOrange),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(UrgentOrange.copy(alpha = 0.5f))),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = UrgentOrange, modifier = Modifier.size(13.dp))
                    Text(text = "Erase All", fontSize = 11.sp, color = UrgentOrange, modifier = Modifier.padding(start = 2.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun CompactGroupCard(
    title: String,
    icon: ImageVector,
    summary: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OceanCard)
            .border(1.dp, if (isExpanded) HydroCyan.copy(alpha = 0.4f) else Color(0xFF2E3E6E), RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header clickable row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(HydroBlue.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = summary, fontSize = 11.sp, color = TextMuted)
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expanded content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp, top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF1E293B))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    content()
                }
            }
        }
    }
}

@Composable
private fun ToggleSettingRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = if (checked) HydroCyan else TextMuted, modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HydroBlue)
        )
    }
}

enum class SettingsSection {
    PROFILE,
    REMINDERS,
    NOTIFICATIONS,
    MODES,
    DATA
}
