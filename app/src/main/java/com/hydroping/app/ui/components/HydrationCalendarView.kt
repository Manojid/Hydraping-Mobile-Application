package com.hydroping.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.data.DailyGoal
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.SuccessGreen
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary
import com.hydroping.app.ui.theme.UrgentOrange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HydrationCalendarView(
    dailyGoals: List<DailyGoal>,
    selectedDateString: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonthCal by remember {
        mutableStateOf(Calendar.getInstance())
    }

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dayKeyFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    val daysInMonth = remember(currentMonthCal.get(Calendar.MONTH), currentMonthCal.get(Calendar.YEAR)) {
        val cal = currentMonthCal.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val offset = (firstDayOfWeek - Calendar.SUNDAY + 7) % 7

        val list = mutableListOf<CalendarDay>()
        // Add empty offset cells
        for (i in 0 until offset) {
            list.add(CalendarDay(0, "", false, 0, 0))
        }
        // Add days of month
        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = dayKeyFormat.format(cal.time)
            val matchGoal = dailyGoals.find { it.dateString == dateStr }
            val consumed = matchGoal?.totalConsumedMl ?: 0
            val target = matchGoal?.targetMl ?: 2000
            list.add(CalendarDay(day, dateStr, true, consumed, target))
        }
        list
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(OceanCard)
            .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            // Month Header with navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = HydroCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = monthYearFormat.format(currentMonthCal.time),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row {
                    IconButton(
                        onClick = {
                            val next = currentMonthCal.clone() as Calendar
                            next.add(Calendar.MONTH, -1)
                            currentMonthCal = next
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = HydroCyan)
                    }
                    IconButton(
                        onClick = {
                            val next = currentMonthCal.clone() as Calendar
                            next.add(Calendar.MONTH, 1)
                            currentMonthCal = next
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = HydroCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Day of week labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { dayLabel ->
                    Text(
                        text = dayLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Days Grid (Chunks of 7)
            daysInMonth.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    week.forEach { cell ->
                        if (cell.dayNumber == 0) {
                            Box(modifier = Modifier.weight(1f).height(38.dp))
                        } else {
                            val isSelected = cell.dateString == selectedDateString
                            val statusColor = when {
                                cell.consumedMl >= cell.targetMl && cell.consumedMl > 0 -> SuccessGreen
                                cell.consumedMl > 0 -> GoldCelebration
                                else -> TextMuted.copy(alpha = 0.3f)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) HydroBlue.copy(alpha = 0.4f) else Color.Transparent)
                                    .border(
                                        width = if (isSelected) 1.dp else 0.dp,
                                        color = if (isSelected) HydroCyan else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onDateSelected(cell.dateString) },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = cell.dayNumber.toString(),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(statusColor)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = SuccessGreen, label = "Goal Met")
                LegendItem(color = GoldCelebration, label = "Partial")
                LegendItem(color = TextMuted.copy(alpha = 0.4f), label = "Missed")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(text = label, fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(start = 4.dp))
    }
}

data class CalendarDay(
    val dayNumber: Int,
    val dateString: String,
    val isValid: Boolean,
    val consumedMl: Int,
    val targetMl: Int
)
