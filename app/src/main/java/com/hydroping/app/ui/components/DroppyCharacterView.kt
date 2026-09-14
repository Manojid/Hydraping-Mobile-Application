package com.hydroping.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.R
import com.hydroping.app.domain.CharacterCatalog
import com.hydroping.app.domain.CharacterMood
import com.hydroping.app.domain.HydrationUnit
import com.hydroping.app.domain.HydrationUnitHelper
import com.hydroping.app.domain.ReminderAnimation
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary

@Composable
fun DroppyCharacterView(
    characterId: String,
    mood: CharacterMood,
    dialogueText: String,
    currentMl: Int = 0,
    targetMl: Int = 2000,
    unit: HydrationUnit = HydrationUnit.ML,
    onCharacterTap: () -> Unit,
    animationType: ReminderAnimation = ReminderAnimation.LIQUID_WAVE,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "droppy_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val drawableRes = CharacterCatalog.getDrawable(characterId, mood)

    val progressFraction = if (targetMl > 0) (currentMl.toFloat() / targetMl).coerceIn(0f, 1.25f) else 0f
    val percentProgress = (progressFraction * 100).toInt()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dynamic selected animation with companion avatar & real-time water fill
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(y = floatOffset.dp)
                .scale(scalePulse)
                .clickable { onCharacterTap() },
            contentAlignment = Alignment.Center
        ) {
            ReminderAnimationView(
                animationType = animationType,
                characterAvatarRes = drawableRes,
                sizeDp = 220,
                progress = progressFraction
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Integrated Hydration Percentage & Volume Status Pill
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = if (percentProgress >= 100) GoldCelebration.copy(alpha = 0.45f) else HydroCyan.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF0F1B38),
                            Color(0xFF16254E)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        if (percentProgress >= 100) listOf(GoldCelebration, HydroCyan)
                        else listOf(HydroCyan.copy(alpha = 0.85f), HydroBlue.copy(alpha = 0.65f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = if (percentProgress >= 100) GoldCelebration else HydroCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$percentProgress%",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (percentProgress >= 100) GoldCelebration else HydroCyan
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${HydrationUnitHelper.formatShort(currentMl, unit)} / ${HydrationUnitHelper.format(targetMl, unit)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dialogue speech bubble placed below
        Box(
            modifier = Modifier
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF1E293B).copy(alpha = 0.95f),
                            Color(0xFF0F172A).copy(alpha = 0.95f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(HydroCyan.copy(alpha = 0.6f), HydroBlue.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = dialogueText,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
