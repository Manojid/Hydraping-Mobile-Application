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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.data.DrinkLog
import com.hydroping.app.domain.HydrationUnitHelper
import com.hydroping.app.ui.UiState
import com.hydroping.app.ui.components.AnalyticsDashboard
import com.hydroping.app.ui.components.EditDrinkDialog
import com.hydroping.app.ui.components.HydrationCalendarView
import com.hydroping.app.ui.components.MilestonesCard
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.OceanDeep
import com.hydroping.app.ui.theme.OceanSurface
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary
import com.hydroping.app.ui.theme.UrgentOrange
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    uiState: UiState,
    onExportCsv: () -> Unit,
    onExportJson: () -> Unit = {},
    onUpdateDrink: (DrinkLog) -> Unit = {},
    onDeleteDrink: (DrinkLog) -> Unit = {},
    onUndoDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    var editingDrink by remember { mutableStateOf<DrinkLog?>(null) }
    var selectedCalendarDate by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (editingDrink != null) {
        EditDrinkDialog(
            drink = editingDrink!!,
            unit = uiState.unit,
            onDismiss = { editingDrink = null },
            onSave = { updated ->
                onUpdateDrink(updated)
                editingDrink = null
            },
            onDelete = { toDelete ->
                onDeleteDrink(toDelete)
                editingDrink = null
                coroutineScope.launch {
                    val res = snackbarHostState.showSnackbar(
                        message = "Drink entry deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (res == SnackbarResult.ActionPerformed) {
                        onUndoDelete()
                    }
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(OceanDeep, OceanSurface, OceanDeep)))
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hydration Journey",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Analytics, calendar performance & records.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Export Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(OceanCard)
                                .border(1.dp, HydroCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable { onExportCsv() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HydroCyan)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(OceanCard)
                                .border(1.dp, GoldCelebration.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable { onExportJson() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldCelebration)
                        }
                    }
                }
            }

            // Sub tabs: 0 = Today & Logs, 1 = Calendar & Trends
            item {
                TabRow(
                    selectedTabIndex = selectedSubTab,
                    containerColor = OceanCard,
                    contentColor = HydroCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                            color = HydroCyan
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(text = "Today & Logs", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp))
                            }
                        }
                    )
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(text = "Calendar & Trends", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp))
                            }
                        }
                    )
                }
            }

            if (selectedSubTab == 0) {
                // 1. Milestone Badges Showcase
                item {
                    MilestonesCard(
                        milestones = uiState.milestones,
                        currentStreakDays = uiState.streakDays
                    )
                }

                // 2. Section Title
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Drink Logs",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "${uiState.todayDrinks.size} logged (${HydrationUnitHelper.format(uiState.currentTotalMl, uiState.unit)})",
                            fontSize = 12.sp,
                            color = HydroCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // 3. Drink logs list or empty placeholder
                if (uiState.todayDrinks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "No drinks",
                                    tint = TextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "No drinks logged yet today.",
                                    fontSize = 14.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Text(
                                    text = "Tap a quick button on the Home screen to begin!",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.todayDrinks) { drink ->
                        DrinkLogItem(
                            drink = drink,
                            unit = uiState.unit,
                            onEdit = { editingDrink = it },
                            onDelete = { toDel ->
                                onDeleteDrink(toDel)
                                coroutineScope.launch {
                                    val res = snackbarHostState.showSnackbar(
                                        message = "Deleted drink",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (res == SnackbarResult.ActionPerformed) {
                                        onUndoDelete()
                                    }
                                }
                            }
                        )
                    }
                }
            } else {
                // Calendar & Trends Tab
                item {
                    HydrationCalendarView(
                        dailyGoals = uiState.recentDailyGoals,
                        selectedDateString = selectedCalendarDate,
                        onDateSelected = { selectedCalendarDate = it }
                    )
                }

                item {
                    AnalyticsDashboard(
                        dailyGoals = uiState.recentDailyGoals,
                        unit = uiState.unit,
                        streakDays = uiState.streakDays
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun DrinkLogItem(
    drink: DrinkLog,
    unit: com.hydroping.app.domain.HydrationUnit,
    onEdit: (DrinkLog) -> Unit,
    onDelete: (DrinkLog) -> Unit
) {
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(drink.timestampMs))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OceanCard)
            .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(16.dp))
            .clickable { onEdit(drink) }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HydroBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Drink Icon",
                        tint = HydroCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "+${HydrationUnitHelper.format(drink.amountMl, unit)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$timeFormatted • ${drink.source}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onEdit(drink) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = HydroCyan.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                }

                IconButton(
                    onClick = { onDelete(drink) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = UrgentOrange.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

