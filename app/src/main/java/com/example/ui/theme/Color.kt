package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// THEME ENUM DEFINITION
// ==========================================
enum class AppTheme(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val backgroundColor: Color
) {
    BRIGHT_SAPPHIRE("Bright Sapphire", "Electric blue & clean glass slate", Color(0xFF0F52BA), Color(0xFFF0F4FF)),
    MIDNIGHT_COBALT("Midnight Cobalt", "Deep cobalt & navy blue night", Color(0xFF2563EB), Color(0xFF0F172A)),
    GOLDEN_TAUPE("Golden Taupe", "Warm golden bronze & sand taupe", Color(0xFFD97706), Color(0xFFFDFBF7)),
    MOSSY_HOLLOW("Mossy Hollow", "Earthy sage & deep forest moss", Color(0xFF15803D), Color(0xFFF4F7F4)),
    PERIWINKLE_DREAM("Periwinkle Dream", "Soft lavender & dreamy violet", Color(0xFF7C3AED), Color(0xFFF5F3FF)),
    CREME_BRULEE("Creme Brulee", "Rich custard cream & caramel toast", Color(0xFFEA580C), Color(0xFFFFFBEB)),
    NEON_GLAM("Neon Glam", "Cyberpunk electric magenta & cyan", Color(0xFFEC4899), Color(0xFF09090B)),
    CARBON_DUSK("Carbon Dusk", "Graphite carbon slate & cyan dusk", Color(0xFF38BDF8), Color(0xFF18181B))
}

// ==========================================
// BRIGHT SAPPHIRE BRAND COLOR PALETTE
// ==========================================
val SapphirePrimary = Color(0xFF0F52BA)
val SapphirePrimaryLight = Color(0xFF2563EB)
val SapphirePrimaryDark = Color(0xFF0A3A86)
val SapphirePrimaryContainerLight = Color(0xFFDBEAFE)
val SapphireOnPrimaryContainerLight = Color(0xFF1E3A8A)
val SapphirePrimaryContainerDark = Color(0xFF1E3A8A)
val SapphireOnPrimaryContainerDark = Color(0xFFBFDBFE)

val SapphireSecondary = Color(0xFF4F46E5)
val SapphireSecondaryLight = Color(0xFF6366F1)
val SapphireSecondaryDark = Color(0xFF3730A3)
val SapphireSecondaryContainerLight = Color(0xFFE0E7FF)
val SapphireOnSecondaryContainerLight = Color(0xFF312E81)
val SapphireSecondaryContainerDark = Color(0xFF312E81)
val SapphireOnSecondaryContainerDark = Color(0xFFC7D2FE)

val SapphireTertiary = Color(0xFF0284C7)
val SapphireTertiaryContainerLight = Color(0xFFE0F2FE)
val SapphireOnTertiaryContainerLight = Color(0xFF075985)
val SapphireTertiaryContainerDark = Color(0xFF0C4A6E)
val SapphireOnTertiaryContainerDark = Color(0xFFBAE6FD)

val SapphireLightBackground = Color(0xFFF0F4FF)
val SapphireLightSurface = Color(0xFFFFFFFF)
val SapphireLightSurfaceVariant = Color(0xFFF8FAFF)
val SapphireLightOutline = Color(0xFFCBD5E1)

val SapphireDarkBackground = Color(0xFF0B1120)
val SapphireDarkSurface = Color(0xFF131C31)
val SapphireDarkSurfaceVariant = Color(0xFF1E293B)
val SapphireDarkOutline = Color(0xFF334155)

// ==========================================
// 1. MIDNIGHT COBALT
// ==========================================
val CobaltPrimary = Color(0xFF2563EB)
val CobaltPrimaryDark = Color(0xFF60A5FA)
val CobaltBackgroundLight = Color(0xFFF0F6FF)
val CobaltSurfaceLight = Color(0xFFFFFFFF)
val CobaltBackgroundDark = Color(0xFF090D16)
val CobaltSurfaceDark = Color(0xFF111827)

