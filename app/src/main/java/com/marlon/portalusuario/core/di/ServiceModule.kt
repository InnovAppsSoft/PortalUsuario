package com.marlon.portalusuario.core.di

import com.marlon.portalusuario.data.preferences.IAppPreferencesManager
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.feature.trafficbubble.FloatingBubbleViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {
    @Provides
    @ServiceScoped
    fun provideFloatingBubbleViewModel(
        preferencesManager: IAppPreferencesManager,
        repository: UserRepository,
    ) = FloatingBubbleViewModel(preferencesManager, repository)
}
