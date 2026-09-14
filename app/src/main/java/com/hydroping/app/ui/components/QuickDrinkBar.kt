package com.hydroping.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hydroping.app.domain.HydrationUnit
import com.hydroping.app.domain.HydrationUnitHelper
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary

@Composable
fun QuickDrinkBar(
    favoriteAmount: Int,
    unit: HydrationUnit = HydrationUnit.ML,
    onDrinkLogged: (Int) -> Unit,
    onCustomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = when (unit) {
        HydrationUnit.ML -> listOf(100, 250, 500)
        HydrationUnit.L -> listOf(250, 500, 750)
        HydrationUnit.OZ -> listOf(236, 355, 473) // 8oz, 12oz, 16oz
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "One-Tap Quick Logging",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            presets.forEach { amountMl ->
                val isFav = amountMl == favoriteAmount
                val formattedLabel = HydrationUnitHelper.formatShort(amountMl, unit)
                val unitLabel = unit.label

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(if (isFav) 8.dp else 2.dp, RoundedCornerShape(16.dp), spotColor = HydroCyan)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isFav) Brush.verticalGradient(listOf(HydroBlue, Color(0xFF0369A1)))
                            else Brush.verticalGradient(listOf(OceanCard, Color(0xFF131D33)))
                        )
                        .border(
                            width = if (isFav) 1.5.dp else 1.dp,
                            color = if (isFav) HydroCyan else Color(0xFF2E3E6E),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onDrinkLogged(amountMl) }
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Water Icon",
                            tint = if (isFav) Color.White else HydroCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "+$formattedLabel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = unitLabel,
                            fontSize = 10.sp,
                            color = if (isFav) Color.White.copy(alpha = 0.8f) else TextMuted
                        )
                    }
                }
            }

            // Custom amount button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(listOf(OceanCard, Color(0xFF131D33))))
                    .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(16.dp))
                    .clickable { onCustomClick() }
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Custom Drink",
                        tint = HydroCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Custom",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "amount",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

