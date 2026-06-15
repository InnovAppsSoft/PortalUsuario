package com.marlon.portalusuario.feature.splash.presentation

import com.marlon.portalusuario.domain.model.AppSettings
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.testhelpers.FakeAppPreferencesManager
import com.marlon.portalusuario.testhelpers.MainCoroutineRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = android.app.Application::class)
class SplashViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val appSettings = AppSettings(
        modeNight = ModeNight.NO,
        isDynamicColor = false,
        isIntroOpened = true,
    )

    @Test
    fun `pref emits AppSettings from preferences`() = runTest {
        val preferences = FakeAppPreferencesManager(appSettings)
        val viewModel = SplashViewModel(preferences)

        val result = viewModel.pref.first()
        assertEquals(appSettings, result)
    }
}
