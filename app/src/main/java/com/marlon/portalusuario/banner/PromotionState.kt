package com.marlon.portalusuario.banner

import io.github.suitetecsa.sdk.promotion.model.Promotion

data class PromotionState(
    val promotions: List<Promotion> = listOf(),
    val isLoading: Boolean = false,
    val onError: Boolean = false
)
