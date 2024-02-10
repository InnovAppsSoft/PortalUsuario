package cu.suitetecsa.cubacelmanager.di

import android.content.Context
import cu.suitetecsa.cubacelmanager.data.source.PreferencesDataSource
import cu.suitetecsa.sdk.android.SimCardCollector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BalanceModule {
    @Provides
    @Singleton
    fun providePreferencesDataSource(@ApplicationContext context: Context): PreferencesDataSource =
        PreferencesDataSource(context)

    @Provides
    @Singleton
    fun simCardCollector(@ApplicationContext context: Context): SimCardCollector =
        SimCardCollector.Builder()
            .build(context)
}
