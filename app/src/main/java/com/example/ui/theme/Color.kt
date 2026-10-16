package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// User's Exact Palette from image
val PaletteGoldenYellow = Color(0xFFF6BD60)
val PaletteWarmCream = Color(0xFFF7EDE2)
val PaletteSoftBlush = Color(0xFFF5CAC3)
val PaletteSageTeal = Color(0xFF84A59D)
val PaletteWarmCoral = Color(0xFFF28482)

// Light Mode ("Retro Warm Paper / Cassette Futurism")
val LightBackground = PaletteWarmCream // #F7EDE2
val LightSurface = Color(0xFFFFFDF9)
val LightSurfaceVariant = Color(0xFFEFE3D5)
val LightSurfaceBorder = Color(0xFFDECFC0)
val LightTextPrimary = Color(0xFF242A2E)
val LightTextSecondary = Color(0xFF5D6B67)
val LightTextMuted = Color(0xFF8B9A95)

// Dark Mode ("Retro Cassette Night")
val DarkBackground = Color(0xFF15191C)
val DarkSurface = Color(0xFF1E2428)
val DarkSurfaceVariant = Color(0xFF283137)
val DarkSurfaceBorder = Color(0xFF38444B)
val DarkTextPrimary = PaletteWarmCream // #F7EDE2
val DarkTextSecondary = Color(0xFFB5C4C0)
val DarkTextMuted = Color(0xFF7A8985)

// Backward compatible aliases
val CyberBackground = DarkBackground
val CyberSurface = DarkSurface
val CyberSurfaceVariant = DarkSurfaceVariant
val CyberSurfaceBorder = DarkSurfaceBorder

val NeonCyan = PaletteSageTeal
val NeonCyanDim = Color(0xFF678981)
val NeonAmber = PaletteGoldenYellow
val NeonAmberGlow = Color(0xFFFFD485)
val NeonCoral = PaletteWarmCoral
val NeonGreen = PaletteSageTeal
val NeonPurple = PaletteSoftBlush

val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextMuted = DarkTextMuted
