package com.marlon.portalusuario.presentation.mobileservices.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent.OnHideSImCardsSettings
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent.OnHideServiceSettings
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent.OnShowServiceSettings
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesState
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesViewModel
import com.marlon.portalusuario.presentation.mobileservices.components.BalanceSection
import com.marlon.portalusuario.presentation.mobileservices.components.BonusSection
import com.marlon.portalusuario.presentation.mobileservices.components.MobileServiceSelector
import com.marlon.portalusuario.presentation.mobileservices.components.PlansSection
import com.marlon.portalusuario.presentation.mobileservices.components.configsimcards.ConfigSimCardsBottomSheet
import com.marlon.portalusuario.presentation.mobileservices.components.servsettings.ServiceSettingsBottomSheet
import com.marlon.portalusuario.ui.components.ErrorDialog
import io.github.suitetecsa.sdk.android.model.SimCard

private const val TAG = "MobileServicesScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileServicesScreen(viewModel: MobileServicesViewModel = hiltViewModel()) {
    val mobServices by viewModel.mobileServices.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    Log.d(TAG, "MobileServicesScreen: ${viewModel.simCards}")

    PullToRefreshBox(
        isRefreshing = viewModel.state.value.isLoading,
        onRefresh = { viewModel.onEvent(MobileServicesEvent.OnUpdate) },
        modifier = Modifier
            .fillMaxSize()
    ) {
        mobServices.takeIf { it.isNotEmpty() }?.let { services ->
            val serviceId = preferences.mssId ?: services.first().id.also {
                viewModel.onEvent(MobileServicesEvent.OnChangeCurrentMobileService(it))
            }
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

        viewModel.state.value.error?.let {
            ErrorDialog(it) { viewModel.onEvent(MobileServicesEvent.OnErrorDismiss) }
        }
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
        BalanceSection(
            modifier = Modifier.padding(horizontal = 16.dp),
            balanceCredit = "${service.mainBalance} ${service.currency}",
            lockDate = service.lockDate,
            deletionDate = service.deletionDate,
            isSimPaired = service.slotIndex != -1,
            onAddBalance = { /*TODO*/ },
            onSendBalance = { /*TODO*/ }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PlansSection(plans = service.plans)
        service.bonuses.takeIf { it.isNotEmpty() }?.let {
            Spacer(modifier = Modifier.height(8.dp))
            BonusSection(bonuses = it)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (state.isServiceSettingsVisible) {
        ServiceSettingsBottomSheet(
            mobService = service,
            onDismiss = { onEvent(OnHideServiceSettings) }
        )
    }
}
