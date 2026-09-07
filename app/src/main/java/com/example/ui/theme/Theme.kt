package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dynamically builds a Material 3 ColorScheme for any of the 8 AppTheme presets.
 */
fun getColorSchemeForAppTheme(appTheme: AppTheme, darkTheme: Boolean): ColorScheme {
    return when (appTheme) {
        AppTheme.BRIGHT_SAPPHIRE -> if (darkTheme) {
            darkColorScheme(
                primary = SapphirePrimaryLight,
                onPrimary = Color.White,
                primaryContainer = SapphirePrimaryContainerDark,
                onPrimaryContainer = SapphireOnPrimaryContainerDark,
                secondary = SapphireSecondaryLight,
                onSecondary = Color.White,
                tertiary = Color(0xFF38BDF8),
                background = SapphireDarkBackground,
                onBackground = TextPrimaryDark,
                surface = SapphireDarkSurface,
                onSurface = TextPrimaryDark,
                surfaceVariant = SapphireDarkSurfaceVariant,
                onSurfaceVariant = TextSecondaryDark,
                outline = SapphireDarkOutline,
                surfaceTint = SapphirePrimaryLight
            )
        } else {
            lightColorScheme(
                primary = SapphirePrimary,
                onPrimary = Color.White,
                primaryContainer = SapphirePrimaryContainerLight,
                onPrimaryContainer = SapphireOnPrimaryContainerLight,
                secondary = SapphireSecondary,
                onSecondary = Color.White,
                tertiary = SapphireTertiary,
                background = SapphireLightBackground,
                onBackground = TextPrimaryLight,
                surface = SapphireLightSurface,
                onSurface = TextPrimaryLight,
                surfaceVariant = SapphireLightSurfaceVariant,
                onSurfaceVariant = TextSecondaryLight,
                outline = SapphireLightOutline,
                surfaceTint = SapphirePrimary
            )
        }

        AppTheme.MIDNIGHT_COBALT -> if (darkTheme) {
            darkColorScheme(
                primary = CobaltPrimaryDark,
                onPrimary = Color(0xFF0F172A),
                secondary = Color(0xFF818CF8),
                background = CobaltBackgroundDark,
                onBackground = Color(0xFFF8FAFC),
                surface = CobaltSurfaceDark,
                onSurface = Color(0xFFF8FAFC),
                surfaceVariant = Color(0xFF1F2937),
                onSurfaceVariant = Color(0xFF9CA3AF),
                outline = Color(0xFF374151)
            )
        } else {
            lightColorScheme(
                primary = CobaltPrimary,
                onPrimary = Color.White,
                secondary = Color(0xFF4F46E5),
                background = CobaltBackgroundLight,
                onBackground = Color(0xFF0F172A),
                surface = CobaltSurfaceLight,
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFE0E7FF),
                onSurfaceVariant = Color(0xFF374151),
                outline = Color(0xFFC7D2FE)
            )
        }

        AppTheme.GOLDEN_TAUPE -> if (darkTheme) {
            darkColorScheme(
                primary = TaupePrimaryDark,
                onPrimary = Color(0xFF1C1917),
                secondary = Color(0xFFF59E0B),
                background = TaupeBackgroundDark,
                onBackground = Color(0xFFFAFAF9),
                surface = TaupeSurfaceDark,
                onSurface = Color(0xFFFAFAF9),
                surfaceVariant = Color(0xFF44403C),
                onSurfaceVariant = Color(0xFFA8A29E),
                outline = Color(0xFF57534E)
            )
        } else {
            lightColorScheme(
                primary = TaupePrimary,
                onPrimary = Color.White,
                secondary = Color(0xFFD97706),
                background = TaupeBackgroundLight,
                onBackground = Color(0xFF1C1917),
                surface = TaupeSurfaceLight,
                onSurface = Color(0xFF1C1917),
                surfaceVariant = Color(0xFFF5F5F4),
                onSurfaceVariant = Color(0xFF57534E),
                outline = Color(0xFFE7E5E4)
            )
        }

        AppTheme.MOSSY_HOLLOW -> if (darkTheme) {
            darkColorScheme(
                primary = MossPrimaryDark,
                onPrimary = Color(0xFF0A1A10),
                secondary = Color(0xFF34D399),
                background = MossBackgroundDark,
                onBackground = Color(0xFFF0FDF4),
                surface = MossSurfaceDark,
                onSurface = Color(0xFFF0FDF4),
                surfaceVariant = Color(0xFF1E3A2B),
                onSurfaceVariant = Color(0xFF86EFAC),
                outline = Color(0xFF274E38)
            )
        } else {
            lightColorScheme(
                primary = MossPrimary,
                onPrimary = Color.White,
                secondary = Color(0xFF059669),
                background = MossBackgroundLight,
                onBackground = Color(0xFF052E16),
                surface = MossSurfaceLight,
                onSurface = Color(0xFF052E16),
                surfaceVariant = Color(0xFFDCFCE7),
                onSurfaceVariant = Color(0xFF166534),
                outline = Color(0xFFBBF7D0)
            )
        }

        AppTheme.PERIWINKLE_DREAM -> if (darkTheme) {
            darkColorScheme(
                primary = PeriwinklePrimaryDark,
                onPrimary = Color(0xFF0F0C20),
                secondary = Color(0xFFC084FC),
                background = PeriwinkleBackgroundDark,
                onBackground = Color(0xFFFAF5FF),
                surface = PeriwinkleSurfaceDark,
                onSurface = Color(0xFFFAF5FF),
                surfaceVariant = Color(0xFF2E2A52),
                onSurfaceVariant = Color(0xFFC084FC),
                outline = Color(0xFF4C4580)
            )
        } else {
            lightColorScheme(
                primary = PeriwinklePrimary,
                onPrimary = Color.White,
                secondary = Color(0xFF9333EA),
                background = PeriwinkleBackgroundLight,
                onBackground = Color(0xFF2E1065),
                surface = PeriwinkleSurfaceLight,
                onSurface = Color(0xFF2E1065),
                surfaceVariant = Color(0xFFEDE9FE),
                onSurfaceVariant = Color(0xFF6B21A8),
                outline = Color(0xFFDDD6FE)
            )
        }

        AppTheme.CREME_BRULEE -> if (darkTheme) {
            darkColorScheme(
                primary = CremePrimaryDark,
                onPrimary = Color(0xFF1F1207),
                secondary = Color(0xFFFACC15),
                background = CremeBackgroundDark,
                onBackground = Color(0xFFFFFBEB),
                surface = CremeSurfaceDark,
                onSurface = Color(0xFFFFFBEB),
                surfaceVariant = Color(0xFF452B18),
                onSurfaceVariant = Color(0xFFFDE68A),
                outline = Color(0xFF633E23)
            )
        } else {
            lightColorScheme(
                primary = CremePrimary,
                onPrimary = Color.White,
                secondary = Color(0xFFD97706),
                background = CremeBackgroundLight,
                onBackground = Color(0xFF451A03),
                surface = CremeSurfaceLight,
                onSurface = Color(0xFF451A03),
                surfaceVariant = Color(0xFFFEF3C7),
                onSurfaceVariant = Color(0xFF92400E),
                outline = Color(0xFFFDE68A)
            )
        }

        AppTheme.NEON_GLAM -> if (darkTheme) {
            darkColorScheme(
                primary = GlamPrimaryDark,
                onPrimary = Color(0xFF09090B),
                secondary = Color(0xFF22D3EE),
                background = GlamBackgroundDark,
                onBackground = Color(0xFFFAFAFA),
                surface = GlamSurfaceDark,
                onSurface = Color(0xFFFAFAFA),
                surfaceVariant = Color(0xFF27272A),
                onSurfaceVariant = Color(0xFFF472B6),
                outline = Color(0xFF3F3F46)
            )
        } else {
            lightColorScheme(
                primary = GlamPrimary,
                onPrimary = Color.White,
                secondary = Color(0xFF06B6D4),
                background = GlamBackgroundLight,
                onBackground = Color(0xFF881337),
                surface = GlamSurfaceLight,
                onSurface = Color(0xFF881337),
                surfaceVariant = Color(0xFFFFE4E6),
                onSurfaceVariant = Color(0xFFBE123C),
                outline = Color(0xFFFECDD3)
            )
        }

        AppTheme.CARBON_DUSK -> if (darkTheme) {
            darkColorScheme(
                primary = CarbonPrimaryDark,
                onPrimary = Color(0xFF0F172A),
                secondary = Color(0xFF94A3B8),
                background = CarbonBackgroundDark,
                onBackground = Color(0xFFF8FAFC),
                surface = CarbonSurfaceDark,
                onSurface = Color(0xFFF8FAFC),
                surfaceVariant = Color(0xFF334155),
                onSurfaceVariant = Color(0xFFCBD5E1),
                outline = Color(0xFF475569)
            )
        } else {
            lightColorScheme(
                primary = CarbonPrimary,
                onPrimary = Color.White,
                secondary = Color(0xFF475569),
                background = CarbonBackgroundLight,
                onBackground = Color(0xFF0F172A),
                surface = CarbonSurfaceLight,
                onSurface = Color(0xFF0F172A),
                surfaceVariant = Color(0xFFE2E8F0),
                onSurfaceVariant = Color(0xFF334155),
                outline = Color(0xFFCBD5E1)
            )
        }
    }
}

@Composable
fun SapphireAITheme(
    appTheme: AppTheme = AppTheme.BRIGHT_SAPPHIRE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = getColorSchemeForAppTheme(appTheme, darkTheme)
    val view = LocalView.current

    if (!view.isInEditMode && view.context is Activity) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun SapphireTheme(
    appTheme: AppTheme = AppTheme.BRIGHT_SAPPHIRE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = SapphireAITheme(appTheme = appTheme, darkTheme = darkTheme, content = content)
