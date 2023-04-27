package com.marlon.portalusuario.commons.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Purple800,
    primaryVariant = Purple700,
    onPrimary = Color.White,
    secondary = Purple900,
    secondaryVariant = Purple700,
    onSecondary = Color.White,
    error = Red800
)

private val DarkColors = darkColors(
    primary = Orange900,
    primaryVariant = Orange800,
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
        colors = if (darkTheme) DarkColors else LightColors,
        typography = SuitEtecsaTypography,
        shapes = SuitEtecsaShapes,
        content = content
    )
}