package com.marlon.portalusuario.data.une

import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.domain.data.UneRepository
import com.marlon.portalusuario.une.Une
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UneRepositoryImpl
    @Inject
    constructor(
        private val dao: ServicesDao,
    ) : UneRepository {
        override val allUnes: Flow<List<Une>> = dao.getAllUnes()

        override suspend fun insertUne(une: Une) {
            dao.insertUne(une)
        }

        override suspend fun updateUne(une: Une) {
            dao.updateUne(une)
        }

        override suspend fun deleteUne(une: Une) {
            dao.deleteUne(une)
        }

        override suspend fun deleteAllUnes() {
            dao.deleteAllUnes()
        }
    }
