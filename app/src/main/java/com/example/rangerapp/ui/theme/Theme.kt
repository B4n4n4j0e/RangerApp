package com.example.rangerapp.ui.theme

import android.hardware.lights.Light
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorPalette = darkColors(
    primary = LightPurple,
    primaryVariant= DarkPurple,
    secondary= LightTurquoise,
    secondaryVariant= LightTurquoise,
    background = LightBlack,
    surface = LightBlack,
    error = LightRed,
    onPrimary= Black,
    onSecondary= Black,
    onBackground = White,
    onSurface = White,
    onError = Black
)


private val LightColorPalette = lightColors(
    primary = MediumPurple,
    primaryVariant= DarkPurple,
    secondary= LightTurquoise,
    secondaryVariant= DarkTurquoise,
    background = White,
    surface = White,
    error = Red,
    onPrimary= White,
    onSecondary= Black,
    onBackground = Black,
    onSurface = Black,
    onError = White

)

@Composable
fun JetpackComposeCrashCourseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}