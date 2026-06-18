package com.marlon.portalusuario.feature.mobileservices.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marlon.portalusuario.core.components.EmptyState
import com.marlon.portalusuario.core.components.ErrorDialog
import com.marlon.portalusuario.core.components.PrettyCard
import com.marlon.portalusuario.core.theme.PortalUsuarioTheme
import com.marlon.portalusuario.domain.model.MobServPreferences
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.ServiceType
import com.marlon.portalusuario.feature.mobileservices.presentation.MobileServicesEvent
import com.marlon.portalusuario.feature.mobileservices.presentation.MobileServicesEvent.OnHideSImCardsSettings
import com.marlon.portalusuario.feature.mobileservices.presentation.MobileServicesEvent.OnHideServiceSettings
import com.marlon.portalusuario.feature.mobileservices.presentation.MobileServicesEvent.OnShowServiceSettings
import com.marlon.portalusuario.feature.mobileservices.presentation.MobileServicesState
import com.marlon.portalusuario.feature.mobileservices.presentation.MobileServicesViewModel
import com.marlon.portalusuario.feature.mobileservices.presentation.components.BalanceSection
import com.marlon.portalusuario.feature.mobileservices.presentation.components.BonusSection
import com.marlon.portalusuario.feature.mobileservices.presentation.components.MobileServiceSelector
import com.marlon.portalusuario.feature.mobileservices.presentation.components.PlansSection
import com.marlon.portalusuario.feature.mobileservices.presentation.components.configsimcards.ConfigSimCardsBottomSheet
import com.marlon.portalusuario.feature.mobileservices.presentation.components.servsettings.ServiceSettingsBottomSheet
import io.github.suitetecsa.sdk.android.model.SimCard

private const val TAG = "MobileServicesScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileServicesScreen(
    onNavigateToServicios: () -> Unit = {},
    viewModel: MobileServicesViewModel = hiltViewModel(),
) {
    val mobServices by viewModel.mobileServices.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    MobileServicesScreen(
        mobServices = mobServices,
        preferences = preferences,
        state = viewModel.state.value,
        simCards = viewModel.simCards,
        onEvent = viewModel::onEvent,
        onNavigateToServicios = onNavigateToServicios,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileServicesScreen(
    mobServices: List<MobileService>,
    preferences: MobServPreferences,
    state: MobileServicesState,
    simCards: List<SimCard>,
    onEvent: (MobileServicesEvent) -> Unit,
    onNavigateToServicios: () -> Unit = {},
) {
    Log.d(TAG, "MobileServicesScreen: $simCards")

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { onEvent(MobileServicesEvent.OnUpdate) },
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                mobServices.isEmpty() && state.isLoading -> LoadingSkeleton()
                mobServices.isEmpty() -> EmptyState(message = "No hay servicios móviles disponibles")
                else -> {
                    val serviceId =
                        preferences.mssId ?: mobServices.first().id.also {
                            onEvent(MobileServicesEvent.OnChangeCurrentMobileService(it))
                        }
                    ScreenContent(
                        services = mobServices,
                        currentServiceId = serviceId,
                        state = state,
                        onEvent = onEvent,
                        simCards = simCards,
                    )
                }
            }

            if (state.isSimCardsSettingsVisible) {
                ConfigSimCardsBottomSheet(onDismiss = { onEvent(OnHideSImCardsSettings) })
            }

            state.error?.let {
                ErrorDialog(it) { onEvent(MobileServicesEvent.OnErrorDismiss) }
            }
        }

        if (mobServices.isNotEmpty()) {
            FloatingActionButton(
                onClick = onNavigateToServicios,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Recargar")
            }
        }
    }
}

@Composable
private fun LoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PrettyCard(isLoading = true) {
            Spacer(modifier = Modifier.height(48.dp).fillMaxSize())
        }
        PrettyCard(isLoading = true) {
            Spacer(modifier = Modifier.height(160.dp).fillMaxSize())
        }
        PrettyCard(isLoading = true) {
            Spacer(modifier = Modifier.height(140.dp).fillMaxSize())
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

    LazyColumn(
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
    ) {
        item {
            MobileServiceSelector(
                services = services,
                serviceSelected = service,
                onServiceSelected = { onEvent(MobileServicesEvent.OnChangeCurrentMobileService(it.id)) },
                simCards = simCards,
                onShowServiceSettings = { onEvent(OnShowServiceSettings) },
            )
        }
        item {
            Spacer(modifier = Modifier.height(4.dp))
            BalanceSection(
                modifier = Modifier.padding(horizontal = 16.dp),
                balanceCredit = "${service.mainBalance} ${service.currency}",
                lockDate = service.lockDate,
                deletionDate = service.deletionDate,
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            PlansSection(plans = service.plans)
        }
        if (service.bonuses.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                BonusSection(bonuses = service.bonuses)
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (state.isServiceSettingsVisible) {
        ServiceSettingsBottomSheet(
            mobService = service,
            onDismiss = { onEvent(OnHideServiceSettings) },
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MobileServicesScreenPreview() {
    PortalUsuarioTheme {
        MobileServicesScreen(
            mobServices =
                listOf(
                    MobileService(
                        id = "service1",
                        lte = false,
                        advanceBalance = "0.00",
                        status = "Active",
                        lockDate = "2023-12-31",
                        deletionDate = "2024-01-30",
                        saleDate = "",
                        internet = true,
                        plans = emptyList(),
                        bonuses = emptyList(),
                        currency = "CUP",
                        phoneNumber = "52345678",
                        mainBalance = "100.00",
                        consumptionRate = false,
                        slotIndex = 0,
                        type = ServiceType.Remote,
                        lastUpdated = System.currentTimeMillis(),
                    ),
                ),
            preferences =
                MobServPreferences(
                    slotIndexInfoList = emptyList(),
                    mssId = "service1",
                ),
            state = MobileServicesState(),
            simCards = emptyList(),
            onEvent = {},
        )
    }
}
