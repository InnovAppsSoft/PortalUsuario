package com.marlon.portalusuario.commons.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween

object ScrollAnimation {
    @OptIn(ExperimentalAnimationApi::class)
    operator fun invoke(): ContentTransform {
        return slideInVertically(
            initialOffsetY = { 50 },
            animationSpec = tween()
        ) + fadeIn() with slideOutVertically(
            targetOffsetY = { -50 },
            animationSpec = tween()
        ) + fadeOut()
    }
}