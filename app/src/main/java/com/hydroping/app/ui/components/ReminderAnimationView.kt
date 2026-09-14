package com.hydroping.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hydroping.app.domain.ReminderAnimation
import com.hydroping.app.ui.theme.GoldCelebration
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.HydroWave
import com.hydroping.app.ui.theme.OceanCard
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun ReminderAnimationView(
    animationType: ReminderAnimation,
    characterAvatarRes: Int,
    modifier: Modifier = Modifier,
    sizeDp: Int = 180,
    progress: Float = 0.5f
) {
    Box(
        modifier = modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center
    ) {
        when (animationType) {
            ReminderAnimation.LIQUID_WAVE -> LiquidWaveAnimation(characterAvatarRes = characterAvatarRes, sizeDp = sizeDp, progress = progress)
            ReminderAnimation.RIPPLE_PULSE -> RipplePulseAnimation(characterAvatarRes = characterAvatarRes, sizeDp = sizeDp, progress = progress)
            ReminderAnimation.GLOWING_ORB -> GlowingOrbAnimation(characterAvatarRes = characterAvatarRes, sizeDp = sizeDp, progress = progress)
        }
    }
}

/**
 * Reusable animated background wave canvas driven by dynamic hydration progress.
 */
@Composable
fun WaterWaveBackgroundCanvas(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1.15f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "liquid_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val waveOscillation by infiniteTransition.animateFloat(
        initialValue = -0.015f,
        targetValue = 0.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "oscillation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val fillLevel = (animatedProgress + waveOscillation).coerceIn(0.04f, 1.0f)
        val baseWaterY = height * (1f - fillLevel)
        val amplitude = (10.dp.toPx() * (1f - (fillLevel - 0.5f).let { if (it < 0) -it else it } * 0.5f)).coerceAtLeast(4.dp.toPx())

        val path = Path().apply {
            moveTo(0f, height)
            lineTo(0f, baseWaterY)
            var x = 0f
            val step = 8f
            while (x <= width) {
                val y = baseWaterY + (amplitude * sin((x / width * 2 * PI.toFloat()) + wavePhase))
                lineTo(x, y)
                x += step
            }
            lineTo(width, height)
            close()
        }

        val colors = if (progress >= 1.0f) {
            listOf(
                GoldCelebration.copy(alpha = 0.85f),
                HydroCyan.copy(alpha = 0.9f),
                HydroBlue
            )
        } else {
            listOf(
                Color(0xEE00E5FF),
                Color(0xDD0072FF),
                Color(0xFA022557)
            )
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(colors = colors)
        )
    }
}

/**
 * 1. LIQUID WAVE ANIMATION:
 * Filling sloshing liquid wave that simulates dynamic water rising with animated sine crests.
 */
@Composable
private fun LiquidWaveAnimation(
    characterAvatarRes: Int,
    sizeDp: Int,
    progress: Float = 0.5f
) {
    val borderColor = if (progress >= 1.0f) GoldCelebration else HydroCyan
    val spotGlow = if (progress >= 1.0f) GoldCelebration.copy(alpha = 0.4f) else HydroCyan.copy(alpha = 0.35f)

    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .shadow(20.dp, CircleShape, spotColor = spotGlow)
            .clip(CircleShape)
            .background(Color(0xFF071226))
            .border(2.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        WaterWaveBackgroundCanvas(progress = progress)

        // Center Mascot Avatar
        Image(
            painter = painterResource(id = characterAvatarRes),
            contentDescription = "Mascot",
            modifier = Modifier
                .size((sizeDp * 0.65).dp)
                .clip(CircleShape)
        )
    }
}

/**
 * 2. RIPPLING WATER ANIMATION:
 * Concentric multi-ring ripple waves pulsing outward with smooth sonar alpha fade.
 */
