package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = CyberTeal,
    tertiary = ElectricBlue,
    background = DeepBlack,
    surface = SurfaceDarkPurple,
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

private val LightColorScheme = darkColorScheme( // Force dark theme to prevent eye strain
    primary = NeonPurple,
    secondary = CyberTeal,
    tertiary = ElectricBlue,
    background = DeepBlack,
    surface = SurfaceDarkPurple,
    onPrimary = DeepBlack,
    onSecondary = DeepBlack,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark theme
    dynamicColor: Boolean = false, // Disable to preserve our gorgeous neon branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
