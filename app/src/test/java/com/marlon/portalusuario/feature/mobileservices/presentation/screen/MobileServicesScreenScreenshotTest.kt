package com.marlon.portalusuario.feature.mobileservices.presentation.screen

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.ServiceType
import com.marlon.portalusuario.feature.mobileservices.presentation.MobileServicesState
import com.marlon.portalusuario.core.theme.PortalUsuarioTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = Application::class)
class MobileServicesScreenScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeService =
        MobileService(
            id = "1",
            lte = true,
            advanceBalance = "0.00",
            status = "Activo",
            lockDate = "2026-01-01",
            deletionDate = "2026-06-01",
            saleDate = "2024-01-01",
            internet = true,
            plans =
                listOf(
                    MobilePlan("30 GB", "Nauta Hogar", "2026-12-31"),
                ),
            bonuses =
                listOf(
                    MobileBonus("5 GB", "2026-05-01", "Nauta Móvil", "2026-06-01"),
                ),
            currency = "CUP",
            phoneNumber = "+5351234567",
            mainBalance = "150.00",
            consumptionRate = false,
            slotIndex = 0,
            type = ServiceType.Local,
            lastUpdated = 1000,
        )

    @Test
    fun mobileServicesScreen() {
        composeTestRule.setContent {
            PortalUsuarioTheme {
                ScreenContent(
                    services = listOf(fakeService),
                    currentServiceId = "1",
                    state = MobileServicesState(currentServiceId = "1"),
                    onEvent = {},
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
