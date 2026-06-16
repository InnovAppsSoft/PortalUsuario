package com.marlon.portalusuario.testhelpers

import com.marlon.portalusuario.domain.data.UneRepository
import com.marlon.portalusuario.une.Une
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUneRepository(
    initialUnes: List<Une> = emptyList(),
) : UneRepository {
    private val _allUnes = MutableStateFlow(initialUnes)

    override val allUnes: Flow<List<Une>> = _allUnes

    override suspend fun insertUne(une: Une) {
        _allUnes.value = _allUnes.value + une
    }

    override suspend fun updateUne(une: Une) {
        _allUnes.value =
            _allUnes.value.map {
                if (it.id == une.id) une else it
            }
    }

    override suspend fun deleteUne(une: Une) {
        _allUnes.value = _allUnes.value - une
    }

    override suspend fun deleteAllUnes() {
        _allUnes.value = emptyList()
    }
}
