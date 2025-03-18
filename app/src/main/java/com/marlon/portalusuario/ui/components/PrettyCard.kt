package com.marlon.portalusuario.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PrettyCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isFoundErrors: Boolean = false,
    backgroundColor: Color? = null,
    content: (@Composable () -> Unit)
) {
    val shadowColor =
        if (isFoundErrors) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    val privateModifier = if (backgroundColor != null) Modifier.background(color = backgroundColor) else Modifier

    Card(
        modifier = modifier
            .wrapContentSize()
            .shadow(elevation = 8.dp, ambientColor = shadowColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = privateModifier
        ) {
            Box(modifier = privateModifier) {
                if (isLoading) {
                    CustomLinearProgressBar(modifier = Modifier.matchParentSize())
                }
                Surface(
                    modifier = privateModifier
                        .padding(5.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(modifier = privateModifier) {
                        content()
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PrettyCardPreview() {
    PrettyCard(isFoundErrors = true, isLoading = true, backgroundColor = Color(0xFFFF4747)) {
        Text(text = "Hola", modifier = Modifier.padding(16.dp))
    }
}
