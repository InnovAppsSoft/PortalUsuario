package com.marlon.portalusuario.feature.balancemanagement.di

import android.content.Context
import androidx.room.Room
import com.marlon.portalusuario.feature.balancemanagement.data.datasource.BalanceManagementPreferencesDataSourceImpl
import com.marlon.portalusuario.feature.profile.domain.data.datasource.ProfilePreferencesDataSource
import com.marlon.portalusuario.feature.profile.data.datasource.ProfilePreferencesDataSourceImpl
import com.marlon.portalusuario.feature.balancemanagement.data.room.util.BONUS_BALANCE_TABLE
import com.marlon.portalusuario.feature.balancemanagement.data.room.util.MAIN_BALANCE_TABLE
import com.marlon.portalusuario.feature.balancemanagement.data.room.db.BonusBalanceDb
import com.marlon.portalusuario.feature.balancemanagement.data.room.db.MainBalanceDb
import com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource.BalanceManagementPreferencesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BalanceManagementModule {
    @Singleton
    @Provides
    fun provideProfilePreferencesRepository(
        @ApplicationContext context: Context,
    ): ProfilePreferencesDataSource = ProfilePreferencesDataSourceImpl(context)

    @Provides
    internal fun provideMainBalanceDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, MainBalanceDb::class.java, MAIN_BALANCE_TABLE).build()

    @Provides
    internal fun provideBonusBalanceDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, BonusBalanceDb::class.java, BONUS_BALANCE_TABLE).build()

    @Provides
    internal fun provideMainBalanceDao(mainBalanceDb: MainBalanceDb) = mainBalanceDb.mainBalanceDao

    @Provides
    internal fun provideBonusBalanceDao(bonusBalanceDb: BonusBalanceDb) =
        bonusBalanceDb.bonusBalanceDao

    @Singleton
    @Provides
    fun provideBalancePreferencesDataSource(@ApplicationContext context: Context):
            BalanceManagementPreferencesDataSource = BalanceManagementPreferencesDataSourceImpl(context)
}