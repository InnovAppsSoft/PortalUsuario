package com.marlon.portalusuario.presentation.mobileservices.components.configsimcards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ui.components.NautaUserField
import io.github.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.launch

@Composable
fun rememberConfigSimCardsViewState(viewModel: ConfigSimCardsViewModel = hiltViewModel()) =
    remember { ConfigSimCardsViewState(viewModel) }

@Composable
fun ConfigSimCardsView(
    state: ConfigSimCardsViewState,
    onSetIsLoading: (Boolean) -> Unit,
    onSetTitle: (String) -> Unit,
    onSetCanGoNext: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { state.viewModel.state.simCards.size }

    LaunchedEffect(key1 = state.viewModel.state.isLoading) {
        onSetIsLoading(state.viewModel.state.isLoading)
    }

    LaunchedEffect(state.viewModel.isPhoneNumberValid) {
        onSetCanGoNext(state.viewModel.isPhoneNumberValid)
    }

    HorizontalPager(state = pagerState, userScrollEnabled = false) {
        ServiceSettingsView(
            simCard = state.viewModel.state.currentSimCard,
            phoneNumber = state.viewModel.state.phoneNumber,
            isLoading = state.viewModel.state.isLoading,
            onChangePhoneNumber = { state.viewModel.onEvent(ConfigSimCardsEvent.OnChangedPhoneNumber(it)) }
        )
    }

    LaunchedEffect(key1 = state.viewModel.state.currentSimCard) {
        onSetTitle("Configurar SIM${state.viewModel.state.currentSimCard.slotIndex + 1}")
        if (state.viewModel.state.simCards.indexOf(state.viewModel.state.currentSimCard) != pagerState.currentPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    state.viewModel.state.simCards.indexOf(state.viewModel.state.currentSimCard)
                )
            }
        }
    }
}

@Composable
fun ServiceSettingsView(
    simCard: SimCard,
    phoneNumber: String,
    isLoading: Boolean = false,
    onChangePhoneNumber: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                ),
        ) {
            Text(
                text = stringResource(R.string.config_sim_alert_message, simCard.slotIndex + 1),
                modifier = Modifier.padding(8.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        NautaUserField(phoneNumber, !isLoading, onChangePhoneNumber)
        Spacer(modifier = Modifier.height(64.dp))
    }
}
