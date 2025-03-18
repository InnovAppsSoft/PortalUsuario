package com.marlon.portalusuario.ui.components

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

object ScrollAnimation {
    operator fun invoke(): ContentTransform {
        return (
            slideInVertically(
                initialOffsetY = { 50 },
                animationSpec = tween()
            ) + fadeIn()
            ).togetherWith(
            slideOutVertically(
                targetOffsetY = { -50 },
                animationSpec = tween()
            ) + fadeOut()
        )
    }
}
