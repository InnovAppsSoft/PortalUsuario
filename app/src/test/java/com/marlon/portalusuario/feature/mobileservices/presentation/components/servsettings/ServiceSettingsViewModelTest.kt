package com.marlon.portalusuario.feature.mobileservices.presentation.components.servsettings

import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.ServiceType
import com.marlon.portalusuario.testhelpers.FakeSimCardCollector
import com.marlon.portalusuario.testhelpers.FakeUssdExecute
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = android.app.Application::class)
class ServiceSettingsViewModelTest {
    private val simCard =
        FakeSimCardCollector.createSimCard(
            phoneNumber = "12345678",
            slotIndex = 0,
        )
    private val mobileService =
        MobileService(
            id = "service1",
            lte = false,
            advanceBalance = "0.00",
            status = "Active",
            lockDate = "",
            deletionDate = "",
            saleDate = "",
            internet = true,
            plans = emptyList(),
            bonuses = emptyList(),
            currency = "CUP",
            phoneNumber = "12345678",
            mainBalance = "100.00",
            consumptionRate = false,
            slotIndex = 0,
            type = ServiceType.Remote,
        )

    @Test
    fun `onEvent OnTurnConsumptionRate calls ussdExecute`() {
        val ussdExecute = FakeUssdExecute()
        val viewModel =
            ServiceSettingsViewModel(
                simCardCollector = FakeSimCardCollector(listOf(simCard)),
                ussdExecute = ussdExecute,
            )

        viewModel.onEvent(ServiceSettingsEvent.OnTurnConsumptionRate(simCard, mobileService))

        assertEquals(simCard, ussdExecute.lastSimCard())
    }

    @Test
    fun `simCards is populated from collector`() {
        val viewModel =
            ServiceSettingsViewModel(
                simCardCollector = FakeSimCardCollector(listOf(simCard)),
                ussdExecute = FakeUssdExecute(),
            )

        assertEquals(1, viewModel.simCards.size)
        assertEquals(simCard, viewModel.simCards.first())
    }
}
