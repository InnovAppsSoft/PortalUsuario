package com.marlon.portalusuario.feature.balancemanagement.data.repository

import com.marlon.portalusuario.feature.balancemanagement.data.room.util.BonusBalanceCacheMapper
import com.marlon.portalusuario.feature.balancemanagement.data.room.util.MainBalanceCacheMapper
import com.marlon.portalusuario.feature.balancemanagement.data.room.dao.BonusBalanceDao
import com.marlon.portalusuario.feature.balancemanagement.data.room.dao.MainBalanceDao
import com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource.SimCardDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.marlon.portalusuario.feature.balancemanagement.domain.model.BonusBalanceDomainEntity as BonusBalance
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity as MainBalance

class BalancesRepository @Inject constructor(
    private val mainBalanceDao: MainBalanceDao,
    private val bonusBalanceDao: BonusBalanceDao,
    private val mainBalanceCacheMapper: MainBalanceCacheMapper,
    private val bonusBalanceCacheMapper: BonusBalanceCacheMapper
) {

    fun mainBalancesFromCache(): Flow<List<MainBalance>> = mainBalanceDao.getBalances().map { balances ->
        balances.map { mainBalanceCacheMapper.mapToDomain(it) }
    }
    suspend fun mainBalanceFromCache(simCardId: String): MainBalance? =
        mainBalanceDao.getBalance(simCardId)?.let { mainBalanceCacheMapper.mapToDomain(it) }

    suspend fun saveMainBalance(balance: MainBalance) {
        mainBalanceDao.addMainBalance(mainBalanceCacheMapper.mapFromDomain(balance))
    }

    fun bonusBalancesFromCache(): Flow<List<BonusBalance>> = bonusBalanceDao.getBalances().map { balances ->
        balances.map { bonusBalanceCacheMapper.mapToDomain(it) }
    }

    suspend fun bonusBalanceFromCache(simCardId: String): BonusBalance? =
        bonusBalanceDao.getBalance(simCardId)?.let { bonusBalanceCacheMapper.mapToDomain(it) }

    suspend fun saveBonusBalance(balance: BonusBalance) {
        bonusBalanceDao.addBonusBalance(bonusBalanceCacheMapper.mapFromDomain(balance))
    }
}