package cu.suitetecsa.cubacelmanager.di

import android.content.Context
import androidx.room.Room
import cu.suitetecsa.cubacelmanager.data.BalanceDao
import cu.suitetecsa.cubacelmanager.data.sources.BalanceDb
import cu.suitetecsa.cubacelmanager.data.sources.BalanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RoomModule {
    @Provides
    fun provideBalanceDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, BalanceDb::class.java, "balance_table").build()

    @Provides
    fun provideBalanceDao(balanceDb: BalanceDb) = balanceDb.balanceDao

    @Singleton
    @Provides
    fun provideBalanceRepository(balanceDao: BalanceDao) =
        BalanceRepository(balanceDao)
}