// ==========================================
// 2. GOLDEN TAUPE
// ==========================================
val TaupePrimary = Color(0xFFB45309)
val TaupePrimaryDark = Color(0xFFFBBF24)
val TaupeBackgroundLight = Color(0xFFFDFBF7)
val TaupeSurfaceLight = Color(0xFFFFFFFF)
val TaupeBackgroundDark = Color(0xFF1C1917)
val TaupeSurfaceDark = Color(0xFF292524)

// ==========================================
// 3. MOSSY HOLLOW
// ==========================================
val MossPrimary = Color(0xFF15803D)
val MossPrimaryDark = Color(0xFF4ADE80)
val MossBackgroundLight = Color(0xFFF4F7F4)
val MossSurfaceLight = Color(0xFFFFFFFF)
val MossBackgroundDark = Color(0xFF0A1A10)
val MossSurfaceDark = Color(0xFF142A1D)

// ==========================================
// 4. PERIWINKLE DREAM
// ==========================================
val PeriwinklePrimary = Color(0xFF7C3AED)
val PeriwinklePrimaryDark = Color(0xFFA78BFA)
val PeriwinkleBackgroundLight = Color(0xFFF5F3FF)
val PeriwinkleSurfaceLight = Color(0xFFFFFFFF)
val PeriwinkleBackgroundDark = Color(0xFF0F0C20)
val PeriwinkleSurfaceDark = Color(0xFF1E1A3A)

// ==========================================
// 5. CREME BRULEE
// ==========================================
val CremePrimary = Color(0xFFEA580C)
val CremePrimaryDark = Color(0xFFFB923C)
val CremeBackgroundLight = Color(0xFFFFFBEB)
val CremeSurfaceLight = Color(0xFFFFFFFF)
val CremeBackgroundDark = Color(0xFF1F1207)
val CremeSurfaceDark = Color(0xFF2E1C0C)

// ==========================================
// 6. NEON GLAM
// ==========================================
val GlamPrimary = Color(0xFFEC4899)
val GlamPrimaryDark = Color(0xFFF472B6)
val GlamBackgroundLight = Color(0xFFFFF1F2)
val GlamSurfaceLight = Color(0xFFFFFFFF)
val GlamBackgroundDark = Color(0xFF09090B)
val GlamSurfaceDark = Color(0xFF18181B)

// ==========================================
// 7. CARBON DUSK
// ==========================================
val CarbonPrimary = Color(0xFF0284C7)
val CarbonPrimaryDark = Color(0xFF38BDF8)
val CarbonBackgroundLight = Color(0xFFF8FAFC)
val CarbonSurfaceLight = Color(0xFFFFFFFF)
val CarbonBackgroundDark = Color(0xFF0F172A)
val CarbonSurfaceDark = Color(0xFF1E293B)

// Legacy & Alias Mappings
val SapphireAccent = SapphireSecondary
val SapphireLightTint = SapphireLightBackground
val SapphireUltraLight = SapphireLightSurfaceVariant

// Glassmorphism Palettes
val GlassWhite = Color(0xEEFFFFFF)
val GlassWhiteBorder = Color(0x66FFFFFF)
val GlassSurfaceLight = Color(0xCCFFFFFF)
val GlassBorderLight = Color(0x400F52BA)
val GlassCardBgLight = Color(0xF5FFFFFF)

val DarkBg = SapphireDarkBackground
val DarkBackground = SapphireDarkBackground
val DarkSurface = SapphireDarkSurface
val DarkGlass = Color(0xCC131C31)
val DarkGlassSurface = SapphireDarkSurfaceVariant
val DarkGlassBorder = Color(0x3364748B)

// Badges
val BadgeAiBg = Color(0xFFEFF6FF)
val BadgeAiText = SapphirePrimary
val BadgeAiReadyBg = Color(0xFFEFF6FF)
val BadgeAiReadyText = SapphirePrimary
val BadgeProBg = Color(0xFFFFFBEB)
val BadgeProText = Color(0xFFD97706)
val BadgeExportBg = Color(0xFFECFDF5)
val BadgeExportText = Color(0xFF059669)

// Typography
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
