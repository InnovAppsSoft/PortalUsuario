package cu.suitetecsa.nautanav.di

import android.content.Context
import androidx.room.Room
import cu.suitetecsa.nautanav.data.UserDao
import cu.suitetecsa.nautanav.data.network.NautaService
import cu.suitetecsa.nautanav.data.network.UserDb
import cu.suitetecsa.nautanav.data.network.UserRepository
import cu.suitetecsa.nautanav.data.network.UserRepositoryImpl
import cu.suitetecsa.nautanav.service.CountdownServiceClient
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
        Room.databaseBuilder(
            context,
            UserDb::class.java,
            cu.suitetecsa.nautanav.commons.USER_TABLE
        ).build()

    @Provides
    fun provideUserDao(userDb: UserDb) = userDb.userDao

    @Provides
    fun providerUserRepository(userDao: UserDao, nautaService: NautaService): UserRepository =
        UserRepositoryImpl(
            userDao = userDao,
            service = nautaService
        )

    @Singleton
    @Provides
    fun provideNautaSharedPreferences(@ApplicationContext context: Context): cu.suitetecsa.nautanav.util.Pref =
        cu.suitetecsa.nautanav.util.Pref(context)

    @Singleton
    @Provides
    fun provideCountdownServiceClient(@ApplicationContext context: Context) = CountdownServiceClient(context)
}
