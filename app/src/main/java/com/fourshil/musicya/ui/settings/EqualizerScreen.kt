package com.fourshil.musicya.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    viewModel: EqualizerViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val isEnabled by viewModel.isEnabled.collectAsState()
    val bands by viewModel.bands.collectAsState()
    val bassLevel by viewModel.bassLevel.collectAsState()
    val virtualizerLevel by viewModel.virtualizerLevel.collectAsState()
    val presets by viewModel.presets.collectAsState()
    val currentPreset by viewModel.currentPreset.collectAsState()
    val isInitialized by viewModel.isInitialized.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
         Spacer(modifier = Modifier.height(24.dp))
        
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
             Row(verticalAlignment = Alignment.CenterVertically) {
                ArtisticButton(
                    onClick = onBack,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = PureBlack) },
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "EQ/FX",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    ),
                    color = PureBlack
                )
            }
            
            // Toggle Switch
             Switch(
                checked = isEnabled,
                onCheckedChange = { viewModel.toggleEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MangaRed,
                    checkedTrackColor = PureBlack,
                    uncheckedThumbColor = PureBlack,
                    uncheckedTrackColor = Color.LightGray,
                    uncheckedBorderColor = PureBlack
                )
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        if (!isInitialized) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                 Text("AUDIO FX UNAVAILABLE", style = MaterialTheme.typography.headlineMedium)
            }
        } else {
             // Presets
            if (presets.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    ArtisticButton(
                        onClick = { expanded = true },
                        text = if (currentPreset >= 0) presets[currentPreset].uppercase() else "CUSTOM",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White).border(2.dp, PureBlack)
                    ) {
                        presets.forEachIndexed { index, preset ->
                            DropdownMenuItem(
                                text = { Text(preset.uppercase(), fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.setPreset(index)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Bands Container
            ArtisticCard(
                onClick = null,
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    bands.forEach { band ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween 
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .width(40.dp)
                            ) {
                                Slider(
                                    value = band.level.toFloat(),
                                    onValueChange = { viewModel.setBandLevel(band.index, it.toInt()) },
                                    valueRange = band.minLevel.toFloat()..band.maxLevel.toFloat(),
                                    enabled = isEnabled,
                                    modifier = Modifier
                                        .graphicsLayer { rotationZ = 270f }
                                        .width(200.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = MangaRed,
                                        activeTrackColor = PureBlack,
                                        inactiveTrackColor = Color.LightGray
                                    )
                                )
                            }
                            
                            Text(
                                text = formatFreq(band.centerFreq),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bass & Virtualizer
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FXSlider(
                    label = "BASS BOOST",
                    value = bassLevel.toFloat(),
                    onValueChange = { viewModel.setBassLevel(it.toInt()) },
                    enabled = isEnabled
                )
                FXSlider(
                   label = "VIRTUALIZER",
                   value = virtualizerLevel.toFloat(),
                   onValueChange = { viewModel.setVirtualizerLevel(it.toInt()) },
                   enabled = isEnabled
                )
            }
        }
    }
}

@Composable
fun FXSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    enabled: Boolean
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1000f,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MangaRed,
                activeTrackColor = PureBlack,
                inactiveTrackColor = Color.LightGray
            )
        )
    }
}

private fun formatFreq(mHz: Int): String {
    return if (mHz < 1000000) "${mHz / 1000}Hz" else "${mHz / 1000000}kHz"
}
