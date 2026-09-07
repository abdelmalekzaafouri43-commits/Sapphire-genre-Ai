package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTheme

/**
 * ThemeSelectorCard Composable
 *
 * Displays selectable color theme chips for Midnight Cobalt, Golden Taupe,
 * Mossy Hollow, Periwinkle Dream, Creme Brulee, Neon Glam, Carbon Dusk, and Bright Sapphire.
 */
@Composable
fun ThemeSelectorCard(
    currentTheme: AppTheme,
    onSelectTheme: (AppTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        useGradientBorder = true,
        cornerRadius = 20.dp,
        padding = 18.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme Palette",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Color Themes & Visual Identity",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Active: ${currentTheme.title}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(text = currentTheme.title.uppercase(), isPro = false)
            }

            // Theme Selection Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AppTheme.values()) { theme ->
                    val isSelected = theme == currentTheme
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        label = "BorderColor"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectTheme(theme) }
                            .testTag("theme_chip_${theme.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Theme Color Swatches (Primary + Background)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-6).dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(theme.primaryColor, CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(theme.backgroundColor, CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape)
                                )
                            }

                            Column {
                                Text(
                                    text = theme.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = theme.description,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
