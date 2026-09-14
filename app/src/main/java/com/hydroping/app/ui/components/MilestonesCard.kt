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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.domain.MilestoneBadge
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.OceanCardElevated
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary

@Composable
fun MilestonesCard(
    milestones: List<MilestoneBadge>,
    currentStreakDays: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(OceanCardElevated)
            .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Milestones",
                        tint = GoldCelebration,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Streak Milestones",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Text(
                    text = "$currentStreakDays Day Streak",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldCelebration
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                milestones.forEach { badge ->
                    val isUnlocked = badge.isUnlocked
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isUnlocked) OceanCard else Color(0xFF0F172A))
                            .border(
                                width = 1.dp,
                                color = if (isUnlocked) GoldCelebration.copy(alpha = 0.5f) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isUnlocked) badge.iconEmoji else "🔒",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${badge.requiredDays}d",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) GoldCelebration else TextMuted
                            )
                            Text(
                                text = if (isUnlocked) "Unlocked" else "Locked",
                                fontSize = 9.sp,
                                color = if (isUnlocked) HydroCyan else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
