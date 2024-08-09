package com.marlon.portalusuario.presentation.mobileservices.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.ServiceType
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent.OnHideSImCardsSettings
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent.OnHideServiceSettings
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent.OnShowServiceSettings
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesState
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesViewModel
import com.marlon.portalusuario.presentation.mobileservices.components.BalanceCard
import com.marlon.portalusuario.presentation.mobileservices.components.BonusSection
import com.marlon.portalusuario.presentation.mobileservices.components.MobileServiceSelector
import com.marlon.portalusuario.presentation.mobileservices.components.PlansSection
import com.marlon.portalusuario.presentation.mobileservices.components.configsimcards.ConfigSimCardsBottomSheet
import com.marlon.portalusuario.presentation.mobileservices.components.servsettings.ServiceSettingsBottomSheet
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.util.Utils.fixDateFormat
import io.github.suitetecsa.sdk.android.model.SimCard

private const val TAG = "MobileServicesScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileServicesScreen(viewModel: MobileServicesViewModel = hiltViewModel()) {
    val pullToRefreshState = rememberPullToRefreshState()
    val mobServices by viewModel.mobileServices.collectAsState()
    val preferences by viewModel.preferences.collectAsState()

    Log.d(TAG, "MobileServicesScreen: ${viewModel.simCards}")

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(key1 = true) {
            if (!viewModel.state.value.isLoading) { viewModel.onEvent(MobileServicesEvent.OnUpdate) }
        }
    }

    LaunchedEffect(key1 = viewModel.state.value.isLoading) {
        if (viewModel.state.value.isLoading) pullToRefreshState.startRefresh() else pullToRefreshState.endRefresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        mobServices.takeIf { it.isNotEmpty() }?.let { services ->
            val serviceId = preferences.mssId ?: services.first().id
            ScreenContent(
                services = services,
                currentServiceId = serviceId,
                state = viewModel.state.value,
                onEvent = viewModel::onEvent,
                simCards = viewModel.simCards
            )
        }

        if (viewModel.state.value.isSimCardsSettingsVisible) {
            ConfigSimCardsBottomSheet(onDismiss = { viewModel.onEvent(OnHideSImCardsSettings) })
        }

        PullToRefreshContainer(state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun ScreenContent(
    services: List<MobileService>,
    currentServiceId: String,
    state: MobileServicesState,
    onEvent: (MobileServicesEvent) -> Unit,
    simCards: List<SimCard> = listOf(),
) {
    val service = services.first { it.id == currentServiceId }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        MobileServiceSelector(
            services = services,
            serviceSelected = service,
            onServiceSelected = { onEvent(MobileServicesEvent.OnChangeCurrentMobileService(it.id)) },
            simCards = simCards,
            onShowServiceSettings = { onEvent(OnShowServiceSettings) }
        )
        Spacer(modifier = Modifier.height(4.dp))
        BalanceCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            balanceCredit = "${service.mainBalance} ${service.currency}",
            lockDate = service.lockDate,
            deletionDate = service.deletionDate,
            isSimPaired = service.slotIndex != -1,
            onAddBalance = { /*TODO*/ },
            onSendBalance = { /*TODO*/ }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PlansSection(service = service)
        Spacer(modifier = Modifier.height(8.dp))
        BonusSection(service = service)
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (state.isServiceSettingsVisible) {
        ServiceSettingsBottomSheet(mobService = service, onDismiss = { onEvent(OnHideServiceSettings) })
    }
}

@Preview
@Composable
private fun ScreenContentPreview() {
    PortalUsuarioTheme {
        Surface {
            ScreenContent(
                services = listOf(
                    MobileService(
                        id = "5351872843",
                        lte = false,
                        advanceBalance = "0",
                        status = "Activo",
                        lockDate = "26/6/2024".fixDateFormat(),
                        deletionDate = "26/7/2024".fixDateFormat(),
                        saleDate = "19/8/2022".fixDateFormat(),
                        internet = true,
                        plans = listOf(
                            MobilePlan("01:23:55", "MINUTOS", "01/08/2024".replace("/", "-")),
                            MobilePlan("1560", "SMS", "01/08/2024".replace("/", "-")),
                            MobilePlan("719.55 MB", "DATOS", "01/08/2024".replace("/", "-")),
                            MobilePlan("0.00 B", "DATOS LTE", "01/08/2024".replace("/", "-")),
                        ),
                        bonuses = listOf(
                            MobileBonus("296.61 MB", "", "DATOS NACIONALES", "01/08/2024".replace("/", "-")),
                            MobileBonus(
                                "0",
                                "01/08/2024".replace("/", "-"),
                                "DATOS LTE",
                                "01/08/2024".replace("/", "-")
                            ),
                        ),
                        currency = "CUP",
                        phoneNumber = "51872843",
                        mainBalance = "10.00",
                        consumptionRate = true,
                        slotIndex = -1,
                        type = ServiceType.Local
                    )
                ),
                currentServiceId = "5351872843",
                state = MobileServicesState(),
                onEvent = {},
                simCards = listOf()
            )
        }
    }
}
