package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.SapphireSecondary

@Composable
fun StorageProgressBar(
    percentage: Float = 0.85f,
    usedStorage: String = "8.5 GB",
    totalStorage: String = "10 GB",
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        padding = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STORAGE CAPACITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SapphirePrimary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${(percentage * 100).toInt()}% ($usedStorage / $totalStorage)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val progressGradient = Brush.horizontalGradient(
                colors = listOf(SapphirePrimary, SapphireSecondary, Color(0xFF00D2FF))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = percentage.coerceIn(0f, 1f))
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(brush = progressGradient)
                )
            }
        }
    }
}
