package com.example.d308vacationplanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = OceanBlue,
    secondary = SunsetOrange,
    tertiary = PalmGreen,
    background = SkyLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DeepNavy,
    onSurface = DeepNavy
)

val DarkColors = darkColorScheme(
    primary = OceanBlue,
    secondary = SunsetOrange,
    tertiary = PalmGreen,
    background = DeepNavy,
    surface = SandYellow,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)
@Composable
fun D308VacationPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
){
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
