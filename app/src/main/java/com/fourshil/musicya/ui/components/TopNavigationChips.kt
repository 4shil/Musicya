package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack
import com.fourshil.musicya.ui.theme.PureWhite

data class TopNavItem(
    val route: String,
    val label: String
)

@Composable
fun TopNavigationChips(
    items: List<TopNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            val isSelected = currentRoute == item.route
            TopNavChip(
                label = item.label,
                isSelected = isSelected,
                onClick = { onItemClick(item.route) }
            )
        }
    }
}

@Composable
private fun TopNavChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MangaRed else PureWhite
    val contentColor = if (isSelected) PureWhite else PureBlack
    val borderColor = PureBlack

    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(2.dp, borderColor, RoundedCornerShape(50))
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = contentColor
        )
    }
}
