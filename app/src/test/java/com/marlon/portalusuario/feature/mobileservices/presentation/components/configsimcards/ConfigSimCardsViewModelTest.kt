package com.marlon.portalusuario.feature.mobileservices.presentation.components.configsimcards

import com.marlon.portalusuario.testhelpers.FakeMobServicesPreferences
import com.marlon.portalusuario.testhelpers.FakeSimCardCollector
import com.marlon.portalusuario.testhelpers.FakeUserRepository
import com.marlon.portalusuario.testhelpers.MainCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class ConfigSimCardsViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val simCard =
        FakeSimCardCollector.createSimCard(
            phoneNumber = "12345678",
            slotIndex = 0,
        )

    @Test
    fun `state initializes with first sim card`() {
        val viewModel =
            ConfigSimCardsViewModel(
                preferences = FakeMobServicesPreferences(),
                repository = FakeUserRepository(),
                simCardCollector = FakeSimCardCollector(listOf(simCard)),
            )

        assertEquals(0, viewModel.state.currentSimCard.slotIndex)
        assertEquals(1, viewModel.state.simCards.size)
        assertEquals(
            0,
            viewModel.state.simCards
                .first()
                .slotIndex,
        )
    }

    @Test
    fun `OnChangedPhoneNumber updates phoneNumber in state`() {
        val viewModel =
            ConfigSimCardsViewModel(
                preferences = FakeMobServicesPreferences(),
                repository = FakeUserRepository(),
                simCardCollector = FakeSimCardCollector(listOf(simCard)),
            )

        viewModel.onEvent(ConfigSimCardsEvent.OnChangedPhoneNumber("87654321"))

        assertEquals("87654321", viewModel.state.phoneNumber)
    }

    @Test
    fun `OnChangedPhoneNumber with invalid number is invalid`() {
        val viewModel =
            ConfigSimCardsViewModel(
                preferences = FakeMobServicesPreferences(),
                repository = FakeUserRepository(),
                simCardCollector = FakeSimCardCollector(listOf(simCard)),
            )

        viewModel.onEvent(ConfigSimCardsEvent.OnChangedPhoneNumber("123"))

        assertFalse(viewModel.isPhoneNumberValid)
    }
}
