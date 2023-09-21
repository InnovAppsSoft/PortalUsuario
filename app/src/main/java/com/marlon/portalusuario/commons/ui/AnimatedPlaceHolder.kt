package com.marlon.portalusuario.commons.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.marlon.portalusuario.commons.utils.doWhenHasNextOrPrevious

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
        transitionSpec = { ScrollAnimation() }, label = ""
    ) { str ->
        Text(
            text = str
        )
    }
}