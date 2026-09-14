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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.domain.HydrationUnitHelper
import com.hydroping.app.ui.UiState
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.UrgentOrange

@Composable
fun MinimalDashboardView(
    uiState: UiState,
    onDrinkLogged: (Int) -> Unit,
    onCustomClick: () -> Unit,
    onToggleMinimalMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Minimal Mode Banner & Companion return button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Minimal Focus",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(OceanCard)
                    .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(10.dp))
                    .clickable { onToggleMinimalMode() }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(13.dp))
                    Text(
                        text = "Character View",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = HydroCyan,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Wave Progress
        WaterWaveProgress(
            currentMl = uiState.currentTotalMl,
            targetMl = uiState.targetDailyMl,
            unit = uiState.unit,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Next Reminder Pill
        uiState.pacingInfo?.let { pacing ->
            val isBehind = pacing.isBehindSchedule
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isBehind) UrgentOrange.copy(alpha = 0.12f) else OceanCard)
                    .border(1.dp, if (isBehind) UrgentOrange.copy(alpha = 0.4f) else Color(0xFF2E3E6E), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isBehind) Icons.Default.Bolt else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isBehind) UrgentOrange else HydroCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isBehind) "Catch-up: Next in ${pacing.calculatedDelayMinutes}m"
                            else "Next reminder in ${pacing.calculatedDelayMinutes}m",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Text(
                        text = "${HydrationUnitHelper.format(pacing.remainingMl, uiState.unit)} left",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBehind) UrgentOrange else HydroCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick drink bar
        QuickDrinkBar(
            favoriteAmount = uiState.favoriteAmountMl,
            unit = uiState.unit,
            onDrinkLogged = onDrinkLogged,
            onCustomClick = onCustomClick
        )
    }
}
