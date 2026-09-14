package com.hydroping.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.domain.HydrationUnit
import com.hydroping.app.domain.HydrationUnitHelper
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.HydroWave
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import kotlin.math.sin

@Composable
fun WaterWaveProgress(
    currentMl: Int,
    targetMl: Int,
    unit: HydrationUnit = HydrationUnit.ML,
    modifier: Modifier = Modifier
) {
    val targetProgress = if (targetMl > 0) (currentMl.toFloat() / targetMl).coerceIn(0f, 1.25f) else 0f
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(targetProgress) {
        animatedProgress.animateTo(
            targetValue = targetProgress,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val progressPercent = (animatedProgress.value * 100).toInt()

    Box(
        modifier = modifier
            .size(175.dp)
            .shadow(16.dp, CircleShape, spotColor = HydroCyan.copy(alpha = 0.3f))
            .clip(CircleShape)
            .background(OceanCard)
            .border(3.dp, Brush.sweepGradient(listOf(HydroBlue, HydroCyan, HydroWave, HydroBlue)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Water wave canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val waterHeight = height * (1f - animatedProgress.value.coerceIn(0f, 1f))
            val waveAmplitude = 8.dp.toPx()
            val waveFrequency = 1.2f

            val path = Path().apply {
                moveTo(0f, height)
                lineTo(0f, waterHeight)
                var x = 0f
                while (x <= width) {
                    val y = waterHeight + waveAmplitude * sin(waveFrequency * (x / width) * 2 * Math.PI.toFloat() + wavePhase)
                    lineTo(x, y)
                    x += 4f
                }
                lineTo(width, height)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        HydroCyan.copy(alpha = 0.85f),
                        HydroWave.copy(alpha = 0.95f),
                        HydroBlue
                    )
                )
            )
        }

        // Percentage and stats readout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "$progressPercent%",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = "${HydrationUnitHelper.formatShort(currentMl, unit)} / ${HydrationUnitHelper.format(targetMl, unit)}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (progressPercent >= 50) Color.White.copy(alpha = 0.95f) else TextMuted
            )
        }
    }
}

