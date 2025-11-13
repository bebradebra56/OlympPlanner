package com.olympplanner.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DayColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueLight,
    onPrimaryContainer = TextPrimaryDay,
    secondary = AccentGold,
    onSecondary = TextPrimaryDay,
    secondaryContainer = MarbleCard,
    onSecondaryContainer = TextPrimaryDay,
    tertiary = CategoryPersonal,
    onTertiary = Color.White,
    background = SkyBlueLight,
    onBackground = TextPrimaryDay,
    surface = MarbleCard,
    onSurface = TextPrimaryDay,
    surfaceVariant = Color.White,
    onSurfaceVariant = TextSecondaryDay,
    error = ErrorRed,
    onError = Color.White,
    outline = TextSecondaryDay.copy(alpha = 0.3f),
    outlineVariant = TextSecondaryDay.copy(alpha = 0.1f)
)

private val NightColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = NightSkyLight,
    onPrimaryContainer = TextPrimaryNight,
    secondary = AccentGold,
    onSecondary = NightSkyDark,
    secondaryContainer = MarbleCardNight,
    onSecondaryContainer = TextPrimaryNight,
    tertiary = CategoryPersonal,
    onTertiary = Color.White,
    background = NightSkyDark,
    onBackground = TextPrimaryNight,
    surface = MarbleCardNight,
    onSurface = TextPrimaryNight,
    surfaceVariant = NightSkyLight,
    onSurfaceVariant = TextSecondaryNight,
    error = ErrorRed,
    onError = Color.White,
    outline = TextSecondaryNight.copy(alpha = 0.3f),
    outlineVariant = TextSecondaryNight.copy(alpha = 0.1f)
)

@Composable
fun OlympPlannerTheme(
    themeMode: ThemeMode = ThemeMode.DAY,
    accentSaturation: Float = 1.0f,
    content: @Composable () -> Unit
) {
    // Apply accent saturation to colors
    val saturatedElectricBlue = adjustSaturation(ElectricBlue, accentSaturation)
    val saturatedAccentGold = adjustSaturation(AccentGold, accentSaturation)
    
    val colorScheme = when (themeMode) {
        ThemeMode.DAY -> DayColorScheme.copy(
            primary = saturatedElectricBlue,
            secondary = saturatedAccentGold
        )
        ThemeMode.NIGHT -> NightColorScheme.copy(
            primary = saturatedElectricBlue,
            secondary = saturatedAccentGold
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = themeMode == ThemeMode.DAY
                isAppearanceLightNavigationBars = themeMode == ThemeMode.DAY
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Adjusts the saturation of a color
 * @param color The original color
 * @param saturation Saturation multiplier (0.0 = grayscale, 1.0 = original, >1.0 = more saturated)
 */
private fun adjustSaturation(color: Color, saturation: Float): Color {
    // Convert to HSV-like values
    val r = color.red
    val g = color.green
    val b = color.blue
    
    // Find max and min RGB values
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    
    // Calculate current saturation
    val currentSaturation = if (max != 0f) (max - min) / max else 0f
    
    // Calculate the gray value (desaturated color)
    val gray = (r + g + b) / 3f
    
    // Interpolate between gray and original color based on saturation parameter
    val newSaturation = currentSaturation * saturation
    val mixFactor = if (currentSaturation != 0f) newSaturation / currentSaturation else 1f
    
    return Color(
        red = gray + (r - gray) * mixFactor.coerceIn(0f, 1f),
        green = gray + (g - gray) * mixFactor.coerceIn(0f, 1f),
        blue = gray + (b - gray) * mixFactor.coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

