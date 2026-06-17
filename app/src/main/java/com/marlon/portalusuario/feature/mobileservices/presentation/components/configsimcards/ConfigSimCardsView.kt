package com.marlon.portalusuario.feature.mobileservices.presentation.components.configsimcards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
    onSetCanGoNext: (Boolean) -> Unit,
) {
    val viewState = state.viewModel.state
    val currentSimCard = viewState.currentSimCard

    if (currentSimCard == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { viewState.simCards.size }

    LaunchedEffect(viewState.isLoading) {
        onSetIsLoading(viewState.isLoading)
    }

    LaunchedEffect(state.viewModel.isPhoneNumberValid) {
        onSetCanGoNext(state.viewModel.isPhoneNumberValid)
    }

    HorizontalPager(state = pagerState, userScrollEnabled = false) {
        ServiceSettingsView(
            simCard = currentSimCard,
            phoneNumber = viewState.phoneNumber,
            isLoading = viewState.isLoading,
            onChangePhoneNumber = { state.viewModel.onEvent(ConfigSimCardsEvent.OnChangedPhoneNumber(it)) },
        )
    }

    LaunchedEffect(key1 = currentSimCard) {
        onSetTitle("Configurar SIM${currentSimCard.slotIndex + 1}")
        if (viewState.simCards.indexOf(currentSimCard) != pagerState.currentPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(viewState.simCards.indexOf(currentSimCard))
            }
        }
    }
}

@Composable
fun ServiceSettingsView(
    simCard: SimCard,
    phoneNumber: String,
    isLoading: Boolean = false,
    onChangePhoneNumber: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier =
                Modifier
                    .background(
                        MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small,
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
