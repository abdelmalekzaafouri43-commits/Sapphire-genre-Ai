package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTheme
import com.example.ui.theme.DarkGlass
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.SapphireSecondary

enum class NavDestination(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    WORKSHEET("Worksheet Generator", Icons.Default.Description),
    PPT("PPT Generator", Icons.Default.Slideshow),
    DOCUMENTS("My Documents", Icons.Default.Folder),
    ANALYTICS("Analytics", Icons.Default.Analytics)
}

@Composable
fun SidebarContent(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    currentTheme: AppTheme = AppTheme.BRIGHT_SAPPHIRE,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DarkGlass else GlassWhite

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(bgColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // 1. Branding Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(SapphirePrimary, SapphireSecondary)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Sapphire AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Content Engine",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SapphirePrimary
                    )
                }
            }

            // 2. User Profile Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                padding = 12.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(SapphirePrimary, SapphireSecondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JD",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "John Doe",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pro Tier Member",
                            fontSize = 10.sp,
                            color = SapphirePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    StatusBadge(text = "PRO", isPro = true)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Navigation Links
            Text(
                text = "NAVIGATE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            NavDestination.values().forEach { destination ->
                val isSelected = destination == currentDestination
                val itemBgColor = if (isSelected) {
                    SapphirePrimary.copy(alpha = 0.12f)
                } else {
                    Color.Transparent
                }
                val itemTextColor = if (isSelected) {
                    SapphirePrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
                val itemIconColor = if (isSelected) {
                    SapphirePrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(itemBgColor)
                        .clickable { onNavigate(destination) }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                        tint = itemIconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = destination.label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = itemTextColor
                    )
                }
            }
        }

        // Bottom Section: Storage Progress Bar & Dark Mode Switch
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StorageProgressBar(percentage = 0.85f, usedStorage = "8.5 GB", totalStorage = "10 GB")

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                padding = 10.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Dark mode",
                            tint = SapphirePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentTheme.title} (${if (isDarkMode) "Dark" else "Light"})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SapphirePrimary,
                            uncheckedThumbColor = SapphirePrimary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            // Creator & Copyright Notice Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Mr. Zaafouri Abdelmalek",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "© 2026 All rights reserved",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
