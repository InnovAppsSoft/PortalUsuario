package com.marlon.portalusuario.data.preferences

import com.marlon.portalusuario.domain.model.MobServPreferences
import com.marlon.portalusuario.domain.model.SlotIndexInfo
import kotlinx.coroutines.flow.Flow

interface IMobServicesPreferences {
    val preferences: Flow<MobServPreferences>
    suspend fun updateSlotIndexInfoList(slotIndexInfoList: List<SlotIndexInfo>)
    suspend fun updateMobileServiceSelectedId(id: String?)
}
