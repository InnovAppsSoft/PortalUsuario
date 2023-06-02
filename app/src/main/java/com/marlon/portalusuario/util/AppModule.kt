package com.marlon.portalusuario.util

import android.content.Context
import androidx.room.Room
import com.marlon.portalusuario.commons.USER_TABLE
import com.marlon.portalusuario.nauta.data.UserDao
import com.marlon.portalusuario.nauta.data.network.NautaService
import com.marlon.portalusuario.nauta.data.network.UserDb
import com.marlon.portalusuario.nauta.data.network.UserRepository
import com.marlon.portalusuario.nauta.data.network.UserRepositoryImpl
import com.marlon.portalusuario.nauta.service.CountdownServiceClient
import cu.suitetecsa.sdk.nauta.data.repository.DefaultNautaSession
import cu.suitetecsa.sdk.nauta.data.repository.JSoupNautaScrapper
import cu.suitetecsa.sdk.nauta.domain.service.NautaClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideNautaClient(): NautaClient = NautaClient(JSoupNautaScrapper(DefaultNautaSession()))

    @Provides
    fun provideUserDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, UserDb::class.java, USER_TABLE).build()

    @Provides
    fun provideUserDao(userDb: UserDb) = userDb.userDao

    @Provides
    fun providerUserRepository(userDao: UserDao, nautaService: NautaService): UserRepository =
        UserRepositoryImpl(userDao = userDao, service = nautaService)

    @Singleton
    @Provides
    fun provideNautaSharedPreferences(@ApplicationContext context: Context): Pref = Pref(context)

    @Singleton
    @Provides
    fun provideCountdownServiceClient(@ApplicationContext context: Context) = CountdownServiceClient(context)
}