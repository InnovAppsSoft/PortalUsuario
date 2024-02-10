package cu.suitetecsa.cubacelmanager.presentation.servicecall

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContactSupport
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocalPolice
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PhoneForwarded
import androidx.compose.material.icons.outlined.PhoneLocked
import androidx.compose.material.icons.outlined.SimCardAlert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cu.suitetecsa.core.ui.components.prettycard.ColorCard
import cu.suitetecsa.core.ui.components.prettycard.PrettyCard
import cu.suitetecsa.core.ui.theme.BrightOrange
import cu.suitetecsa.core.ui.theme.BrightSkyBlue
import cu.suitetecsa.core.ui.theme.DeepLavender
import cu.suitetecsa.core.ui.theme.DeepPurple
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.core.ui.theme.TealBlue
import cu.suitetecsa.core.ui.theme.VibrantGreen
import cu.suitetecsa.core.ui.theme.VibrantTangerineOrange
import cu.suitetecsa.core.ui.theme.VividRed
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.ui.components.SimCardsSpinner
import cu.suitetecsa.sdk.android.model.SimCard

private const val FiftyWeight = .5f
private const val SixtyWeight = .6f
private const val FortyWeight = .4f

@Composable
internal fun ServicesCallRoute(
    viewModel: ServicesCallViewModel = hiltViewModel(),
    setTitle: (String) -> Unit,
    setActions: (@Composable (RowScope.() -> Unit)) -> Unit,
    bottomPadding: PaddingValues,
    topPadding: PaddingValues
) {
    setTitle(stringResource(R.string.services))
    setActions {
        ServicesCallActions(
            simCards = viewModel.state.value.simCards,
            currentSimCard = viewModel.state.value.currentSimCard,
            onSimCardSelect = { sim ->
                sim?.let { viewModel.onEvent(ServiceCallEvent.ChangeSimCard(it)) }
            }
        )
    }

    ServicesCallScreen(
        bottomPadding = bottomPadding,
        topPadding = topPadding,
        onCall = { viewModel.onEvent(ServiceCallEvent.Call(it)) }
    )
}

@Composable
fun ServicesCallScreen(
    onCall: (String) -> Unit = {},
    bottomPadding: PaddingValues,
    topPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottomPadding)
    ) {
        Box(modifier = Modifier.height(topPadding.calculateTopPadding()))
        CallContactsCard(onCall)
        EmergencyCard(onCall)
        FriendPlanCard(onCall)
    }
}

@Composable
private fun FriendPlanCard(onCall: (String) -> Unit) {
    PrettyCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column {
            Row {
                ColorCard(
                    modifier = Modifier
                        .weight(FiftyWeight)
                        .clickable { onCall("*133*4*1%23") },
                    detailIcon = Icons.Outlined.People,
                    detailName = stringResource(R.string.active_plan),
                    backgroundColor = BrightSkyBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                ColorCard(
                    modifier = Modifier
                        .weight(FiftyWeight)
                        .clickable { onCall("*133*4*3%23") },
                    detailIcon = Icons.Outlined.People,
                    detailName = stringResource(R.string.consult_plan),
                    backgroundColor = TealBlue
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                ColorCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onCall("*133*4*2%23") },
                    detailIcon = Icons.Outlined.PersonAdd,
                    detailName = stringResource(R.string.manager_friend),
                    backgroundColor = VividRed
                )
            }
        }
    }
}

@Composable
private fun EmergencyCard(onCall: (String) -> Unit) {
    PrettyCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column {
            Row {
                ColorCard(
                    modifier = Modifier.weight(FiftyWeight).clickable { onCall("104") },
                    detailIcon = Icons.Outlined.LocalHospital,
                    detailName = stringResource(R.string.ambulance),
                    backgroundColor = TealBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                ColorCard(
                    modifier = Modifier.weight(FiftyWeight).clickable { onCall("105") },
                    detailIcon = Icons.Outlined.LocalFireDepartment,
                    detailName = stringResource(R.string.firefighters),
                    backgroundColor = BrightOrange
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                ColorCard(
                    modifier = Modifier.weight(SixtyWeight).clickable { onCall("107") },
                    detailIcon = Icons.Outlined.DirectionsBoat,
                    detailName = stringResource(R.string.maritime_rescue),
                    backgroundColor = VibrantGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                ColorCard(
                    modifier = Modifier.weight(FortyWeight).clickable { onCall("106") },
                    detailIcon = Icons.Outlined.LocalPolice,
                    detailName = stringResource(R.string.police),
                    backgroundColor = VibrantTangerineOrange
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                ColorCard(
                    modifier = Modifier.weight(FortyWeight).clickable { onCall("103") },
                    detailIcon = Icons.Outlined.Medication,
                    detailName = stringResource(R.string.anti_drug),
                    backgroundColor = DeepLavender
                )
                Spacer(modifier = Modifier.width(8.dp))
                ColorCard(
                    modifier = Modifier.weight(SixtyWeight).clickable { onCall("52642266") },
                    detailIcon = Icons.Outlined.ContactSupport,
                    detailName = stringResource(R.string.customer_service),
                    backgroundColor = DeepPurple
                )
            }
        }
    }
}

@Composable
private fun CallContactsCard(onCall: (String) -> Unit) {
    PrettyCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row {
            ColorCard(
                modifier = Modifier.weight(FiftyWeight).clickable { onCall("") },
                detailIcon = Icons.Outlined.PhoneForwarded,
                detailName = "*99",
                backgroundColor = DeepLavender
            )
            Spacer(modifier = Modifier.width(8.dp))
            ColorCard(
                modifier = Modifier.weight(FiftyWeight).clickable { onCall("") },
                detailIcon = Icons.Outlined.PhoneLocked,
                detailName = stringResource(R.string.private_call),
                backgroundColor = VibrantGreen
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesCallPreview() {
    SuitEtecsaTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ServicesCallScreen(bottomPadding = PaddingValues(), topPadding = PaddingValues())
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ServicesCallPreviewDark() {
    SuitEtecsaTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ServicesCallScreen(bottomPadding = PaddingValues(), topPadding = PaddingValues())
        }
    }
}

@Composable
fun ServicesCallActions(
    simCards: List<SimCard?>,
    currentSimCard: SimCard?,
    onSimCardSelect: (SimCard?) -> Unit,
    isSomeTaskRunning: Boolean = false
) {
    if (simCards.isNotEmpty()) {
        val simCardIcons = listOf(
            ImageVector.vectorResource(id = R.drawable.sim_one),
            ImageVector.vectorResource(id = R.drawable.sim_two),
            ImageVector.vectorResource(id = R.drawable.sim_three)
        )
        if (simCards.size > 1) {
            SimCardsSpinner(simCards, currentSimCard, onSimCardSelect, isSomeTaskRunning, simCardIcons)
        }
    } else {
        Icon(imageVector = Icons.Outlined.SimCardAlert, contentDescription = null)
    }
}
