package com.hydroping.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val OceanDeep = Color(0xFF070D1E)
val OceanSurface = Color(0xFF0F1A30)
val OceanCard = Color(0xFF162544)
val OceanCardElevated = Color(0xFF1D315B)

val HydroCyan = Color(0xFF00E5FF)
val HydroBlue = Color(0xFF0284C7)
val HydroLightBlue = Color(0xFF38BDF8)
val HydroWave = Color(0xFF0EA5E9)

val DropletHighlight = Color(0xFFBAE6FD)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val SuccessGreen = Color(0xFF10B981)
val GoldCelebration = Color(0xFFFBBF24)
val UrgentOrange = Color(0xFFF97316)

val HydroDarkColorScheme = darkColorScheme(
    primary = HydroBlue,
    onPrimary = Color.White,
    primaryContainer = OceanCard,
    onPrimaryContainer = HydroCyan,
    secondary = HydroLightBlue,
    onSecondary = OceanDeep,
    background = OceanDeep,
    onBackground = TextPrimary,
    surface = OceanSurface,
    onSurface = TextPrimary,
    surfaceVariant = OceanCard,
    onSurfaceVariant = TextSecondary
)
