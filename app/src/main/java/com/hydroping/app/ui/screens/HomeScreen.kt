package com.hydroping.app.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.domain.HydrationUnitHelper
import com.hydroping.app.ui.UiState
import com.hydroping.app.ui.components.CustomDrinkDialog
import com.hydroping.app.ui.components.DashboardTrendsCard
import com.hydroping.app.ui.components.DroppyCharacterView
import com.hydroping.app.ui.components.FocusModeDialog
import com.hydroping.app.ui.components.GoalCelebrationDialog
import com.hydroping.app.ui.components.MinimalDashboardView
import com.hydroping.app.ui.components.PersonalRecordsCard
import com.hydroping.app.ui.components.QuickDrinkBar
import com.hydroping.app.ui.components.RelationshipCard
import com.hydroping.app.ui.components.WaterWaveProgress
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
fun HomeScreen(
    uiState: UiState,
    onDrinkLogged: (Int) -> Unit,
    onCharacterTap: () -> Unit,
    onStartFocusMode: (Int) -> Unit,
    onCancelFocusMode: () -> Unit,
    onToggleMinimalMode: (Boolean) -> Unit,
    onDismissCelebration: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var showFocusDialog by remember { mutableStateOf(false) }

    if (showCustomDialog) {
        CustomDrinkDialog(
            initialAmountMl = uiState.favoriteAmountMl,
            onDismiss = { showCustomDialog = false },
            onConfirm = { amount ->
                onDrinkLogged(amount)
                showCustomDialog = false
            }
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

    if (uiState.showGoalCelebration) {
        GoalCelebrationDialog(
            userName = uiState.userName,
            streakDays = uiState.streakDays,
            characterId = uiState.character.id,
            onDismiss = onDismissCelebration
        )
    }

    val isFocusActive = uiState.focusUntilMs > System.currentTimeMillis()
    val focusRemainingMins = if (isFocusActive) {
        ((uiState.focusUntilMs - System.currentTimeMillis()) / (60 * 1000L)).toInt().coerceAtLeast(1)
    } else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(OceanDeep, OceanSurface, OceanDeep)
                )
            )
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top App Bar / Personalized Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, ${uiState.userName.ifBlank { "Manoj" }}!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "HydraPing • Smart Hydration",
                    fontSize = 12.sp,
                    color = HydroCyan
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Minimal Mode Quick Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (uiState.minimalMode) HydroBlue.copy(alpha = 0.3f) else OceanCard)
                        .border(1.dp, if (uiState.minimalMode) HydroCyan else Color(0xFF2E3E6E), RoundedCornerShape(12.dp))
                        .clickable { onToggleMinimalMode(!uiState.minimalMode) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Minimal Mode",
                        tint = if (uiState.minimalMode) HydroCyan else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                // Focus Mode Quick Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isFocusActive) GoldCelebration.copy(alpha = 0.2f) else OceanCard)
                        .border(1.dp, if (isFocusActive) GoldCelebration else Color(0xFF2E3E6E), RoundedCornerShape(12.dp))
                        .clickable {
                            if (isFocusActive) onCancelFocusMode() else showFocusDialog = true
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isFocusActive) Icons.Default.Close else Icons.Default.HourglassTop,
                            contentDescription = "Focus Mode",
                            tint = if (isFocusActive) GoldCelebration else HydroCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isFocusActive) "${focusRemainingMins}m Focus" else "Focus Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isFocusActive) GoldCelebration else TextPrimary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dynamic Weather & Exercise Smart Suggestions Banners
        if (uiState.scheduleProfile.weatherBoosterEnabled) {
            val weather = uiState.weatherData
            val weatherTitle = if (weather != null) {
                "Live Weather Booster: ${weather.iconEmoji} ${weather.conditionTitle} (${weather.temperatureC}°C) • +${weather.extraHydrationMl} ml boost applied."
            } else {
                "Live Weather Booster: ☀️ 29.0°C • +300 ml climate boost active."
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(GoldCelebration.copy(alpha = 0.12f))
                    .border(1.dp, GoldCelebration.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = GoldCelebration, modifier = Modifier.size(16.dp))
                    Text(
                        text = weatherTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = GoldCelebration,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.scheduleProfile.exerciseModeEnabled) {
            val boost = if (uiState.activityLevel.extraMl > 0) "+${uiState.activityLevel.extraMl} ml" else "+350 ml"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HydroCyan.copy(alpha = 0.12f))
                    .border(1.dp, HydroCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Exercise Cadence Active: $boost activity boost applied to pacing.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = HydroCyan,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.minimalMode) {
            MinimalDashboardView(
                uiState = uiState,
                onDrinkLogged = onDrinkLogged,
                onCustomClick = { showCustomDialog = true },
                onToggleMinimalMode = { onToggleMinimalMode(false) }
            )
        } else {
            // Dynamic Reminder Cadence Status Pill
            if (isFocusActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GoldCelebration.copy(alpha = 0.12f))
                        .border(1.dp, GoldCelebration.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = "Focus Active",
                                tint = GoldCelebration,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Focus Mode Active: Reminders muted (${focusRemainingMins}m left)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldCelebration,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            } else if (uiState.currentTotalMl >= uiState.targetDailyMl && uiState.targetDailyMl > 0) {
                // Invariant: Goal completion stops daytime reminders
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .border(1.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Goal Met",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "🎉 Daily Goal Achieved! Next reminder tomorrow at wake-up.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            } else {
                uiState.pacingInfo?.let { pacing ->
                    val isBehind = pacing.isBehindSchedule
                    val isAhead = pacing.isAheadOfSchedule
                    val pillBorderColor = when {
                        isBehind -> UrgentOrange.copy(alpha = 0.6f)
                        isAhead -> GoldCelebration.copy(alpha = 0.5f)
                        else -> HydroCyan.copy(alpha = 0.4f)
                    }
                    val pillBgColor = when {
                        isBehind -> UrgentOrange.copy(alpha = 0.12f)
                        isAhead -> GoldCelebration.copy(alpha = 0.12f)
                        else -> HydroBlue.copy(alpha = 0.15f)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(pillBgColor)
                            .border(1.dp, pillBorderColor, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isBehind) Icons.Default.Bolt else Icons.Default.Schedule,
                                    contentDescription = "Reminder Pacing",
                                    tint = if (isBehind) UrgentOrange else HydroCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isBehind) "Gradual Catch-up: Next reminder in ${pacing.calculatedDelayMinutes}m"
                                    else "Adaptive Reminder: Next in ${pacing.calculatedDelayMinutes}m",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            Text(
                                text = "${HydrationUnitHelper.format(pacing.remainingMl, uiState.unit)} left",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBehind) UrgentOrange else HydroCyan
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Companion Mascot View with integrated water filling wave, hydration progress readout & dialogue
            DroppyCharacterView(
                characterId = uiState.character.id,
                mood = uiState.mood,
                dialogueText = uiState.dialogue,
                currentMl = uiState.currentTotalMl,
                targetMl = uiState.targetDailyMl,
                unit = uiState.unit,
                onCharacterTap = onCharacterTap,
                animationType = uiState.reminderAnimation,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 1-Tap Quick Drink Bar
            QuickDrinkBar(
                favoriteAmount = uiState.favoriteAmountMl,
                unit = uiState.unit,
                onDrinkLogged = { onDrinkLogged(it) },
                onCustomClick = { showCustomDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Droppy's Day Relationship Card
            RelationshipCard(
                characterName = uiState.character.name,
                dayStartTimeMs = uiState.dayStartTimeMs,
                drinksCount = uiState.todayDrinks.size,
                streakDays = uiState.streakDays
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dashboard Performance Trends Card
            DashboardTrendsCard(
                todayDrinks = uiState.todayDrinks,
                currentTotalMl = uiState.currentTotalMl,
                targetDailyMl = uiState.targetDailyMl,
                streakDays = uiState.streakDays
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Personal Records & Lifetime Milestones Card
            PersonalRecordsCard(
                bestDayMl = uiState.bestDayMl,
                longestStreak = uiState.longestStreak,
                lifetimeTotalMl = uiState.lifetimeTotalMl,
                totalDrinksLogged = uiState.totalDrinksLogged
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

