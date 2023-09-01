package com.marlon.portalusuario.feature.balancemanagement.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.marlon.portalusuario.feature.balancemanagement.data.room.util.BONUS_BALANCE_TABLE
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.BonusBalanceCacheEntity
import kotlinx.coroutines.flow.Flow

internal typealias BonusBalances = List<BonusBalanceCacheEntity>

@Dao
interface BonusBalanceDao {
    @Query("SELECT * FROM $BONUS_BALANCE_TABLE ORDER BY id ASC")
    fun getBalances(): Flow<BonusBalances>
    @Query("SELECT * FROM $BONUS_BALANCE_TABLE WHERE id = :id")
    suspend fun getBalance(id: String): BonusBalanceCacheEntity?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBonusBalance(balance: BonusBalanceCacheEntity): Long

    @Update
    suspend fun updateBonusBalance(balance: BonusBalanceCacheEntity)

    @Delete
    suspend fun deleteBonusBalance(balance: BonusBalanceCacheEntity)
}