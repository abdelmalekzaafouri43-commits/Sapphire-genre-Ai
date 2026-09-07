package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BadgeAiBg
import com.example.ui.theme.BadgeAiText
import com.example.ui.theme.BadgeProBg
import com.example.ui.theme.BadgeProText

@Composable
fun StatusBadge(
    text: String,
    isPro: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isPro) BadgeProBg else BadgeAiBg
    val textColor = if (isPro) BadgeProText else BadgeAiText
    val borderColor = if (isPro) Color(0xFFFDE68A) else Color(0xFFC7D2FE)

    Row(
        modifier = modifier
            .background(color = bgColor, shape = CircleShape)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPro) Icons.Default.Star else Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}
