package com.hydroping.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.hydroping.app.ui.theme.HydroBlue
import com.hydroping.app.ui.theme.HydroCyan
import com.hydroping.app.ui.theme.OceanCard
import com.hydroping.app.ui.theme.OceanDeep
import com.hydroping.app.ui.theme.TextMuted
import com.hydroping.app.ui.theme.TextPrimary
import com.hydroping.app.ui.theme.TextSecondary
import com.hydroping.app.ui.theme.UrgentOrange

@Composable
fun CustomNumberInputDialog(
    title: String,
    subtitle: String,
    initialValue: Int,
    unitSuffix: String,
    minValue: Int = 1,
    maxValue: Int = 10000,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf(initialValue.toString()) }
    val parsed = textValue.toIntOrNull()
    val isValid = parsed != null && parsed in minValue..maxValue

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(OceanCard)
                .border(1.dp, HydroCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(HydroBlue.copy(alpha = 0.3f))
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = HydroCyan)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 6) {
                            textValue = input
                        }
                    },
                    suffix = { Text(unitSuffix, color = HydroCyan, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                if (!isValid && textValue.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Must be between $minValue and $maxValue $unitSuffix",
                        fontSize = 11.sp,
                        color = UrgentOrange
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            parsed?.let { onConfirm(it) }
                        },
                        enabled = isValid,
                        colors = ButtonDefaults.buttonColors(containerColor = HydroCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomFlexibleTimePickerDialog(
    title: String,
    subtitle: String,
    initialHour: Int,
    initialMinute: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialHour.coerceIn(0, 23)) }
    var selectedMinute by remember { mutableStateOf(initialMinute.coerceIn(0, 59)) }
    var isAm by remember { mutableStateOf(initialHour < 12) }

    val displayHour = when {
        selectedHour == 0 -> 12
        selectedHour > 12 -> selectedHour - 12
        else -> selectedHour
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(OceanCard)
                .border(1.dp, HydroCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(HydroBlue.copy(alpha = 0.3f))
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = HydroCyan)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Time Display Box
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(OceanDeep)
                        .border(1.dp, HydroBlue, RoundedCornerShape(14.dp))
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = String.format("%02d:%02d %s", displayHour, selectedMinute, if (isAm) "AM" else "PM"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HydroCyan
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hour Adjuster
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hour", fontSize = 13.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = {
                                val next = if (selectedHour == 0) 23 else selectedHour - 1
                                selectedHour = next
                                isAm = next < 12
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("-", fontSize = 16.sp, color = HydroCyan) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(String.format("%02d", displayHour), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                val next = (selectedHour + 1) % 24
                                selectedHour = next
                                isAm = next < 12
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("+", fontSize = 16.sp, color = HydroCyan) }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Minute Adjuster (15m step intervals)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Minute", fontSize = 13.sp, color = TextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = {
                                selectedMinute = if (selectedMinute == 0) 45 else (selectedMinute - 15).coerceAtLeast(0)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("-", fontSize = 16.sp, color = HydroCyan) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(String.format("%02d", selectedMinute), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                selectedMinute = (selectedMinute + 15) % 60
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) { Text("+", fontSize = 16.sp, color = HydroCyan) }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // AM / PM Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Period", fontSize = 13.sp, color = TextSecondary)
                    Row {
                        Button(
                            onClick = {
                                if (!isAm) {
                                    isAm = true
                                    selectedHour = (selectedHour - 12).coerceAtLeast(0)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAm) HydroCyan else OceanDeep
                            ),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        ) {
                            Text("AM", color = if (isAm) Color.Black else TextMuted, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                if (isAm) {
                                    isAm = false
                                    selectedHour = (selectedHour + 12).coerceAtMost(23)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isAm) HydroCyan else OceanDeep
                            ),
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        ) {
                            Text("PM", color = if (!isAm) Color.Black else TextMuted, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onConfirm(selectedHour, selectedMinute) },
                        colors = ButtonDefaults.buttonColors(containerColor = HydroCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Time", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
