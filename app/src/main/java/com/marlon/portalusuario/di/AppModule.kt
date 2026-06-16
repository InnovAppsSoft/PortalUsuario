package com.marlon.portalusuario.di

import android.content.Context
import androidx.room.Room
import com.marlon.portalusuario.data.ServicesDB
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.mappers.ClientProfileApiToEntityMapper
import com.marlon.portalusuario.data.mappers.ClientProfileEntityToDomainMapper
import com.marlon.portalusuario.data.mappers.MobBonusApiToModelMapper
import com.marlon.portalusuario.data.mappers.MobPlanApiToModelMapper
import com.marlon.portalusuario.data.mappers.MobServiceApiToEntityMapper
import com.marlon.portalusuario.data.mappers.MobServiceEntityToDomainMapper
import com.marlon.portalusuario.data.mappers.NavServApiToEntityMapper
import com.marlon.portalusuario.data.mappers.NavServEntityToDomainMapper
import com.marlon.portalusuario.data.notifications.PunRepositoryImpl
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.data.preferences.IAppPreferencesManager
import com.marlon.portalusuario.data.preferences.IMobServicesPreferences
import com.marlon.portalusuario.data.preferences.MobServicesPreferences
import com.marlon.portalusuario.data.une.UneRepositoryImpl
import com.marlon.portalusuario.data.user.UserAccountRepositoryImpl
import com.marlon.portalusuario.data.user.UserRepositoryImpl
import com.marlon.portalusuario.domain.data.PunRepository
import com.marlon.portalusuario.domain.data.UneRepository
import com.marlon.portalusuario.domain.data.UserAccountRepository
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.feature.mobileservices.domain.usecases.IUssdExecute
import com.marlon.portalusuario.feature.mobileservices.domain.usecases.UssdExecute
import com.marlon.portalusuario.util.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.suitetecsa.sdk.android.SimCardCollector
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context,
    ) = NetworkConnectivityObserver(context)

    @Singleton
    @Provides
    fun provideAppPreference(
        @ApplicationContext context: Context,
    ): IAppPreferencesManager = AppPreferencesManager(context)

    @Singleton
    @Provides
    fun provideServicesDB(
        @ApplicationContext context: Context,
    ) = Room
        .databaseBuilder(
            context,
            ServicesDB::class.java,
            "services_db",
        ).addMigrations(ServicesDB.MIGRATION_1_2)
        .build()

    @Singleton
    @Provides
    fun provideServicesDao(servicesDB: ServicesDB) = servicesDB.servicesDao

    @Singleton
    @Provides
    fun provideMobServicesPreferences(
        @ApplicationContext context: Context,
    ): IMobServicesPreferences = MobServicesPreferences(context)

    @Singleton
    @Provides
    fun provideMobPlanApiToDomainMapper() = MobPlanApiToModelMapper()

    @Singleton
    @Provides
    fun provideMobBonusApiToDomainMapper() = MobBonusApiToModelMapper()

    @Singleton
    @Provides
    fun provideMobServApiToEntityMapper(
        preferences: IMobServicesPreferences,
        planApiToModelMapper: MobPlanApiToModelMapper,
        bonusApiToModelMapper: MobBonusApiToModelMapper,
    ) = MobServiceApiToEntityMapper(preferences, planApiToModelMapper, bonusApiToModelMapper)

    @Singleton
    @Provides
    fun provideMobServEntityToDomainMapper() = MobServiceEntityToDomainMapper()

    @Singleton
    @Provides
    fun provideClientProfileApiToEntityMapper() = ClientProfileApiToEntityMapper()

    @Singleton
    @Provides
    fun provideClientProfileEntityToDomainMapper() = ClientProfileEntityToDomainMapper()

    @Singleton
    @Provides
    fun provideNavServApiToEntityMapper() = NavServApiToEntityMapper()

    @Singleton
    @Provides
    fun provideNavServEntityToDomainMapper() = NavServEntityToDomainMapper()

    @Singleton
    @Provides
    fun provideUserRepository(
        servicesDao: ServicesDao,
        mobServiceEntityToDomainMapper: MobServiceEntityToDomainMapper,
        clientProfileEntityToDomainMapper: ClientProfileEntityToDomainMapper,
        navServEntityToDomainMapper: NavServEntityToDomainMapper,
    ): UserRepository =
        UserRepositoryImpl(
            dao = servicesDao,
            mobServiceEntityToDomainMapper = mobServiceEntityToDomainMapper,
            clientProfileEntityToDomainMapper = clientProfileEntityToDomainMapper,
            navServEntityToDomainMapper = navServEntityToDomainMapper,
        )

    @Singleton
    @Provides
    fun provideUssdExecute(impl: UssdExecute): IUssdExecute = impl

    @Singleton
    @Provides
    fun provideSimCollector(
        @ApplicationContext context: Context,
    ) = SimCardCollector.Builder().build(context)

    @Singleton
    @Provides
    fun provideUneRepository(dao: ServicesDao): UneRepository = UneRepositoryImpl(dao)

    @Singleton
    @Provides
    fun providePunRepository(
        dao: ServicesDao,
        @ApplicationContext context: Context,
    ): PunRepository = PunRepositoryImpl(dao, context)

    @Singleton
    @Provides
    fun provideUserAccountRepository(dao: ServicesDao): UserAccountRepository = UserAccountRepositoryImpl(dao)
}
