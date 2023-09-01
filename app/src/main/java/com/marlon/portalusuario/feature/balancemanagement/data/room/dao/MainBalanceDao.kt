package com.marlon.portalusuario.feature.balancemanagement.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.marlon.portalusuario.feature.balancemanagement.data.room.util.MAIN_BALANCE_TABLE
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainBalanceCacheEntity
import kotlinx.coroutines.flow.Flow

internal typealias MainBalances = List<MainBalanceCacheEntity>

@Dao
interface MainBalanceDao {
    @Query("SELECT * FROM $MAIN_BALANCE_TABLE ORDER BY id ASC")
    fun getBalances(): Flow<MainBalances>

    @Query("SELECT * FROM $MAIN_BALANCE_TABLE WHERE id = :id")
    suspend fun getBalance(id: String): MainBalanceCacheEntity?

    @Insert(onConflict = REPLACE)
    suspend fun addMainBalance(mainBalanceCacheEntity: MainBalanceCacheEntity): Long

    @Update
    suspend fun updateMainBalance(mainBalanceCacheEntity: MainBalanceCacheEntity)

    @Delete
    suspend fun deleteMainBalance(mainBalanceCacheEntity: MainBalanceCacheEntity)
}