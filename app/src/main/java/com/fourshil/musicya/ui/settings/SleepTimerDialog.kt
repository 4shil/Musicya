package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.util.SleepTimer

/**
 * Sleep timer configuration dialog with preset durations and custom input.
 */
@Composable
fun SleepTimerDialog(
    currentMinutes: Int,
    isActive: Boolean,
    onSetTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(currentMinutes.takeIf { it > 0 } ?: 15) }
    var isCustom by remember { mutableStateOf(false) }
    var customText by remember { mutableStateOf("") }

    val presets = listOf(
        5 to "5 minutes",
        10 to "10 minutes",
        15 to "15 minutes",
        30 to "30 minutes",
        45 to "45 minutes",
        60 to "1 hour",
        90 to "1.5 hours",
        120 to "2 hours"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sleep Timer")
            }
        },
        text = {
            Column {
                if (isActive) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Timer active: $currentMinutes min remaining",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "Stop playback after:",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Preset options
                presets.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { (minutes, label) ->
                            FilterChip(
                                selected = selectedMinutes == minutes && !isCustom,
                                onClick = {
                                    selectedMinutes = minutes
                                    isCustom = false
                                },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom input
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = isCustom,
                        onClick = { isCustom = true },
                        label = { Text("Custom") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = customText,
                        onValueChange = { newVal ->
                            customText = newVal.filter { it.isDigit() }
                            customText.toIntOrNull()?.let { selectedMinutes = it }
                            isCustom = true
                        },
                        label = { Text("Minutes") },
                        modifier = Modifier.width(120.dp),
                        singleLine = true,
                        enabled = isCustom
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // End of track option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedMinutes == 0,
                            onClick = { selectedMinutes = 0 },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedMinutes == 0,
                        onClick = { selectedMinutes = 0 }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("End of current track")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSetTimer(selectedMinutes) },
                enabled = selectedMinutes >= 0
            ) {
                Text(if (isActive) "Update" else "Start")
            }
        },
        dismissButton = {
            if (isActive) {
                TextButton(onClick = onCancelTimer) {
                    Text("Cancel Timer")
                }
            }
        }
    )
}