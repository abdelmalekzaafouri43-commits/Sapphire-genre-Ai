package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import com.example.ui.components.ApiKeyBoxCard
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.StorageProgressBar
import com.example.ui.components.ThemeSelectorCard
import com.example.ui.theme.AppTheme
import com.example.ui.theme.SapphireAccent
import com.example.ui.theme.SapphirePrimary

@Composable
fun AnalyticsScreen(
    docs: List<GeneratedDoc>,
    apiKey: String = "",
    isTestingKey: Boolean = false,
    apiKeyTestResult: String? = null,
    isKeyValid: Boolean? = null,
    onApiKeyChange: (String) -> Unit = {},
    onTestKey: (String) -> Unit = {},
    currentTheme: AppTheme = AppTheme.BRIGHT_SAPPHIRE,
    onSelectTheme: (AppTheme) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val worksheetCount = docs.count { it.docType == DocType.WORKSHEET }
    val pptCount = docs.count { it.docType == DocType.PRESENTATION }
    val totalGbUsed = (docs.size * 0.15).coerceAtLeast(1.2)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ThemeSelectorCard(
            currentTheme = currentTheme,
            onSelectTheme = onSelectTheme
        )

        ApiKeyBoxCard(
            apiKey = apiKey,
            isTestingKey = isTestingKey,
            testResult = apiKeyTestResult,
            isKeyValid = isKeyValid,
            onApiKeyChange = onApiKeyChange,
            onTestKey = onTestKey
        )
        Text(
            text = "Analytics & System Storage",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Usage statistics and storage capacity tracking",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        StorageProgressBar(
            percentage = (totalGbUsed / 10.0).toFloat(),
            usedStorage = "${String.format("%.1f", totalGbUsed)} GB",
            totalStorage = "10 GB",
            modifier = Modifier.fillMaxWidth()
        )

        // Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Worksheets Metric
            GlassCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SapphirePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Worksheets",
                            tint = SapphirePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$worksheetCount",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Worksheets Built",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Presentations Metric
            GlassCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SapphireAccent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Slideshow,
                            contentDescription = "Presentations",
                            tint = SapphireAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$pptCount",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "PPT Decks Built",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // AI Acceleration Status Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SapphirePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Speed",
                            tint = SapphirePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Gemini 2.5 Flash Engine",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Sub-second content response time",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                StatusBadge(text = "AI Ready")
            }
        }

        // Copyright Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Mr. Zaafouri Abdelmalek",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "© 2026 All rights reserved",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
