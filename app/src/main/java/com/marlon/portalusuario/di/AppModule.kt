package com.marlon.portalusuario.di

import android.content.Context
import androidx.room.Room
import com.marlon.portalusuario.data.ServicesDB
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.mappers.MobServMapper
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.data.preferences.MobServicesPreferences
import com.marlon.portalusuario.data.preferences.SessionStorage
import com.marlon.portalusuario.data.source.AuthService
import com.marlon.portalusuario.data.source.UserApiDataSource
import com.marlon.portalusuario.data.user.UserRepositoryImpl
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.presentation.mobileservices.usecases.RefreshAuthToken
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
    fun provideSessionStorage(@ApplicationContext context: Context) =
        SessionStorage(context)

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
    fun provideNautaService(
        refreshAuthToken: RefreshAuthToken,
        sessionStorage: SessionStorage,
    ) = UserApiDataSource(NautaApi.nautaService, refreshAuthToken, sessionStorage)

    @Singleton
    @Provides
    fun provideAuthService() = AuthService(NautaApi.nautaService)

    @Singleton
    @Provides
    fun provideMobServicesPreferences(@ApplicationContext context: Context) =
        MobServicesPreferences(context)

    @Singleton
    @Provides
    fun provideMobServMapper(preferences: MobServicesPreferences) =
        MobServMapper(preferences)

    @Singleton
    @Provides
    fun provideUserRepository(
        userApiDataSource: UserApiDataSource,
        servicesDao: ServicesDao,
        mobServMapper: MobServMapper
    ): UserRepository =
        UserRepositoryImpl(userApiDataSource, servicesDao, mobServMapper)

    @Singleton
    @Provides
    fun provideSimCollector(@ApplicationContext context: Context) =
        SimCardCollector.Builder().build(context)
}
