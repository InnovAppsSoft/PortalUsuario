package com.marlon.portalusuario.feature.balancemanagement.di

import android.content.Context
import android.telephony.TelephonyManager
import com.marlon.portalusuario.feature.balancemanagement.data.suitetecsa.SuitEtecsaSimCardsProvider
import com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource.SimCardDataSource
import cu.suitetecsa.sdk.sim.SimCardsAPI
import cu.suitetecsa.sdk.ussd.UssdApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SuitEtecsaModule {
    @Singleton
    @Provides
    fun provideSimCardsApi(
        @ApplicationContext context: Context
    ): SimCardsAPI = SimCardsAPI.builder(context).build()

    @Singleton
    @Provides
    fun provideUssdApi(@ApplicationContext context: Context): UssdApi =
        UssdApi.builder(context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).build()

    @Singleton
    @Provides
    fun provideSuitEtecsaSimCardsProvider(
        simCardsAPI: SimCardsAPI
    ): SimCardDataSource = SuitEtecsaSimCardsProvider(simCardsAPI)
}