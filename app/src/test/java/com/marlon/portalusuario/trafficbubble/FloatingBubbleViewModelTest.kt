package com.marlon.portalusuario.trafficbubble

import com.marlon.portalusuario.domain.model.AppSettings
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.ServiceType
import com.marlon.portalusuario.testhelpers.FakeAppPreferencesManager
import com.marlon.portalusuario.testhelpers.FakeUserRepository
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
class FloatingBubbleViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val appSettings =
        AppSettings(
            isShowingAccountBalanceOnTrafficBubble = true,
            isShowingDataBalanceOnTrafficBubble = true,
        )
    private val dataPlan = MobilePlan(data = "1.5 GB", type = "DATOS", expires = "31/12/2026")
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
            plans = listOf(dataPlan),
            bonuses = emptyList(),
            currency = "CUP",
            phoneNumber = "12345678",
            mainBalance = "150.00",
            consumptionRate = false,
            slotIndex = 0,
            type = ServiceType.Remote,
        )

    @Test
    fun `init collects preferences and updates account balance visibility`() =
        runTest {
            val viewModel =
                FloatingBubbleViewModel(
                    appSettings = FakeAppPreferencesManager(appSettings),
                    mobileServicesRepository = FakeUserRepository(listOf(mobileService)),
                )

            val state = viewModel.state.first()
            assertEquals(true, state.isShowingAccountBalance)
            assertEquals(true, state.isShowingDataBalance)
        }

    @Test
    fun `init collects mobile services and updates account balance`() =
        runTest {
            val viewModel =
                FloatingBubbleViewModel(
                    appSettings = FakeAppPreferencesManager(appSettings),
                    mobileServicesRepository = FakeUserRepository(listOf(mobileService)),
                )

            val state = viewModel.state.first()
            assertEquals("150.00 CUP", state.accountBalance)
        }

    @Test
    fun `init collects mobile services and updates data balance for DATA plan`() =
        runTest {
            val viewModel =
                FloatingBubbleViewModel(
                    appSettings = FakeAppPreferencesManager(appSettings),
                    mobileServicesRepository = FakeUserRepository(listOf(mobileService)),
                )

            val state = viewModel.state.first()
            assertEquals("1.5 GB", state.dataBalance)
        }

    @Test
    fun `onEvent OnSwitchingAccountBalanceVisibility updates state`() =
        runTest {
            val viewModel =
                FloatingBubbleViewModel(
                    appSettings = FakeAppPreferencesManager(appSettings),
                    mobileServicesRepository = FakeUserRepository(listOf(mobileService)),
                )

            viewModel.onEvent(FloatingBubbleEvent.OnSwitchingAccountBalanceVisibility(false))

            val state = viewModel.state.first()
            assertEquals(false, state.isShowingAccountBalance)
        }

    @Test
    fun `onEvent OnSwitchingDataBalanceVisibility updates state`() =
        runTest {
            val viewModel =
                FloatingBubbleViewModel(
                    appSettings = FakeAppPreferencesManager(appSettings),
                    mobileServicesRepository = FakeUserRepository(listOf(mobileService)),
                )

            viewModel.onEvent(FloatingBubbleEvent.OnSwitchingDataBalanceVisibility(false))

            val state = viewModel.state.first()
            assertEquals(false, state.isShowingDataBalance)
        }

    @Test
    fun `onEvent OnCalculateDataUsage updates lastTime`() =
        runTest {
            val viewModel =
                FloatingBubbleViewModel(
                    appSettings = FakeAppPreferencesManager(appSettings),
                    mobileServicesRepository = FakeUserRepository(listOf(mobileService)),
                )

            viewModel.onEvent(FloatingBubbleEvent.OnCalculateDataUsage)

            val state = viewModel.state.first()
            assertEquals(true, state.lastTime > 0)
        }
}
