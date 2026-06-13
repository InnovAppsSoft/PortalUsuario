package com.marlon.portalusuario.feature.mobileservices.presentation

import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.MobServPreferences
import com.marlon.portalusuario.domain.model.ServiceType
import com.marlon.portalusuario.domain.model.SlotIndexInfo
import com.marlon.portalusuario.testhelpers.FakeMobServicesPreferences
import com.marlon.portalusuario.testhelpers.FakeSimCardCollector
import com.marlon.portalusuario.testhelpers.FakeUserRepository
import com.marlon.portalusuario.testhelpers.MainCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class MobileServicesViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val simCard = FakeSimCardCollector.createSimCard(
        phoneNumber = "12345678",
        slotIndex = 0,
    )
    private val mobileService = MobileService(
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
        lastUpdated = System.currentTimeMillis(),
    )
    private val mobServPreferencesData = MobServPreferences(
        slotIndexInfoList = listOf(SlotIndexInfo(0, "12345678")),
        mssId = "service1",
    )

    @Test
    fun `init sets currentServiceId from preferences`() {
        val viewModel = MobileServicesViewModel(
            mobServicesPreferences = FakeMobServicesPreferences(mobServPreferencesData),
            repository = FakeUserRepository(listOf(mobileService)),
            simCardCollector = FakeSimCardCollector(listOf(simCard)),
        )

        assertEquals("service1", viewModel.state.value.currentServiceId)
    }

    @Test
    fun `onEvent OnShowServiceSettings updates state`() {
        val viewModel = MobileServicesViewModel(
            mobServicesPreferences = FakeMobServicesPreferences(),
            repository = FakeUserRepository(),
            simCardCollector = FakeSimCardCollector(),
        )

        viewModel.onEvent(MobileServicesEvent.OnShowServiceSettings)

        assertEquals(true, viewModel.state.value.isServiceSettingsVisible)
    }

    @Test
    fun `onEvent OnHideServiceSettings updates state`() {
        val viewModel = MobileServicesViewModel(
            mobServicesPreferences = FakeMobServicesPreferences(),
            repository = FakeUserRepository(),
            simCardCollector = FakeSimCardCollector(),
        )

        viewModel.onEvent(MobileServicesEvent.OnShowServiceSettings)
        viewModel.onEvent(MobileServicesEvent.OnHideServiceSettings)

        assertEquals(false, viewModel.state.value.isServiceSettingsVisible)
    }

    @Test
    fun `onEvent OnShowSImCardsSettings updates state`() {
        val viewModel = MobileServicesViewModel(
            mobServicesPreferences = FakeMobServicesPreferences(),
            repository = FakeUserRepository(),
            simCardCollector = FakeSimCardCollector(),
        )

        viewModel.onEvent(MobileServicesEvent.OnShowSImCardsSettings)

        assertEquals(true, viewModel.state.value.isSimCardsSettingsVisible)
    }

    @Test
    fun `onEvent OnHideSImCardsSettings updates state`() {
        val viewModel = MobileServicesViewModel(
            mobServicesPreferences = FakeMobServicesPreferences(),
            repository = FakeUserRepository(),
            simCardCollector = FakeSimCardCollector(),
        )

        viewModel.onEvent(MobileServicesEvent.OnShowSImCardsSettings)
        viewModel.onEvent(MobileServicesEvent.OnHideSImCardsSettings)

        assertEquals(false, viewModel.state.value.isSimCardsSettingsVisible)
    }

    @Test
    fun `onEvent OnErrorDismiss clears error`() {
        val viewModel = MobileServicesViewModel(
            mobServicesPreferences = FakeMobServicesPreferences(),
            repository = FakeUserRepository(),
            simCardCollector = FakeSimCardCollector(),
        )

        viewModel.onEvent(MobileServicesEvent.OnErrorDismiss)

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `onEvent OnChangeCurrentMobileService updates serviceId and persists`() {
        val mobServicesPreferences = FakeMobServicesPreferences(mobServPreferencesData)
        val viewModel = MobileServicesViewModel(
            mobServicesPreferences = mobServicesPreferences,
            repository = FakeUserRepository(),
            simCardCollector = FakeSimCardCollector(),
        )

        viewModel.onEvent(MobileServicesEvent.OnChangeCurrentMobileService("service2"))

        assertEquals("service2", viewModel.state.value.currentServiceId)
    }
}
