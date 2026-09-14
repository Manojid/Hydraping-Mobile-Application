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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hydroping.app.data.DrinkLog
import com.hydroping.app.domain.HydrationUnit
import com.hydroping.app.domain.HydrationUnitHelper
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary
import com.hydroping.app.ui.theme.UrgentOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditDrinkDialog(
    drink: DrinkLog,
    unit: HydrationUnit = HydrationUnit.ML,
    onDismiss: () -> Unit,
    onSave: (DrinkLog) -> Unit,
    onDelete: (DrinkLog) -> Unit
) {
    var amountText by remember { mutableStateOf(drink.amountMl.toString()) }
    val formattedTime = remember(drink.timestampMs) {
        SimpleDateFormat("h:mm a • MMM d, yyyy", Locale.getDefault()).format(Date(drink.timestampMs))
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = OceanCard,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Drink",
                        tint = HydroCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Edit Drink Entry",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Text(
                    text = "Logged at $formattedTime (${drink.source})",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp)
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Volume (ml)", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HydroCyan,
                        unfocusedBorderColor = Color(0xFF2E3E6E),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = HydroCyan
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick presets in edit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100, 250, 350, 500).forEach { preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF2E3E6E), RoundedCornerShape(10.dp))
                                .clickable { amountText = preset.toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$preset",
                                fontSize = 12.sp,
                                color = HydroCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Delete Button
                OutlinedButton(
                    onClick = {
                        onDelete(drink)
                        onDismiss()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentOrange),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(UrgentOrange.copy(alpha = 0.5f))),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = UrgentOrange, modifier = Modifier.size(16.dp))
                    Text(text = "Delete This Entry", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = UrgentOrange, modifier = Modifier.padding(start = 6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val newAmount = amountText.toIntOrNull() ?: drink.amountMl
                            onSave(drink.copy(amountMl = newAmount.coerceAtLeast(10)))
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HydroBlue),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
