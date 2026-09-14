package com.hydroping.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan

@Composable
fun ScreenEdgeGlowOverlay(
    modifier: Modifier = Modifier,
    glowColor: Color = HydroCyan,
    secondaryColor: Color = HydroBlue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "edge_glow_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val cornerGlowSpread by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 90f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corner_spread"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val edgeThickness = 12f

        // 1. Top Edge Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(glowColor.copy(alpha = 0.7f * pulseAlpha), Color.Transparent),
                startY = 0f,
                endY = 48f
            ),
            topLeft = Offset(0f, 0f),
            size = Size(w, 48f)
        )

        // 2. Bottom Edge Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, glowColor.copy(alpha = 0.7f * pulseAlpha)),
                startY = h - 48f,
                endY = h
            ),
            topLeft = Offset(0f, h - 48f),
            size = Size(w, 48f)
        )

        // 3. Left Edge Gradient
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(glowColor.copy(alpha = 0.6f * pulseAlpha), Color.Transparent),
                startX = 0f,
                endX = 40f
            ),
            topLeft = Offset(0f, 0f),
            size = Size(40f, h)
        )

        // 4. Right Edge Gradient
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, glowColor.copy(alpha = 0.6f * pulseAlpha)),
                startX = w - 40f,
                endX = w
            ),
            topLeft = Offset(w - 40f, 0f),
            size = Size(40f, h)
        )

        // 5. Four Corner Radial Accents
        val cornerAlpha = (0.85f * pulseAlpha).coerceIn(0f, 1f)

        // Top-Left Corner
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor.copy(alpha = cornerAlpha), secondaryColor.copy(alpha = cornerAlpha * 0.5f), Color.Transparent),
                center = Offset(0f, 0f),
                radius = cornerGlowSpread * 1.6f
            ),
            center = Offset(0f, 0f),
            radius = cornerGlowSpread * 1.6f
        )

        // Top-Right Corner
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor.copy(alpha = cornerAlpha), secondaryColor.copy(alpha = cornerAlpha * 0.5f), Color.Transparent),
                center = Offset(w, 0f),
                radius = cornerGlowSpread * 1.6f
            ),
            center = Offset(w, 0f),
            radius = cornerGlowSpread * 1.6f
        )

        // Bottom-Left Corner
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor.copy(alpha = cornerAlpha), secondaryColor.copy(alpha = cornerAlpha * 0.5f), Color.Transparent),
                center = Offset(0f, h),
                radius = cornerGlowSpread * 1.6f
            ),
            center = Offset(0f, h),
            radius = cornerGlowSpread * 1.6f
        )

        // Bottom-Right Corner
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor.copy(alpha = cornerAlpha), secondaryColor.copy(alpha = cornerAlpha * 0.5f), Color.Transparent),
                center = Offset(w, h),
                radius = cornerGlowSpread * 1.6f
            ),
            center = Offset(w, h),
            radius = cornerGlowSpread * 1.6f
        )

        // 6. Crisp Inner Border Frame
        drawRect(
            color = glowColor.copy(alpha = 0.6f * pulseAlpha),
            topLeft = Offset(edgeThickness / 2, edgeThickness / 2),
            size = Size(w - edgeThickness, h - edgeThickness),
            style = Stroke(width = edgeThickness / 2)
        )
    }
}
