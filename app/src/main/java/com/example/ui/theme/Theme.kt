package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

data class DiesColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val goldenYellow: Color,
    val warmCream: Color,
    val softBlush: Color,
    val sageTeal: Color,
    val warmCoral: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val todayHighlightBg: Color,
    val celebrationBg: Color
)

val LocalDiesColors = staticCompositionLocalOf {
    DiesColors(
        isDark = false,
        background = LightBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        surfaceBorder = LightSurfaceBorder,
        textPrimary = LightTextPrimary,
        textSecondary = LightTextSecondary,
        textMuted = LightTextMuted,
        goldenYellow = PaletteGoldenYellow,
        warmCream = PaletteWarmCream,
        softBlush = PaletteSoftBlush,
        sageTeal = PaletteSageTeal,
        warmCoral = PaletteWarmCoral,
        primaryAccent = PaletteSageTeal,
        secondaryAccent = PaletteGoldenYellow,
        todayHighlightBg = Color(0xFFFDEFD9),
        celebrationBg = Color(0xFFE3EDE9)
    )
}

object DiesTheme {
    val colors: DiesColors
        @Composable
        get() = LocalDiesColors.current
}

private val LightDiesColors = DiesColors(
    isDark = false,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    surfaceBorder = LightSurfaceBorder,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    goldenYellow = PaletteGoldenYellow,
    warmCream = PaletteWarmCream,
    softBlush = PaletteSoftBlush,
    sageTeal = PaletteSageTeal,
    warmCoral = PaletteWarmCoral,
    primaryAccent = PaletteSageTeal,
    secondaryAccent = PaletteGoldenYellow,
    todayHighlightBg = Color(0xFFFDEFD9),
    celebrationBg = Color(0xFFE3EDE9)
)

private val DarkDiesColors = DiesColors(
    isDark = true,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    surfaceBorder = DarkSurfaceBorder,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    goldenYellow = PaletteGoldenYellow,
    warmCream = PaletteWarmCream,
    softBlush = PaletteSoftBlush,
    sageTeal = PaletteSageTeal,
    warmCoral = PaletteWarmCoral,
    primaryAccent = PaletteGoldenYellow,
    secondaryAccent = PaletteSageTeal,
    todayHighlightBg = Color(0xFF2B2215),
    celebrationBg = Color(0xFF1B2825)
)

private val LightColorScheme = lightColorScheme(
    primary = PaletteSageTeal,
    onPrimary = Color.White,
    primaryContainer = PaletteSoftBlush,
    onPrimaryContainer = LightTextPrimary,
    secondary = PaletteGoldenYellow,
    onSecondary = LightTextPrimary,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightTextPrimary,
    tertiary = PaletteWarmCoral,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightSurfaceBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = PaletteGoldenYellow,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = PaletteGoldenYellow,
    secondary = PaletteSageTeal,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = PaletteSageTeal,
    tertiary = PaletteWarmCoral,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSurfaceBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to light mode as requested, fully toggleable!
    content: @Composable () -> Unit
) {
    val diesColors = if (darkTheme) DarkDiesColors else LightDiesColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = diesColors.background.toArgb()
                window.navigationBarColor = diesColors.background.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    CompositionLocalProvider(LocalDiesColors provides diesColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
