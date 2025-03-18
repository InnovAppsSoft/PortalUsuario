package com.marlon.portalusuario.ui.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CustomLinearProgressBar(
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    progress: Float? = null
) {
    if (progress != null) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = modifier,
            color = color,
            trackColor = MaterialTheme.colorScheme.surface
        )
    } else {
        LinearProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}
