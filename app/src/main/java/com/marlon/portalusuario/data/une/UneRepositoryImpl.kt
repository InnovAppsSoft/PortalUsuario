package com.marlon.portalusuario.data.une

import com.marlon.portalusuario.database.une.UneDAO
import com.marlon.portalusuario.domain.data.UneRepository
import com.marlon.portalusuario.une.Une
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UneRepositoryImpl
    @Inject
    constructor(
        private val uneDao: UneDAO,
    ) : UneRepository {
        override val allUnes: Flow<List<Une>> = uneDao.getAllUne()

        override suspend fun insertUne(une: Une) {
            uneDao.insertUne(une)
        }

        override suspend fun updateUne(une: Une) {
            uneDao.updateUne(une)
        }

        override suspend fun deleteUne(une: Une) {
            uneDao.deleteUne(une)
        }

        override suspend fun deleteAllUnes() {
            uneDao.deleteAllUne()
        }
    }
