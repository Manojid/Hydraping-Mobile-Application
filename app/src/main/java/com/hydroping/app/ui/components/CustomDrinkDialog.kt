package com.hydroping.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.OceanSurface
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary

@Composable
fun CustomDrinkDialog(
    initialAmountMl: Int = 300,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedAmount by remember { mutableFloatStateOf(initialAmountMl.toFloat()) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(OceanSurface)
                .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Custom Water Amount",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${selectedAmount.toInt()} ml",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HydroCyan,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Slider(
                    value = selectedAmount,
                    onValueChange = { selectedAmount = it },
                    valueRange = 50f..1000f,
                    steps = 18,
                    colors = SliderDefaults.colors(
                        thumbColor = HydroCyan,
                        activeTrackColor = HydroBlue,
                        inactiveTrackColor = OceanCard
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "50 ml", fontSize = 11.sp, color = TextMuted)
                    Text(text = "500 ml", fontSize = 11.sp, color = TextMuted)
                    Text(text = "1000 ml", fontSize = 11.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = { onConfirm(selectedAmount.toInt()) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HydroBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "Log Drink", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
