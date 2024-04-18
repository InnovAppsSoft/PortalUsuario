package com.marlon.portalusuario.banner

sealed class PromotionEvent {
    data object Reload : PromotionEvent()
}