package com.marlon.portalusuario.domain.model

data class MobServPreferences(
    val slotIndexInfoList: List<SlotIndexInfo>,
    val mssId: String? = null, // MobileServiceSelectedId
)
