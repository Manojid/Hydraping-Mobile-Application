package com.hydroping.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hydroping.app.R
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.SuccessGreen
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary
import kotlin.random.Random

import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.CharacterMood

@Composable
fun GoalCelebrationDialog(
    userName: String,
    streakDays: Int,
    characterId: String = "pikachu",
    onDismiss: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.6f) }
    val confettiProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        confettiProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val confettiColors = listOf(
        HydroCyan,
        GoldCelebration,
        SuccessGreen,
        Color(0xFFE879F9),
        Color(0xFF38BDF8),
        Color(0xFFFBBF24)
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scaleAnim.value),
            contentAlignment = Alignment.Center
        ) {
            // Confetti canvas animation in background
            Canvas(modifier = Modifier.size(320.dp)) {
                val p = confettiProgress.value
                val random = Random(42)
                for (i in 0..45) {
                    val angle = random.nextFloat() * 2f * Math.PI.toFloat()
                    val dist = (50f + random.nextFloat() * 140f) * p
                    val x = size.width / 2 + Math.cos(angle.toDouble()).toFloat() * dist
                    val y = size.height / 2 + Math.sin(angle.toDouble()).toFloat() * dist + (p * 50f)
                    val color = confettiColors[i % confettiColors.size]
                    drawCircle(
                        color = color.copy(alpha = (1f - p * 0.7f).coerceIn(0f, 1f)),
                        radius = (random.nextFloat() * 4f + 3f),
                        center = Offset(x, y)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = OceanCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, GoldCelebration.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
                    .shadow(24.dp, spotColor = GoldCelebration)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(16.dp, CircleShape, spotColor = GoldCelebration)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(GoldCelebration.copy(alpha = 0.3f), Color.Transparent)))
                            .border(2.dp, GoldCelebration, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = CharacterCatalog.getDrawable(characterId, CharacterMood.HAPPY_CELEBRATING)),
                            contentDescription = "Celebration",
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Goal Completed! 🎉",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldCelebration
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Great job, ${userName.ifBlank { "Manoj" }}! You reached today's hydration goal.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldCelebration.copy(alpha = 0.15f))
                            .border(1.dp, GoldCelebration.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "🔥 $streakDays Day Streak Active!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldCelebration
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Normal hydration reminders are now paused for the rest of today.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = HydroBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Awesome, Keep Going! 💧",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
