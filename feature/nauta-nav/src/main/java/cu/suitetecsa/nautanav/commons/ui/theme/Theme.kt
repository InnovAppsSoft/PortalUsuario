package cu.suitetecsa.nautanav.commons.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Purple800,
    onPrimary = Color.White,
    secondary = Purple900,
    onSecondary = Color.White,
    error = Red800
)

private val DarkColors = darkColorScheme(
    primary = Orange900,
    onPrimary = Color.White,
    secondary = Orange600,
    onSecondary = Color.White,
    error = Red200
)

@Composable
fun SuitEtecsaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SuitEtecsaTypography,
        shapes = SuitEtecsaShapes,
        content = content
    )
}