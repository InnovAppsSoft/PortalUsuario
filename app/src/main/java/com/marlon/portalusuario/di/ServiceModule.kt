package com.marlon.portalusuario.di

import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.trafficbubble.FloatingBubbleViewModel
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
    fun provideFloatingBubbleViewModel(preferencesManager: AppPreferencesManager, repository: UserRepository) =
        FloatingBubbleViewModel(preferencesManager, repository)
}