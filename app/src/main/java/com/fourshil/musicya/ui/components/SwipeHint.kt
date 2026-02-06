package com.fourshil.musicya.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.theme.NeoDimens
import kotlinx.coroutines.delay

/**
 * Shows a hint overlay for first-time users about available gestures.
 */
@Composable
fun SwipeHint(
    message: String,
    modifier: Modifier = Modifier,
    autoDismissMs: Long = 3000,
    onDismiss: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(autoDismissMs)
        visible = false
        delay(300) // Wait for fade animation
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it },
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 4.dp,
            modifier = Modifier.padding(NeoDimens.SpacingM)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = NeoDimens.SpacingM, vertical = NeoDimens.SpacingS),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(NeoDimens.SpacingS)
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Hint manager to show contextual hints.
 */
object HintManager {
    private val _shownHints = mutableSetOf<String>()
    
    /**
     * Check if a hint has been shown before.
     */
    fun hasBeenShown(hintId: String): Boolean = _shownHints.contains(hintId)
    
    /**
     * Mark a hint as shown.
     */
    fun markShown(hintId: String) { _shownHints.add(hintId) }
    
    /**
     * Reset all shown hints (for testing).
     */
    fun reset() { _shownHints.clear() }
}