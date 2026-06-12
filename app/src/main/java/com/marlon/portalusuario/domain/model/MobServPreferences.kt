package com.marlon.portalusuario.domain.model

data class MobServPreferences(
    val slotIndexInfoList: List<SlotIndexInfo>,
    // MobileServiceSelectedId
    val mssId: String? = null,
)