@Composable
private fun RipplePulseAnimation(
    characterAvatarRes: Int,
    sizeDp: Int,
    progress: Float = 0.5f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")

    val ripple1 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutLinearInEasing)),
        label = "r1"
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutLinearInEasing)),
        label = "a1"
    )

    val ripple2 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(tween(2400, delayMillis = 800, easing = FastOutLinearInEasing)),
        label = "r2"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2400, delayMillis = 800, easing = FastOutLinearInEasing)),
        label = "a2"
    )

    Box(modifier = Modifier.size(sizeDp.dp), contentAlignment = Alignment.Center) {
        // Outer ripple 1
        Box(
            modifier = Modifier
                .size((sizeDp * 0.75).dp)
                .scale(ripple1)
                .border(2.dp, HydroCyan.copy(alpha = alpha1), CircleShape)
        )

        // Outer ripple 2
        Box(
            modifier = Modifier
                .size((sizeDp * 0.75).dp)
                .scale(ripple2)
                .border(2.dp, HydroBlue.copy(alpha = alpha2), CircleShape)
        )

        // Center Mascot Card with dynamic water wave fill
        Box(
            modifier = Modifier
                .size((sizeDp * 0.72).dp)
                .shadow(16.dp, CircleShape, spotColor = if (progress >= 1.0f) GoldCelebration else HydroCyan)
                .clip(CircleShape)
                .background(Color(0xFF071226))
                .border(2.dp, if (progress >= 1.0f) GoldCelebration else HydroCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            WaterWaveBackgroundCanvas(progress = progress)

            Image(
                painter = painterResource(id = characterAvatarRes),
                contentDescription = "Mascot",
                modifier = Modifier.size((sizeDp * 0.62).dp)
            )
        }
    }
}

/**
 * 3. BOUNCING DROPLET ANIMATION:
 * Playful vertical jumping drop with dynamic squash-and-stretch and shadow scaling.
 */
@Composable
private fun BouncingDropletAnimation(
    characterAvatarRes: Int,
    sizeDp: Int,
    progress: Float = 0.5f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bouncing_droplet")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -16f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceY"
    )

    val scaleX by infiniteTransition.animateFloat(
        initialValue = 1.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "squashX"
    )

    val shadowScale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shadowScale"
    )

    Box(modifier = Modifier.size(sizeDp.dp), contentAlignment = Alignment.Center) {
        // Floor shadow
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = (sizeDp * 0.55).dp, height = 12.dp)
                .offset(y = (-6).dp)
                .scale(shadowScale)
                .background(Color(0x5500E5FF), CircleShape)
        )

        // Mascot with vertical bounce, squash and dynamic water wave fill
        Box(
            modifier = Modifier
                .offset(y = offsetY.dp)
                .scale(scaleX = scaleX, scaleY = 2f - scaleX)
                .size((sizeDp * 0.72).dp)
                .shadow(16.dp, CircleShape, spotColor = if (progress >= 1.0f) GoldCelebration else HydroCyan)
                .clip(CircleShape)
                .background(Color(0xFF071226))
                .border(2.dp, if (progress >= 1.0f) GoldCelebration else HydroCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            WaterWaveBackgroundCanvas(progress = progress)

            Image(
                painter = painterResource(id = characterAvatarRes),
                contentDescription = "Mascot",
                modifier = Modifier.size((sizeDp * 0.62).dp)
            )
        }
    }
}

/**
 * 4. CYBER HYDRATION ORB ANIMATION:
 * Futuristic neon-cyan energy ring orb with counter-rotating tech arcs and pulsing aura.
 */
@Composable
private fun GlowingOrbAnimation(
    characterAvatarRes: Int,
    sizeDp: Int,
    progress: Float = 0.5f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_orb")

    val rotationCW by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cw"
    )

    val rotationCCW by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ccw"
    )

    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura"
    )

    Box(modifier = Modifier.size(sizeDp.dp), contentAlignment = Alignment.Center) {
        // Radial Tech Arc 1 (Clockwise)
        Canvas(
            modifier = Modifier
                .size((sizeDp * 0.92).dp)
                .rotate(rotationCW)
        ) {
            drawArc(
                color = HydroCyan,
                startAngle = 10f,
                sweepAngle = 100f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx())
            )
            drawArc(
                color = GoldCelebration,
                startAngle = 190f,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Radial Tech Arc 2 (Counter-Clockwise)
        Canvas(
            modifier = Modifier
                .size((sizeDp * 0.82).dp)
                .rotate(rotationCCW)
        ) {
            drawArc(
                color = HydroBlue,
                startAngle = 60f,
                sweepAngle = 140f,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx())
            )
            drawArc(
                color = HydroCyan.copy(alpha = 0.6f),
                startAngle = 240f,
                sweepAngle = 80f,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Center Glowing Mascot Orb with dynamic water wave fill
        Box(
            modifier = Modifier
                .size((sizeDp * 0.68).dp)
                .scale(auraPulse)
                .shadow(24.dp, CircleShape, spotColor = if (progress >= 1.0f) GoldCelebration else HydroCyan)
                .clip(CircleShape)
                .background(Color(0xFF071226))
                .border(2.5.dp, if (progress >= 1.0f) GoldCelebration else HydroCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            WaterWaveBackgroundCanvas(progress = progress)

            Image(
                painter = painterResource(id = characterAvatarRes),
                contentDescription = "Mascot",
                modifier = Modifier.size((sizeDp * 0.58).dp)
            )
        }
    }
}
