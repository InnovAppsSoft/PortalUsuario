package com.marlon.portalusuario.feature.splash.presentation.screen

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.marlon.portalusuario.domain.model.AppSettings
import com.marlon.portalusuario.feature.splash.presentation.SplashViewModel
import com.marlon.portalusuario.testhelpers.FakeAppPreferencesManager
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SplashScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun splashScreen() {
        val preferences =
            FakeAppPreferencesManager(
                AppSettings(isIntroOpened = true),
            )
        val viewModel = SplashViewModel(preferences)

        composeTestRule.setContent {
            PortalUsuarioTheme {
                SplashScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
