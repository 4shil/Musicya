package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.theme.Slate400

/**
 * Neo-Brutalism halftone/dot pattern background
 * Creates a subtle tiled dot pattern for visual interest
 * Kept very light (0.05 alpha) for professional appearance
 */
@Composable
fun HalftoneBackground(
    modifier: Modifier = Modifier,
    color: Color = Slate400,
    dotSize: Float = 1.5f,  // Smaller dots for subtlety
    spacing: Float = 10f,
    alpha: Float = 0.05f    // Very subtle by default
) {
    val paintColor = color.copy(alpha = alpha)
    val density = LocalDensity.current

    // Cache the brush to avoid recreation on every recomposition
    val brush = remember(color, dotSize, spacing, density, alpha) {
        val sizePx = with(density) { spacing.dp.toPx() }.toInt().coerceAtLeast(1)
        val dotPx = with(density) { dotSize.dp.toPx() }
        
        val bitmap = ImageBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            this.color = paintColor
            isAntiAlias = true
        }
        
        // Draw centered dot to be tiled
        canvas.drawCircle(Offset(sizePx / 2f, sizePx / 2f), dotPx / 2f, paint)
        
        ShaderBrush(
            androidx.compose.ui.graphics.ImageShader(
                bitmap, 
                TileMode.Repeated, 
                TileMode.Repeated
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    )
}

