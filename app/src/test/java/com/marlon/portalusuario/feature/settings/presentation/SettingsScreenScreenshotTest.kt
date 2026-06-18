package com.marlon.portalusuario.feature.settings.presentation

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.marlon.portalusuario.data.preferences.AppPreferencesViewModel
import com.marlon.portalusuario.domain.model.AppSettings
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.testhelpers.FakeAppPreferencesManager
import com.marlon.portalusuario.core.theme.PortalUsuarioTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = Application::class)
class SettingsScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun settingsScreen() {
        val preferences =
            FakeAppPreferencesManager(
                AppSettings(
                    modeNight = ModeNight.FOLLOW_SYSTEM,
                    isDynamicColor = true,
                    isShowingTrafficBubble = true,
                    isShowingAccountBalanceOnTrafficBubble = true,
                    isShowingDataBalanceOnTrafficBubble = false,
                    isIntroOpened = true,
                ),
            )
        val viewModel = AppPreferencesViewModel(preferences)

        composeTestRule.setContent {
            PortalUsuarioTheme {
                SettingsScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
