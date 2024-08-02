package com.marlon.portalusuario.di

import android.content.Context
import androidx.room.Room
import com.marlon.portalusuario.data.ServicesDB
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.data.source.AuthService
import com.marlon.portalusuario.data.source.UserService
import com.marlon.portalusuario.data.user.UserRepositoryImpl
import com.marlon.portalusuario.domain.data.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.nauta.api.NautaApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideAppPreference(@ApplicationContext context: Context) =
        AppPreferences(context)

    @Singleton
    @Provides
    fun provideServicesDB(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            ServicesDB::class.java,
            "services_db"
        ).build()

    @Singleton
    @Provides
    fun provideServicesDao(servicesDB: ServicesDB) = servicesDB.servicesDao

    @Singleton
    @Provides
    fun provideNautaService() = UserService(NautaApi.nautaService)

    @Singleton
    @Provides
    fun provideAuthService() = AuthService(NautaApi.nautaService)

    @Singleton
    @Provides
    fun provideUserRepository(
        userService: UserService,
        servicesDao: ServicesDao
    ): UserRepository =
        UserRepositoryImpl(userService, servicesDao)

    @Singleton
    @Provides
    fun provideSimCollector(@ApplicationContext context: Context) =
        SimCardCollector.Builder().build(context)
}
