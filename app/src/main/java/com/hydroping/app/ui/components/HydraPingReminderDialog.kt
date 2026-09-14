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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hydroping.app.R
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.CharacterMood
import com.hydroping.app.domain.CharacterProfile
import com.hydroping.app.domain.ReminderAnimation
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.OceanDeep
import com.hydroping.app.ui.theme.OceanSurface
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary

@Composable
fun HydraPingReminderDialog(
    character: CharacterProfile,
    userName: String,
    animationType: ReminderAnimation,
    currentConsumedMl: Int,
    targetDailyMl: Int,
    requiredAmountMl: Int = 250,
    onDrinkLogged: (Int) -> Unit,
    onLogCustomClick: () -> Unit,
    onSnoozeClick: () -> Unit,
    onSkipClick: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val safeTarget = if (targetDailyMl > 0) targetDailyMl else 2000
    val progressFloat = (currentConsumedMl.toFloat() / safeTarget.toFloat()).coerceIn(0f, 1f)
    val progressPercent = (progressFloat * 100).toInt()
    val mascotAvatar = CharacterCatalog.getDrawable(
        characterId = character.id,
        mood = if (progressPercent < 70) CharacterMood.THIRSTY else CharacterMood.HYDRATED
    )
    val displayName = if (userName.isNotBlank()) userName else "Manoj"

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(28.dp, RoundedCornerShape(24.dp), spotColor = HydroCyan)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(OceanSurface, OceanDeep)))
                .border(2.dp, HydroCyan.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Header Bar with Branding & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(HydroBlue.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = HydroCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "HYDRAPING • ACTIVE REMINDER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HydroCyan,
                            letterSpacing = 0.08.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Prominent Selected Animation View
                ReminderAnimationView(
                    animationType = animationType,
                    characterAvatarRes = mascotAvatar,
                    sizeDp = 170
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Personalized Greeting & Direct Context
                Text(
                    text = "Hey $displayName 💧",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Text(
                    text = "You need $requiredAmountMl ml to stay on track.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Daily Progress Context Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(OceanCard)
                        .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Intake",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "$currentConsumedMl / $safeTarget ml ($progressPercent%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HydroCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progressFloat },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = HydroCyan,
                            trackColor = Color(0xFF0F1A30)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ACTION BUTTON 1: Drink 250 ml (or required amount)
                Button(
                    onClick = {
                        onDrinkLogged(requiredAmountMl)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HydroCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Drink $requiredAmountMl ml",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ACTION BUTTONS ROW: Log Water, Snooze & Skip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onLogCustomClick()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HydroCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = HydroCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Log",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onSnoozeClick()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(
                            Icons.Default.Snooze,
                            contentDescription = null,
                            tint = GoldCelebration,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Snooze",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onSkipClick()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
