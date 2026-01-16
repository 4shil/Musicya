package com.fourshil.musicya.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack
import com.fourshil.musicya.ui.theme.PureWhite

data class ArtisticNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun ArtisticBottomNavigation(
    items: List<ArtisticNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val backgroundColor = if (isDark) MaterialTheme.colorScheme.surface else PureWhite
    val contentColor = if (isDark) PureWhite else PureBlack
    
    // Main Container with Heavy Border and Shadow
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .offset(x = 10.dp, y = 10.dp)
                .background(PureBlack)
        )

        // Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(4.dp, PureBlack)
                .background(backgroundColor),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                ArtisticNavButton(
                    item = item,
                    selected = selected,
                    onClick = { onItemClick(item.route) },
                    contentColor = contentColor
                )
            }
        }
    }
}

@Composable
private fun ArtisticNavButton(
    item: ArtisticNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    contentColor: Color
) {
    val offsetY by animateDpAsState(if (selected) (-8).dp else 0.dp, label = "jump")
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(y = offsetY)
            .clickable(
                indication = null, 
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(8.dp)
    ) {

        
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(28.dp),
            tint = if (selected) contentColor else contentColor.copy(alpha = 0.4f)
        )
        
        if (selected) {
            Text(
                text = item.label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = contentColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
