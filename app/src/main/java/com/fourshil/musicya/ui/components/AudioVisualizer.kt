package com.fourshil.musicya.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Audio visualizer bar animation component.
 * Shows animated bars that respond to simulated audio levels.
 */
@Composable
fun AudioVisualizerBars(
    modifier: Modifier = Modifier,
    barCount: Int = 32,
    primaryColor: Color = Color.White,
    secondaryColor: Color = Color.White.copy(alpha = 0.5f),
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")
    
    // Create random offsets for each bar
    val barPhases = remember { List(barCount) { Random.nextFloat() * 2f * Math.PI.toFloat() } }
    
    // Animate the phase continuously
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    
    // Create animated heights based on sine waves
    val barHeights = remember(phase, isPlaying) {
        if (isPlaying) {
            (0 until barCount).map { i ->
                val baseHeight = sin(phase + barPhases[i]).toFloat() * 0.3f + 0.5f
                val secondary = sin(phase * 1.5f + barPhases[i] * 2f).toFloat() * 0.2f + 0.3f
                (baseHeight + secondary).coerceIn(0.1f, 0.95f)
            }
        } else {
            List(barCount) { 0.1f }
        }
    }

    Canvas(modifier = modifier) {
        val barWidth = size.width / barCount
        val spacing = barWidth * 0.2f
        val actualBarWidth = barWidth - spacing
        
        barHeights.forEachIndexed { index, heightFraction ->
            val barHeight = size.height * heightFraction
            val x = index * barWidth + spacing / 2
            val y = size.height - barHeight
            
            val color = if (index % 3 == 0) primaryColor else secondaryColor
            
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(actualBarWidth, barHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}

/**
 * Simple visualizer with fewer bars for mini player.
 */
@Composable
fun MiniVisualizerBars(
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    color: Color = Color.White,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "miniVisualizer")
    
    val barPhases = remember { List(barCount) { Random.nextFloat() * 2f * Math.PI.toFloat() } }
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val barHeights = remember(phase, isPlaying) {
        if (isPlaying) {
            (0 until barCount).map { i ->
                val h = sin(phase + barPhases[i]).toFloat() * 0.4f + 0.6f
                h.coerceIn(0.2f, 1f)
            }
        } else {
            List(barCount) { 0.2f }
        }
    }

    Canvas(modifier = modifier) {
        val barWidth = size.width / (barCount * 2)
        val spacing = barWidth
        
        barHeights.forEachIndexed { index, heightFraction ->
            val barHeight = size.height * heightFraction
            val x = index * (barWidth * 2) + spacing / 2
            val y = (size.height - barHeight) / 2
            
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
            )
        }
    }
}