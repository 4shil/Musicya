package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.ThemeMode
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onEqualizerClick: () -> Unit = {}
) {
    var sleepTimerEnabled by remember { mutableStateOf(false) }
    var sleepTimerMinutes by remember { mutableStateOf(30) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    val currentTheme by viewModel.themeMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
         Spacer(modifier = Modifier.height(24.dp))
        
        // Header: SYSTEM
        Row(verticalAlignment = Alignment.CenterVertically) {
             ArtisticButton(
                onClick = onBack,
                icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "SYSTEM",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Equalizer
            SettingsItem(
                title = "EQUALIZER",
                subtitle = "ADJUST FREQUENCIES",
                icon = Icons.Default.GraphicEq,
                onClick = onEqualizerClick
            )

            // Sleep Timer
            SettingsItem(
                title = "SLEEP TIMER",
                subtitle = if (sleepTimerEnabled) "$sleepTimerMinutes MIN REMAINING" else "DISABLED",
                icon = Icons.Default.Timer,
                onClick = { showSleepTimerDialog = true },
                trailingContent = {
                    Switch(
                        checked = sleepTimerEnabled,
                        onCheckedChange = { sleepTimerEnabled = it },
                         colors = SwitchDefaults.colors(
                            checkedThumbColor = MangaRed,
                            checkedTrackColor = PureBlack,
                            uncheckedThumbColor = PureBlack,
                            uncheckedTrackColor = Color.LightGray,
                            uncheckedBorderColor = PureBlack
                        )
                    )
                }
            )

            // Theme
            SettingsItem(
                title = "THEME MODE",
                subtitle = when (currentTheme) {
                    ThemeMode.SYSTEM -> "SYSTEM DEFAULT"
                    ThemeMode.LIGHT -> "LIGHT MODE"
                    ThemeMode.DARK -> "DARK MODE"
                },
                icon = Icons.Default.Palette,
                onClick = { showThemeDialog = true }
            )

            // About
            SettingsItem(
                title = "ABOUT LYRA",
                subtitle = "VERSION 1.0.0 // STABLE",
                icon = Icons.Default.Info,
                onClick = {}
            )
        }
    }

    // Sleep Timer Dialog
    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = { Text("TIMER DURATION", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    listOf(15, 30, 45, 60, 90).forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    sleepTimerMinutes = minutes
                                    sleepTimerEnabled = true
                                    showSleepTimerDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = sleepTimerMinutes == minutes && sleepTimerEnabled,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = MangaRed, unselectedColor = PureBlack)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$minutes MINUTES", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    sleepTimerEnabled = false
                    showSleepTimerDialog = false 
                }) {
                    Text("DISABLE", color = MangaRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("CANCEL", color = PureBlack)
                }
            },
            containerColor = Color.White,
            shape = MaterialTheme.shapes.small
        )
    }
    
    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("SELECT THEME", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentTheme == mode,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = MangaRed, unselectedColor = PureBlack)
                            )
                             Spacer(modifier = Modifier.width(8.dp))
                            Text(when (mode) {
                                ThemeMode.SYSTEM -> "SYSTEM DEFAULT"
                                ThemeMode.LIGHT -> "LIGHT MODE"
                                ThemeMode.DARK -> "DARK MODE"
                            }, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("CANCEL", color = PureBlack)
                }
            },
            containerColor = Color.White,
             shape = MaterialTheme.shapes.small
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ArtisticCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, PureBlack)
                    .background(PureBlack.copy(alpha=0.05f)),
                contentAlignment = Alignment.Center
            ) {
                 Icon(icon, null, tint = PureBlack)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}
