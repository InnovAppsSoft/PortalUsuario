package com.marlon.portalusuario.data.preferences

import com.marlon.portalusuario.domain.model.AppSettings
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.testhelpers.FakeAppPreferencesManager
import com.marlon.portalusuario.testhelpers.MainCoroutineRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AppPreferencesViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val appSettings =
        AppSettings(
            modeNight = ModeNight.FOLLOW_SYSTEM,
            isShowingTrafficBubble = false,
            isIntroOpened = false,
        )

    @Test
    fun `state emits AppSettings from manager`() =
        runTest {
            val manager = FakeAppPreferencesManager(appSettings)
            val viewModel = AppPreferencesViewModel(manager)

            val result = viewModel.state.first()
            assertEquals(appSettings, result)
        }

    @Test
    fun `onEvent OnUpdateIsShowingTrafficBubble updates state`() =
        runTest {
            val manager = FakeAppPreferencesManager()
            val viewModel = AppPreferencesViewModel(manager)

            viewModel.onEvent(AppPreferencesEvent.OnUpdateIsShowingTrafficBubble(true))

            val result = viewModel.state.first()
            assertEquals(true, result.isShowingTrafficBubble)
        }

    @Test
    fun `onEvent OnUpdateModeNight updates state`() =
        runTest {
            val manager = FakeAppPreferencesManager()
            val viewModel = AppPreferencesViewModel(manager)

            viewModel.onEvent(AppPreferencesEvent.OnUpdateModeNight(ModeNight.YES))

            val result = viewModel.state.first()
            assertEquals(ModeNight.YES, result.modeNight)
        }

    @Test
    fun `onEvent OnUpdateDynamicColor updates state`() =
        runTest {
            val manager = FakeAppPreferencesManager()
            val viewModel = AppPreferencesViewModel(manager)

            viewModel.onEvent(AppPreferencesEvent.OnUpdateDynamicColor(false))

            val result = viewModel.state.first()
            assertEquals(false, result.isDynamicColor)
        }

    @Test
    fun `onEvent OnUpdateIsIntroOpened updates state`() =
        runTest {
            val manager = FakeAppPreferencesManager()
            val viewModel = AppPreferencesViewModel(manager)

            viewModel.onEvent(AppPreferencesEvent.OnUpdateIsIntroOpened(true))

            val result = viewModel.state.first()
            assertEquals(true, result.isIntroOpened)
        }

    @Test
    fun `onEvent OnSwitchingAccountBalanceVisibility updates state`() =
        runTest {
            val manager = FakeAppPreferencesManager()
            val viewModel = AppPreferencesViewModel(manager)

            viewModel.onEvent(
                AppPreferencesEvent.OnSwitchingAccountBalanceOnTrafficBubbleVisibility(true),
            )

            val result = viewModel.state.first()
            assertEquals(true, result.isShowingAccountBalanceOnTrafficBubble)
        }

    @Test
    fun `onEvent OnSwitchingDataBalanceVisibility updates state`() =
        runTest {
            val manager = FakeAppPreferencesManager()
            val viewModel = AppPreferencesViewModel(manager)

            viewModel.onEvent(
                AppPreferencesEvent.OnSwitchingDataBalanceOnTrafficBubbleVisibility(true),
            )

            val result = viewModel.state.first()
            assertEquals(true, result.isShowingDataBalanceOnTrafficBubble)
        }

    @Test
    fun `onEvent OnUpdateSkippedLogin does not change state`() =
        runTest {
            val manager = FakeAppPreferencesManager()
            val viewModel = AppPreferencesViewModel(manager)

            val before = viewModel.state.first()
            viewModel.onEvent(AppPreferencesEvent.OnUpdateSkippedLogin(true))
            val after = viewModel.state.first()

            assertEquals(before, after)
        }
}
