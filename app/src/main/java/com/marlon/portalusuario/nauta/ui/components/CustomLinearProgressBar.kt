package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CustomLinearProgressBar(
    modifier: Modifier,
    color: Color = MaterialTheme.colors.primary,
    progress: Float? = null
) {
    if (progress != null) {
        LinearProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            backgroundColor = MaterialTheme.colors.surface
        )
    } else {
        LinearProgressIndicator(
            modifier = modifier,
            color = color,
            backgroundColor = MaterialTheme.colors.surface
        )
    }

}