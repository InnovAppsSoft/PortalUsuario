package com.marlon.portalusuario.testhelpers

import com.marlon.portalusuario.data.preferences.IMobServicesPreferences
import com.marlon.portalusuario.domain.model.MobServPreferences
import com.marlon.portalusuario.domain.model.SlotIndexInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMobServicesPreferences(
    initial: MobServPreferences = MobServPreferences(emptyList()),
) : IMobServicesPreferences {
    private val _preferences = MutableStateFlow(initial)

    override val preferences: Flow<MobServPreferences> = _preferences

    override suspend fun updateSlotIndexInfoList(slotIndexInfoList: List<SlotIndexInfo>) {
        _preferences.value = _preferences.value.copy(slotIndexInfoList = slotIndexInfoList)
    }

    override suspend fun updateMobileServiceSelectedId(id: String?) {
        _preferences.value = _preferences.value.copy(mssId = id)
    }
}
