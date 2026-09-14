package com.hydroping.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.data.DrinkLog
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.OceanDeep
import com.hydroping.app.ui.theme.SuccessGreen
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun DashboardTrendsCard(
    todayDrinks: List<DrinkLog>,
    currentTotalMl: Int,
    targetDailyMl: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val completionPct = if (targetDailyMl > 0) ((currentTotalMl.toFloat() / targetDailyMl) * 100).toInt() else 0
    val averageIntakePerDrink = if (todayDrinks.isNotEmpty()) currentTotalMl / todayDrinks.size else 0

    // Analyze peak hydration hours from today's logs
    val morningDrinks = todayDrinks.count {
        val cal = Calendar.getInstance().apply { timeInMillis = it.timestampMs }
        cal.get(Calendar.HOUR_OF_DAY) in 6..11
    }
    val afternoonDrinks = todayDrinks.count {
        val cal = Calendar.getInstance().apply { timeInMillis = it.timestampMs }
        cal.get(Calendar.HOUR_OF_DAY) in 12..17
    }
    val eveningDrinks = todayDrinks.count {
        val cal = Calendar.getInstance().apply { timeInMillis = it.timestampMs }
        cal.get(Calendar.HOUR_OF_DAY) in 18..23
    }

    val peakWindow = when {
        afternoonDrinks >= morningDrinks && afternoonDrinks >= eveningDrinks && afternoonDrinks > 0 -> "Afternoon (12 PM - 5 PM)"
        morningDrinks >= eveningDrinks && morningDrinks > 0 -> "Morning (6 AM - 11 AM)"
        eveningDrinks > 0 -> "Evening (6 PM - 11 PM)"
        else -> "Steady Cadence"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(OceanCard)
            .border(1.dp, HydroBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(HydroBlue.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = HydroCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Hydration Performance",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Daily pacing velocity & stats",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (completionPct >= 100) SuccessGreen.copy(alpha = 0.2f) else HydroCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$completionPct% Goal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (completionPct >= 100) SuccessGreen else HydroCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Linear Progress Bar
            LinearProgressIndicator(
                progress = { (completionPct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (completionPct >= 100) SuccessGreen else HydroCyan,
                trackColor = OceanDeep,
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Column Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Avg Sip
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OceanDeep)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("Avg Sip", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (averageIntakePerDrink > 0) "${averageIntakePerDrink} ml" else "—",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HydroCyan
                        )
                    }
                }

                // Metric 2: Drink Count
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OceanDeep)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("Logs Today", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${todayDrinks.size} drinks",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldCelebration
                        )
                    }
                }

                // Metric 3: Active Streak
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OceanDeep)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("Streak", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$streakDays days 🔥",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Peak Window Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = HydroCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Most Active Window: $peakWindow",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
