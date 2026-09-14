package com.hydroping.app.ui

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hydroping.app.ui.components.CustomDrinkDialog
import com.hydroping.app.ui.components.HydraPingReminderDialog
import com.hydroping.app.ui.components.ScreenEdgeGlowOverlay
import com.hydroping.app.ui.screens.HistoryScreen
import com.hydroping.app.ui.screens.HomeScreen
import com.hydroping.app.ui.screens.OnboardingScreen
import com.hydroping.app.ui.screens.SettingsScreen
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class NavTab {
    HOME,
    HISTORY,
    SETTINGS
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onExportCsv: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }
    var isAppLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(60)
        isAppLoaded = true
    }

    val handleExportJson = {
        coroutineScope.launch {
            val json = viewModel.getExportJsonBackup()
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, json)
                type = "application/json"
            }
            val shareIntent = Intent.createChooser(sendIntent, "HydraPing JSON Backup")
            context.startActivity(shareIntent)
        }
    }

    // In-App HydraPing Active Reminder Overlay (used dynamically for reminders & previews)
    if (uiState.showReminderModal) {
        HydraPingReminderDialog(
            character = uiState.character,
            userName = uiState.userName,
            animationType = uiState.reminderAnimation,
            currentConsumedMl = uiState.currentTotalMl,
            targetDailyMl = uiState.targetDailyMl,
            requiredAmountMl = uiState.favoriteAmountMl,
            onDrinkLogged = { amount ->
                viewModel.logDrink(amount)
                viewModel.showReminderModal(false)
            },
            onLogCustomClick = {
                viewModel.showReminderModal(false)
                viewModel.showCustomLogModal(true)
            },
            onSnoozeClick = {
                viewModel.snoozeReminder()
            },
            onSkipClick = {
                viewModel.skipReminder()
            },
            onDismiss = {
                viewModel.showReminderModal(false)
            }
        )
    }

    if (uiState.showCustomLogModal) {
        CustomDrinkDialog(
            initialAmountMl = uiState.favoriteAmountMl,
            onDismiss = { viewModel.showCustomLogModal(false) },
            onConfirm = { amount ->
                viewModel.logDrink(amount)
                viewModel.showCustomLogModal(false)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isAppLoaded,
            enter = fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.96f, animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(200)),
            modifier = Modifier.fillMaxSize()
        ) {
            if (!uiState.isOnboardingCompleted) {
                OnboardingScreen(
                    onStartCompanion = { name, goal, interval, smartPacing, charId, personality, unit, actLevel, wakeH, sleepH, anim ->
                        viewModel.completeOnboarding(
                            userName = name,
                            dailyGoalMl = goal,
                            reminderIntervalMins = interval,
                            smartPacingEnabled = smartPacing,
                            characterId = charId,
                            personality = personality,
                            unit = unit,
                            activityLevel = actLevel,
                            wakeHour = wakeH,
                            sleepHour = sleepH,
                            animation = anim
                        )
                    }
                )
            } else {
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = OceanCard,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == NavTab.HOME,
                                onClick = { selectedTab = NavTab.HOME },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HydroCyan,
                                    selectedTextColor = HydroCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = HydroBlue.copy(alpha = 0.25f)
                                )
                            )

                            NavigationBarItem(
                                selected = selectedTab == NavTab.HISTORY,
                                onClick = { selectedTab = NavTab.HISTORY },
                                icon = { Icon(Icons.Default.History, contentDescription = "History") },
                                label = { Text("History") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HydroCyan,
                                    selectedTextColor = HydroCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = HydroBlue.copy(alpha = 0.25f)
                                )
                            )

                            NavigationBarItem(
                                selected = selectedTab == NavTab.SETTINGS,
                                onClick = { selectedTab = NavTab.SETTINGS },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("Settings") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HydroCyan,
                                    selectedTextColor = HydroCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = HydroBlue.copy(alpha = 0.25f)
                                )
                            )
                        }
                    }
                ) { paddingValues ->
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            if (targetState.ordinal > initialState.ordinal) {
                                (slideInHorizontally(animationSpec = tween(320)) { width -> width / 3 } + fadeIn(animationSpec = tween(320)))
                                    .togetherWith(slideOutHorizontally(animationSpec = tween(320)) { width -> -width / 3 } + fadeOut(animationSpec = tween(280)))
                            } else {
                                (slideInHorizontally(animationSpec = tween(320)) { width -> -width / 3 } + fadeIn(animationSpec = tween(320)))
                                    .togetherWith(slideOutHorizontally(animationSpec = tween(320)) { width -> width / 3 } + fadeOut(animationSpec = tween(280)))
                            }
                        },
                        label = "tab_transition",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) { targetTab ->
                        when (targetTab) {
                            NavTab.HOME -> HomeScreen(
                                uiState = uiState,
                                onDrinkLogged = { viewModel.logDrink(it) },
                                onCharacterTap = { viewModel.startDrinkingMode() },
                                onStartFocusMode = { viewModel.startFocusMode(it) },
                                onCancelFocusMode = { viewModel.cancelFocusMode() },
                                onToggleMinimalMode = { viewModel.setMinimalMode(it) },
                                onDismissCelebration = { viewModel.dismissGoalCelebration() }
                            )
                            NavTab.HISTORY -> HistoryScreen(
                                uiState = uiState,
                                onExportCsv = onExportCsv,
                                onExportJson = { handleExportJson() },
                                onUpdateDrink = { viewModel.updateDrink(it) },
                                onDeleteDrink = { viewModel.deleteDrink(it) },
                                onUndoDelete = { viewModel.undoLastDelete() }
                            )
                            NavTab.SETTINGS -> SettingsScreen(
                                uiState = uiState,
                                onUserNameChanged = { viewModel.setUserName(it) },
                                onDailyGoalChanged = { viewModel.setDailyGoal(it) },
                                onFavoriteAmountChanged = { viewModel.setFavoriteAmount(it) },
                                onReminderIntervalChanged = { viewModel.setReminderInterval(it) },
                                onSmartPacingToggled = { viewModel.setSmartPacingEnabled(it) },
                                onActiveHoursChanged = { start, end -> viewModel.setActiveHours(start, end) },
                                onCharacterSelected = { id, personality ->
                                    viewModel.setCharacterAndPersonality(id, personality)
                                },
                                onReminderAnimationChanged = { viewModel.setReminderAnimation(it) },
                                onUnitChanged = { viewModel.setUnit(it) },
                                onThemeModeChanged = { viewModel.setThemeMode(it) },
                                onMinimalModeChanged = { viewModel.setMinimalMode(it) },
                                onNotificationStyleChanged = { viewModel.setNotificationStyle(it) },
                                onReminderSoundChanged = { viewModel.setReminderSound(it) },
                                onVibrationToggled = { viewModel.setVibrationEnabled(it) },
                                onSnoozeMinsChanged = { viewModel.setSnoozeMins(it) },
                                onGoalStrategyChanged = { viewModel.setGoalStrategy(it) },
                                onActivityLevelChanged = { viewModel.setActivityLevel(it) },
                                onHealthConnectToggled = { viewModel.setHealthConnectEnabled(it) },
                                onWearSyncToggled = { viewModel.setWearSyncEnabled(it) },
                                onScheduleProfileUpdated = { wStart, wEnd, weStart, weEnd, wInt, weInt, work, study, exer, wthr ->
                                    viewModel.updateScheduleProfile(wStart, wEnd, weStart, weEnd, wInt, weInt, work, study, exer, wthr)
                                },
                                onStudyIntervalChanged = { viewModel.setStudyIntervalMins(it) },
                                onStartFocusMode = { viewModel.startFocusMode(it) },
                                onExportCsv = onExportCsv,
                                onExportJson = { handleExportJson() },
                                onRestoreJson = { jsonStr ->
                                    coroutineScope.launch { viewModel.restoreJsonBackup(jsonStr) }
                                },
                                onResetToday = { viewModel.resetTodayProgress() },
                                onResetAllData = { viewModel.resetAllData() }
                            )
                        }
                    }
                }
            }
        }

        // Screen Corner & Edge Glow Lighting Overlay
        if (uiState.showScreenGlow) {
            ScreenEdgeGlowOverlay()
        }
    }
}

