package com.marlon.portalusuario.promotions

sealed class PromotionEvent {
    data object Load : PromotionEvent()
}