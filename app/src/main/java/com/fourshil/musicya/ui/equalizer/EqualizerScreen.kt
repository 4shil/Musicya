package com.fourshil.musicya.ui.equalizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Equalizer presets and custom curve editor.
 */
data class EqualizerPreset(
    val name: String,
    val bands: List<Float> // -12dB to +12dB for each band
)

object EqualizerPresets {
    val FLAT = EqualizerPreset("Flat", listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
    val BASS_BOOST = EqualizerPreset("Bass Boost", listOf(6f, 4f, 2f, 0f, 0f, 0f, 0f, 0f))
    val TREBLE_BOOST = EqualizerPreset("Treble Boost", listOf(0f, 0f, 0f, 0f, 2f, 4f, 6f, 6f))
    val VOCAL = EqualizerPreset("Vocal", listOf(-2f, -1f, 0f, 3f, 4f, 3f, 0f, -1f))
    val ROCK = EqualizerPreset("Rock", listOf(5f, 3f, 1f, -1f, 0f, 2f, 4f, 5f))
    val JAZZ = EqualizerPreset("Jazz", listOf(3f, 1f, 0f, 2f, -1f, -1f, 1f, 3f))
    val CLASSICAL = EqualizerPreset("Classical", listOf(4f, 2f, 0f, 1f, 3f, 4f, 3f, 2f))
    val ELECTRONIC = EqualizerPreset("Electronic", listOf(5f, 4f, 1f, -1f, -2f, 1f, 4f, 5f))
    val HIP_HOP = EqualizerPreset("Hip Hop", listOf(6f, 4f, 1f, 2f, -1f, 1f, 2f, 4f))
    val POP = EqualizerPreset("Pop", listOf(-1f, 1f, 3f, 4f, 3f, 1f, -1f, -2f))

    val ALL = listOf(FLAT, BASS_BOOST, TREBLE_BOOST, VOCAL, ROCK, JAZZ, CLASSICAL, ELECTRONIC, HIP_HOP, POP)
}

@Composable
fun EqualizerScreen(
    currentPreset: EqualizerPreset,
    onPresetSelected: (EqualizerPreset) -> Unit,
    customBands: List<Float>,
    onBandChanged: (Int, Float) -> Unit,
    onReset: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Presets", "Custom")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> PresetsTab(
                presets = EqualizerPresets.ALL,
                currentPreset = currentPreset,
                onPresetSelected = onPresetSelected
            )
            1 -> CustomEqualizerTab(
                bands = customBands,
                onBandChanged = onBandChanged,
                onReset = onReset
            )
        }
    }
}

@Composable
private fun PresetsTab(
    presets: List<EqualizerPreset>,
    currentPreset: EqualizerPreset,
    onPresetSelected: (EqualizerPreset) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(NeoDimens.ScreenPadding)
    ) {
        itemsIndexed(presets) { index, preset ->
            ListItem(
                headlineContent = {
                    Text(
                        text = preset.name,
                        fontWeight = if (preset.name == currentPreset.name) FontWeight.Bold else FontWeight.Normal
                    )
                },
                leadingContent = {
                    if (preset.name == currentPreset.name) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.clickable { onPresetSelected(preset) }
            )
            if (index < presets.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CustomEqualizerTab(
    bands: List<Float>,
    onBandChanged: (Int, Float) -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NeoDimens.ScreenPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Custom EQ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onReset) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Band sliders
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bands.forEachIndexed { index, value ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${if (value > 0) "+" else ""}${value.toInt()}dB",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Slider(
                        value = value,
                        onValueChange = { onBandChanged(index, it) },
                        valueRange = -12f..12f,
                        modifier = Modifier.height(200.dp)
                    )
                    Text(
                        text = getBandLabel(index),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private fun getBandLabel(index: Int): String {
    return when (index) {
        0 -> "60Hz"
        1 -> "170Hz"
        2 -> "310Hz"
        3 -> "600Hz"
        4 -> "1kHz"
        5 -> "3kHz"
        6 -> "6kHz"
        7 -> "12kHz"
        else -> "${index + 1}"
    }
}