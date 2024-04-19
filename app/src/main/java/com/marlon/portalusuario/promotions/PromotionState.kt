package com.marlon.portalusuario.promotions

import com.marlon.portalusuario.promotions.model.Promotion


data class PromotionState(
    val promotions: List<Promotion> = listOf(),
    val isLoading: Boolean = false,
    val onError: Boolean = false
)
