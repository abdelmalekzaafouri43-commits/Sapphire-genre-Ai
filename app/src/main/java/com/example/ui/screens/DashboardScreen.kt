package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import com.example.ui.components.ApiKeyBoxCard
import com.example.ui.components.GlassCard
import com.example.ui.components.NavDestination
import com.example.ui.components.StatusBadge
import com.example.ui.components.ThemeSelectorCard
import com.example.ui.theme.AppTheme
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.SapphireSecondary

@Composable
fun DashboardScreen(
    recentDocs: List<GeneratedDoc>,
    apiKey: String = "",
    isTestingKey: Boolean = false,
    apiKeyTestResult: String? = null,
    isKeyValid: Boolean? = null,
    onApiKeyChange: (String) -> Unit = {},
    onTestKey: (String) -> Unit = {},
    currentTheme: AppTheme = AppTheme.BRIGHT_SAPPHIRE,
    onSelectTheme: (AppTheme) -> Unit = {},
    onNavigate: (NavDestination) -> Unit,
    onSelectDoc: (GeneratedDoc) -> Unit,
    onToggleFavorite: (GeneratedDoc) -> Unit,
    onDeleteDoc: (GeneratedDoc) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Theme Selector Card
        item {
            ThemeSelectorCard(
                currentTheme = currentTheme,
                onSelectTheme = onSelectTheme
            )
        }

        // API Key Box & Test Card
        item {
            ApiKeyBoxCard(
                apiKey = apiKey,
                isTestingKey = isTestingKey,
                testResult = apiKeyTestResult,
                isKeyValid = isKeyValid,
                onApiKeyChange = onApiKeyChange,
                onTestKey = onTestKey
            )
        }

        // 1. Hero Workspace Banner
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                useGradientBorder = true,
                cornerRadius = 24.dp,
                padding = 20.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(text = "AI Ready", isPro = false)
                        StatusBadge(text = "Pro Feature", isPro = true)
                    }

                    Text(
                        text = "Automated Content Generator",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Instantly create customized educational Worksheets and professional PowerPoint slide decks powered by Gemini AI.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(NavDestination.WORKSHEET) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Worksheet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigate(NavDestination.PPT) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SapphireSecondary)
                        ) {
                            Icon(imageVector = Icons.Default.Slideshow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New PPT Slides", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Metrics Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Worksheets",
                    value = "${recentDocs.count { it.docType == DocType.WORKSHEET }}",
                    subtitle = "Generated",
                    icon = Icons.Default.Description,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "PPT Decks",
                    value = "${recentDocs.count { it.docType == DocType.PRESENTATION }}",
                    subtitle = "Created",
                    icon = Icons.Default.Slideshow,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Hours Saved",
                    value = "36 hrs",
                    subtitle = "Effort Saved",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Quick Action Cards (Worksheet & PPT Shortcuts)
        item {
            Text(
                text = "CREATE NEW ASSETS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SapphirePrimary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Worksheet Card Shortcut
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(NavDestination.WORKSHEET) },
                    cornerRadius = 20.dp,
                    padding = 16.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SapphirePrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = SapphirePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            StatusBadge(text = "AI READY", isPro = false)
                        }

                        Text(
                            text = "Worksheet Generator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Multi-choice, short answer & answer keys for all subjects.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // PPT Generator Card Shortcut
                GlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(NavDestination.PPT) },
                    cornerRadius = 20.dp,
                    padding = 16.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SapphireSecondary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Slideshow,
                                    contentDescription = null,
                                    tint = SapphireSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            StatusBadge(text = "PRO FEATURE", isPro = true)
                        }

                        Text(
                            text = "PPT Slides Designer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Slide decks with bullet points, visuals & speaker notes.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Recent Generation History
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT GENERATION HISTORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SapphirePrimary,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "View All (${recentDocs.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SapphirePrimary,
                    modifier = Modifier.clickable { onNavigate(NavDestination.DOCUMENTS) }
                )
            }
        }

        if (recentDocs.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    padding = 24.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = SapphirePrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No generated documents yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Generate your first Worksheet or PPT presentation above!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recentDocs.take(5)) { doc ->
                DocHistoryItem(
                    doc = doc,
                    onSelect = { onSelectDoc(doc) },
                    onToggleFav = { onToggleFavorite(doc) },
                    onDelete = { onDeleteDoc(doc) }
                )
            }
        }

        // Copyright Footer
        item {
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
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        padding = 12.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SapphirePrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = SapphirePrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DocHistoryItem(
    doc: GeneratedDoc,
    onSelect: () -> Unit,
    onToggleFav: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        cornerRadius = 16.dp,
        padding = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (doc.docType == DocType.WORKSHEET) SapphirePrimary.copy(alpha = 0.15f)
                            else SapphireSecondary.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (doc.docType == DocType.WORKSHEET) Icons.Default.Description else Icons.Default.Slideshow,
                        contentDescription = null,
                        tint = if (doc.docType == DocType.WORKSHEET) SapphirePrimary else SapphireSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = doc.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${doc.gradeOrAudience} • ${doc.slideCountOrQuestionCount} ${if (doc.docType == DocType.WORKSHEET) "Questions" else "Slides"} • ${doc.dateCreated}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFav, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (doc.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (doc.isFavorite) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
