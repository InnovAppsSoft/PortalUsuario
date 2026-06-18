package com.marlon.portalusuario.testhelpers

import com.marlon.portalusuario.domain.data.PunRepository
import com.marlon.portalusuario.feature.notifications.PUNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePunRepository(
    initialPuns: List<PUNotification> = emptyList(),
) : PunRepository {
    private val _allPUN = MutableStateFlow(initialPuns)

    override val allPUN: Flow<List<PUNotification>> = _allPUN

    override suspend fun insertPUNotification(pun: PUNotification) {
        _allPUN.value = _allPUN.value + pun
    }

    override suspend fun updatePUNotification(pun: PUNotification) {
        _allPUN.value =
            _allPUN.value.map {
                if (it.id == pun.id) pun else it
            }
    }

    override suspend fun deletePUNotification(pun: PUNotification) {
        _allPUN.value = _allPUN.value - pun
    }

    override suspend fun deleteAllPUNotifications() {
        _allPUN.value = emptyList()
    }
}
