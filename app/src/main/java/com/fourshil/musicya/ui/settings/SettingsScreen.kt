package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.ThemeMode
import com.fourshil.musicya.player.PlayerController
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate700
import com.fourshil.musicya.ui.theme.Slate900
import com.fourshil.musicya.ui.theme.NeoShadowLight

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    playerController: PlayerController,
    onBack: () -> Unit = {},
    onEqualizerClick: () -> Unit = {}
) {
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    val currentTheme by viewModel.themeMode.collectAsState()
    val sleepTimerRemaining by playerController.sleepTimerRemaining.collectAsState()
    val scrollState = rememberScrollState()
    
    // Convert remaining ms to minutes for display
    val sleepTimerMinutes = (sleepTimerRemaining / 60000).toInt()
    val sleepTimerActive = sleepTimerRemaining > 0

    // Determine content color based on theme
    val isDark = when (currentTheme) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val contentColor = if (isDark) Slate50 else Slate900
    val surfaceColor = if (isDark) Slate900 else Color.White

    Box(modifier = Modifier.fillMaxSize().background(surfaceColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
             Spacer(modifier = Modifier.height(24.dp))
            
            // --- HEADER ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                 ArtisticButton(
                    onClick = onBack,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back", tint = contentColor) },
                    modifier = Modifier.size(56.dp),
                    backgroundColor = surfaceColor,

                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- AUDIO SECTION ---
            SettingsSectionHeader("Audio", contentColor)
            SettingsItem(
                title = "Equalizer",
                subtitle = "Adjust audio frequencies",
                icon = Icons.Default.GraphicEq,
                onClick = onEqualizerClick,
                contentColor = contentColor,
                borderColor = contentColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Playback Speed
            val currentSpeed by playerController.playbackSpeed.collectAsState()
            SettingsItem(
                title = "Playback Speed",
                subtitle = if (currentSpeed == 1.0f) "Normal" else String.format("%.2fx", currentSpeed),
                icon = Icons.Default.Speed,
                onClick = { playerController.cyclePlaybackSpeed() },
                contentColor = contentColor,
                borderColor = contentColor,
                trailingContent = {
                    Text(
                        text = String.format("%.1fx", currentSpeed),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = if (currentSpeed != 1.0f) NeoCoral else contentColor.copy(alpha = 0.6f)
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Crossfade
            var showCrossfadeDialog by remember { mutableStateOf(false) }
            val crossfadeDuration by viewModel.crossfadeDuration.collectAsState()
            SettingsItem(
                title = "Crossfade",
                subtitle = if (crossfadeDuration == 0) "Off" else "$crossfadeDuration seconds",
                icon = Icons.Default.SwapHoriz,
                onClick = { showCrossfadeDialog = true },
                contentColor = contentColor,
                borderColor = contentColor,
                trailingContent = if (crossfadeDuration > 0) {
                    {
                        Text(
                            text = "${crossfadeDuration}s",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = NeoCoral
                        )
                    }
                } else null
            )
            
            // Crossfade Dialog
            if (showCrossfadeDialog) {
                NeoDialogWrapper(
                    title = "CROSSFADE",
                    onDismiss = { showCrossfadeDialog = false },
                    contentColor = contentColor,
                    surfaceColor = surfaceColor
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0, 2, 5, 8, 10, 12).forEach { seconds ->
                            NeoSelectionItem(
                                text = if (seconds == 0) "OFF" else "$seconds SECONDS",
                                selected = crossfadeDuration == seconds,
                                contentColor = contentColor,
                                surfaceColor = surfaceColor,
                                onClick = {
                                    viewModel.setCrossfadeDuration(seconds)
                                    showCrossfadeDialog = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PREFERENCES SECTION ---
            SettingsSectionHeader("Preferences", contentColor)
            
            // Sleep Timer - Connected to PlayerController
            SettingsItem(
                title = "Sleep Timer",
                subtitle = if (sleepTimerActive) "$sleepTimerMinutes min remaining" else "Off",
                icon = Icons.Default.Timer,
                onClick = { showSleepTimerDialog = true },
                contentColor = contentColor,
                borderColor = contentColor,
                trailingContent = if (sleepTimerActive) {
                    {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Timer active",
                            tint = NeoCoral,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else null
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Theme
            SettingsItem(
                title = "Theme",
                subtitle = when (currentTheme) {
                    ThemeMode.SYSTEM -> "System default"
                    ThemeMode.LIGHT -> "Light mode"
                    ThemeMode.DARK -> "Dark mode"
                },
                icon = Icons.Default.Palette,
                onClick = { showThemeDialog = true },
                contentColor = contentColor,
                borderColor = contentColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- INFO SECTION ---
            SettingsSectionHeader("Info", contentColor)
            SettingsItem(
                title = "About Musicya",
                subtitle = "Version 1.0.0",
                icon = Icons.Default.Info,
                onClick = {},
                contentColor = contentColor,
                borderColor = contentColor
            )
             
             Spacer(modifier = Modifier.height(48.dp))
        }

        // --- CUSTOM DIALOGS ---
        if (showSleepTimerDialog) {
            NeoDialogWrapper(
                title = "SLEEP TIMER",
                onDismiss = { showSleepTimerDialog = false },
                contentColor = contentColor,
                surfaceColor = surfaceColor
            ) {
                 Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15, 30, 45, 60).forEach { minutes ->
                        NeoSelectionItem(
                            text = if (minutes == 60) "1 HOUR" else "$minutes MINUTES",
                            selected = sleepTimerActive && sleepTimerMinutes == minutes,
                            contentColor = contentColor,
                            surfaceColor = surfaceColor,
                            onClick = {
                                playerController.setSleepTimer(minutes)
                                showSleepTimerDialog = false
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                     ArtisticButton(
                        onClick = { 
                            playerController.cancelSleepTimer()
                            showSleepTimerDialog = false 
                        },
                        text = if (sleepTimerActive) "CANCEL TIMER" else "CLOSE",
                        backgroundColor = contentColor,
                        contentColor = surfaceColor,
                         modifier = Modifier.fillMaxWidth()
                    )
                 }
            }
        }
        
        if (showThemeDialog) {
             NeoDialogWrapper(
                title = "SELECT THEME",
                onDismiss = { showThemeDialog = false },
                contentColor = contentColor,
                surfaceColor = surfaceColor
            ) {
                 Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                         NeoSelectionItem(
                            text = when (mode) {
                                ThemeMode.SYSTEM -> "SYSTEM DEFAULT"
                                ThemeMode.LIGHT -> "LIGHT MODE"
                                ThemeMode.DARK -> "DARK MODE"
                            },
                            selected = currentTheme == mode,
                            contentColor = contentColor,
                            surfaceColor = surfaceColor,
                            onClick = {
                                viewModel.setThemeMode(mode)
                                showThemeDialog = false
                            }
                        )
                    }
                 }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        ),
        color = color.copy(alpha = 0.5f),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentColor: Color = Slate900,
    borderColor: Color = Slate700,
    trailingContent: @Composable (() -> Unit)? = null
) {
    // Invert the colors for the icon box to make it pop
    val iconBgColor = contentColor.copy(alpha = 0.1f)
    
    ArtisticCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Transparent, // Transparent because we rely on the main background
        borderColor = borderColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, borderColor)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                 Icon(icon, null, modifier = Modifier.size(24.dp), tint = contentColor)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = contentColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
            
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

@Composable
fun NeoDialogWrapper(
    title: String,
    onDismiss: () -> Unit,
    contentColor: Color = Slate900,
    surfaceColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(NeoDimens.BorderMedium, contentColor)
                .background(surfaceColor)
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = contentColor
                    )
                    Icon(
                        Icons.Default.Close, 
                        null, 
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                            .size(24.dp),
                        tint = contentColor
                    )
                }
                HorizontalDivider(
                    thickness = 4.dp, 
                    color = contentColor,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                content()
            }
        }
    }
}

@Composable
fun NeoSelectionItem(
    text: String,
    selected: Boolean,
    contentColor: Color,
    surfaceColor: Color,
    onClick: () -> Unit
) {
    // If selected, we INVERT the colors (Background = ContentColor, Text = SurfaceColor)
    val bgColor = if (selected) contentColor else Color.Transparent
    val textColor = if (selected) surfaceColor else contentColor
    val borderW = if (selected) 2.dp else 0.dp
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderW, contentColor)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected) {
             Icon(Icons.Default.Check, null, tint = textColor, modifier = Modifier.size(20.dp))
             Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
             color = textColor
        )
    }
}

