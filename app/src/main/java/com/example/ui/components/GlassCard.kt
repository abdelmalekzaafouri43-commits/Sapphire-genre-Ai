package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkGlass
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GlassWhiteBorder
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.SapphireSecondary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    useGradientBorder: Boolean = false,
    cornerRadius: Dp = 20.dp,
    padding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(cornerRadius)

    val bgColor = if (isDark) DarkGlass else GlassWhite
    val borderColor = if (isDark) DarkGlassBorder else GlassWhiteBorder

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            SapphirePrimary,
            Color(0xFF818CF8),
            SapphireSecondary
        )
    )

    val shadowColor = if (isDark) Color(0x66000000) else Color(0x1F0F52BA)

    if (useGradientBorder) {
        // Glowing gradient border wrapper
        Box(
            modifier = modifier
                .shadow(elevation = 12.dp, shape = shape, spotColor = shadowColor, ambientColor = shadowColor)
                .background(brush = gradientBrush, shape = shape)
                .padding(1.5.dp) // border thickness
                .clip(shape)
                .background(bgColor)
                .padding(padding),
            content = content
        )
    } else {
        Box(
            modifier = modifier
                .shadow(elevation = 6.dp, shape = shape, spotColor = shadowColor, ambientColor = shadowColor)
                .clip(shape)
                .background(bgColor)
                .border(width = 1.dp, color = borderColor, shape = shape)
                .padding(padding),
            content = content
        )
    }
}
