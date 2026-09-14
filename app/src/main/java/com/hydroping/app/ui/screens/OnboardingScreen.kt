package com.hydroping.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.R
import com.hydroping.app.domain.ActivityLevel
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.CharacterMood
import com.hydroping.app.domain.HydrationUnit
import com.hydroping.app.domain.PersonalityType
import com.hydroping.app.domain.ReminderAnimation
import com.hydroping.app.ui.components.HydraPingReminderDialog
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.OceanDeep
import com.hydroping.app.ui.theme.OceanSurface
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onStartCompanion: (
        userName: String,
        goalMl: Int,
        intervalMins: Int,
        smartPacing: Boolean,
        characterId: String,
        personality: PersonalityType,
        unit: HydrationUnit,
        activityLevel: ActivityLevel,
        wakeHour: Int,
        sleepHour: Int,
        animation: ReminderAnimation
    ) -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) } // 1..6

    // Persistent onboarding state preserved across steps
    var userNameInput by remember { mutableStateOf("") }
    var selectedActivity by remember { mutableStateOf(ActivityLevel.MODERATE) }
    var goalMl by remember { mutableFloatStateOf(2000f) }
    var selectedUnit by remember { mutableStateOf(HydrationUnit.ML) }
    var selectedCharId by remember { mutableStateOf("pikachu") }
    var selectedPersonality by remember { mutableStateOf(PersonalityType.CUTE) }
    var selectedIntervalMins by remember { mutableIntStateOf(60) }
    var wakeHour by remember { mutableIntStateOf(7) }
    var sleepHour by remember { mutableIntStateOf(23) }
    var selectedAnimation by remember { mutableStateOf(ReminderAnimation.LIQUID_WAVE) }
    var previewAnimation by remember { mutableStateOf<ReminderAnimation?>(null) }

    // Live preview dialog for animation selection step
    if (previewAnimation != null) {
        val previewChar = CharacterCatalog.fromId(selectedCharId)
        HydraPingReminderDialog(
            character = previewChar,
            userName = userNameInput.trim().ifEmpty { "Manoj" },
            animationType = previewAnimation!!,
            currentConsumedMl = 500,
            targetDailyMl = goalMl.toInt(),
            requiredAmountMl = 250,
            onDrinkLogged = {
                Toast.makeText(context, "HydraPing Preview: Drank $it ml! 💧", Toast.LENGTH_SHORT).show()
                previewAnimation = null
            },
            onLogCustomClick = {
                Toast.makeText(context, "HydraPing Preview: Log Water opened! 💧", Toast.LENGTH_SHORT).show()
                previewAnimation = null
            },
            onSnoozeClick = {
                Toast.makeText(context, "HydraPing Preview: Snoozed! 💧", Toast.LENGTH_SHORT).show()
                previewAnimation = null
            },
            onSkipClick = {
                Toast.makeText(context, "HydraPing Preview: Skipped! 💧", Toast.LENGTH_SHORT).show()
                previewAnimation = null
            },
            onDismiss = { previewAnimation = null }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(OceanDeep, OceanSurface, OceanDeep)))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Step progress & indicators
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HydraPing Setup",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroCyan
                    )
                    Text(
                        text = "Step $currentStep of 6",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { currentStep / 6f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = HydroCyan,
                    trackColor = Color(0xFF162544)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Step Content (Animated transition between steps)
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_step_content",
                modifier = Modifier.weight(1f, fill = false)
            ) { step ->
                when (step) {
                    1 -> Step1PersonalDetails(
                        userName = userNameInput,
                        onUserNameChange = { userNameInput = it },
                        activityLevel = selectedActivity,
                        onActivityChange = { selectedActivity = it }
                    )
                    2 -> Step2HydrationTarget(
                        goalMl = goalMl,
                        onGoalChange = { goalMl = it }
                    )
                    3 -> Step3Companion(
                        selectedCharId = selectedCharId,
                        onCharSelected = { selectedCharId = it }
                    )
                    4 -> Step4Schedule(
                        intervalMins = selectedIntervalMins,
                        onIntervalChange = { selectedIntervalMins = it }
                    )
                    5 -> Step5SelectAnimation(
                        selectedAnimation = selectedAnimation,
                        onAnimationSelect = { selectedAnimation = it },
                        onPreviewClick = { previewAnimation = it }
                    )
                    6 -> Step6FinalSummary(
                        userName = userNameInput.trim().ifEmpty { "Manoj" },
                        goalMl = goalMl.toInt(),
                        charId = selectedCharId,
                        intervalMins = selectedIntervalMins,
                        animation = selectedAnimation
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons (Bottom Row)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (shown on steps 2..6)
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep -= 1 },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3E6E))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Next / Launch Button
                if (currentStep < 6) {
                    Button(
                        onClick = { currentStep += 1 },
                        modifier = Modifier
                            .weight(if (currentStep > 1) 1.5f else 1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HydroBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (currentStep == 5) "Continue →" else "Next →",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Final Screen: Prominent Launch Button
                    Button(
                        onClick = {
                            val finalName = userNameInput.trim().ifEmpty { "Manoj" }
                            onStartCompanion(
                                finalName,
                                goalMl.toInt(),
                                selectedIntervalMins,
                                true,
                                selectedCharId,
                                selectedPersonality,
                                selectedUnit,
                                selectedActivity,
                                wakeHour,
                                sleepHour,
                                selectedAnimation
                            )
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HydroCyan,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Launch HydraPing! 💧",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// PAGE 1: PERSONAL DETAILS
// ==========================================
@Composable
private fun Step1PersonalDetails(
    userName: String,
    onUserNameChange: (String) -> Unit,
    activityLevel: ActivityLevel,
    onActivityChange: (ActivityLevel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(OceanCard)
                .border(2.dp, HydroCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pikachu_happy),
                contentDescription = "Welcome Mascot",
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Welcome to HydraPing!",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Let's personalize your hydration experience.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name input card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(OceanCard)
                .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(18.dp))
                    Text(
                        text = "What should we call you?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    placeholder = { Text("Manoj", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HydroCyan,
                        unfocusedBorderColor = Color(0xFF2E3E6E),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = HydroCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Activity Level Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(OceanCard)
                .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Daily Activity Level",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ActivityLevel.values().forEach { level ->
                        val isSel = activityLevel == level
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) HydroBlue else Color(0xFF0F172A))
                                .border(1.dp, if (isSel) HydroCyan else Color(0xFF2E3E6E), RoundedCornerShape(10.dp))
                                .clickable { onActivityChange(level) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = level.simpleTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PAGE 2: HYDRATION / TARGET DETAILS
// ==========================================
@Composable
private fun Step2HydrationTarget(
    goalMl: Float,
    onGoalChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Daily Hydration Target",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tailored for healthy hydration and optimal focus.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(OceanCard)
                .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Daily Target",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${goalMl.toInt()} ml",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = HydroCyan
                )
                Text(
                    text = "Suggested ~${(goalMl.toInt() / 250)} standard glasses",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                Slider(
                    value = goalMl,
                    onValueChange = onGoalChange,
                    valueRange = 1000f..4000f,
                    steps = 29,
                    colors = SliderDefaults.colors(
                        thumbColor = HydroCyan,
                        activeTrackColor = HydroBlue,
                        inactiveTrackColor = Color(0xFF0F172A)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1500, 2000, 2500, 3000).forEach { preset ->
                        val isSel = goalMl.toInt() == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) HydroBlue else Color(0xFF0F172A))
                                .border(1.dp, if (isSel) HydroCyan else Color(0xFF2E3E6E), RoundedCornerShape(8.dp))
                                .clickable { onGoalChange(preset.toFloat()) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${preset} ml",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// ==========================================
// PAGE 3: CHARACTER SELECTION
// ==========================================
@Composable
private fun Step3Companion(
    selectedCharId: String,
    onCharSelected: (String) -> Unit
) {
    val mascotRes = CharacterCatalog.getDrawable(selectedCharId, CharacterMood.HYDRATED)

    val characterDescription = when (selectedCharId.lowercase()) {
        "jerry" -> "Jerry brings clever, cheerful energy from Tom & Jerry to keep you well hydrated! 🐭🧀"
        "whiskers" -> "Whiskers guides you with calm, royal, and steady feline patience. 🐾"
        else -> "Pikachu brings high-energy electric sparks to charge up your daily hydration streak! ⚡"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Your Character",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Your character guides your reminders and hydration milestones.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(110.dp)
                .shadow(18.dp, CircleShape, spotColor = HydroCyan)
                .clip(CircleShape)
                .background(OceanCard)
                .border(2.5.dp, HydroCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = mascotRes),
                contentDescription = "Selected Mascot",
                modifier = Modifier.size(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Character selection cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CharacterCatalog.allCharacters.forEach { char ->
                val isSel = selectedCharId == char.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSel) Color(0xFF13223E) else OceanCard)
                        .border(
                            width = if (isSel) 2.dp else 1.dp,
                            color = if (isSel) HydroCyan else Color(0xFF2E3E6E),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onCharSelected(char.id) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = char.previewRes),
                                contentDescription = char.name,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = char.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) HydroCyan else TextPrimary
                                )
                                Text(
                                    text = when (char.id) {
                                        "pikachu" -> "⚡ Electric Pokémon Character"
                                        "jerry" -> "🐭 Tom & Jerry Mouse Character"
                                        "whiskers" -> "🐾 Calm & Stylish Feline"
                                        else -> "⚡ Smart Hydration Character"
                                    },
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        if (isSel) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = HydroCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = characterDescription,
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        )
    }
}

// ==========================================
// PAGE 4: SCHEDULE / OTHER DETAILS
// ==========================================
@Composable
private fun Step4Schedule(
    intervalMins: Int,
    onIntervalChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reminder Cadence",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "HydraPing monitors your active hours and paces reminders.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(OceanCard)
                .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Base Reminder Interval",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(30, 45, 60, 90).forEach { mins ->
                        val isSelected = intervalMins == mins
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) HydroBlue else Color(0xFF0F172A))
                                .border(1.dp, if (isSelected) HydroCyan else Color(0xFF2E3E6E), RoundedCornerShape(12.dp))
                                .clickable { onIntervalChange(mins) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mins}m",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Active Hours: 7:00 AM – 11:00 PM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Smart Pacing Feature Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0B192E))
                .border(1.dp, HydroCyan.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Alarm, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(22.dp))
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(text = "Smart Catch-Up Engine Enabled", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HydroCyan)
                    Text(
                        text = "If you fall behind, reminders dynamically adapt catch-up amounts without overwhelming you.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// PAGE 5: "DASHBOARD ANIMATION STYLE" (DEDICATED STEP)
// ==========================================
@Composable
private fun Step5SelectAnimation(
    selectedAnimation: ReminderAnimation,
    onAnimationSelect: (ReminderAnimation) -> Unit,
    onPreviewClick: (ReminderAnimation) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dashboard Animation Style",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Choose the live fluid water animation that fills behind your character on the main Dashboard.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReminderAnimation.values().forEach { anim ->
                val isSelected = selectedAnimation == anim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0xFF13223E) else OceanCard)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) HydroCyan else Color(0xFF2E3E6E),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onAnimationSelect(anim) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = anim.iconEmoji,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Column {
                                Text(
                                    text = anim.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) HydroCyan else TextPrimary
                                )
                                Text(
                                    text = anim.description,
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Preview button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HydroCyan.copy(alpha = 0.2f))
                                .border(1.dp, HydroCyan, RoundedCornerShape(8.dp))
                                .clickable { onPreviewClick(anim) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = HydroCyan, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Preview",
                                    fontSize = 11.sp,
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
    }
}

// ==========================================
// FINAL ONBOARDING SCREEN: SUMMARY & LAUNCH
// ==========================================
@Composable
private fun Step6FinalSummary(
    userName: String,
    goalMl: Int,
    charId: String,
    intervalMins: Int,
    animation: ReminderAnimation
) {
    val charProfile = CharacterCatalog.fromId(charId)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .shadow(20.dp, CircleShape, spotColor = HydroCyan)
                .clip(CircleShape)
                .background(OceanCard)
                .border(2.5.dp, HydroCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = charProfile.previewRes),
                contentDescription = charProfile.name,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "You're All Set, $userName! 🎉",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Your personalized smart hydration setup is ready to go.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(OceanCard)
                .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryRow("Character", "${charProfile.name}")
                SummaryRow("Daily Target", "$goalMl ml")
                SummaryRow("Reminder Interval", "Every ${intervalMins}m")
                SummaryRow("Animation Style", "${animation.iconEmoji} ${animation.title}")
                SummaryRow("Active Window", "7:00 AM – 11:00 PM")
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HydroCyan)
    }
}
