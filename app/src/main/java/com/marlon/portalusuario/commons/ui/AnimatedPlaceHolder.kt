package com.marlon.portalusuario.commons.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import com.marlon.portalusuario.commons.utils.doWhenHasNextOrPrevious

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedPlaceholder(
    hints: List<String>
) {
    val iterator = hints.listIterator()
    val target by produceState(initialValue = hints.first()) {
        iterator.doWhenHasNextOrPrevious {
            value = it
        }
    }

    AnimatedContent(
        targetState = target,
        transitionSpec = { ScrollAnimation() }
    ) { str ->
        Text(
            text = str
        )
    }
}