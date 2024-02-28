package cu.suitetecsa.core.ui.components.animateplaceholder

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState

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
