package com.marlon.portalusuario.di

import android.content.Context
import androidx.room.Room
import com.marlon.portalusuario.Pref
import com.marlon.portalusuario.commons.USER_TABLE
import com.marlon.portalusuario.nauta.data.UserDao
import com.marlon.portalusuario.nauta.data.network.UserDb
import com.marlon.portalusuario.nauta.data.repository.UserRepository
import com.marlon.portalusuario.nauta.data.repository.UserRepositoryImpl
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
    @Provides
    fun provideUserDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, UserDb::class.java, USER_TABLE).build()

    @Provides
    fun provideUserDao(userDb: UserDb) = userDb.userDao

    @Provides
    fun providerUserRepository(userDao: UserDao): UserRepository =
        UserRepositoryImpl(userDao = userDao)

    @Singleton
    @Provides
    fun provideNautaClient(): NautaClient = NautaClient(JSoupNautaScrapper(DefaultNautaSession()))

    @Singleton
    @Provides
    fun provideNautaSharedPreferences(@ApplicationContext context: Context): Pref = Pref(context)

    @Singleton
    @Provides
    fun provideCountdownServiceClient(@ApplicationContext context: Context) = CountdownServiceClient(context)
}