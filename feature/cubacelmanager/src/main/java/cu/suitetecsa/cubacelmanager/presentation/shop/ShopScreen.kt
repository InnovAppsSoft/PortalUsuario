package cu.suitetecsa.cubacelmanager.presentation.shop

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SimCardAlert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cu.suitetecsa.core.ui.components.ResultDialog
import cu.suitetecsa.core.ui.theme.DeepLavender
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.core.ui.theme.VibrantGreen
import cu.suitetecsa.core.ui.theme.VibrantPink
import cu.suitetecsa.core.ui.theme.VibrantTangerineOrange
import cu.suitetecsa.core.ui.theme.VividRed
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.presentation.shop.components.DataPlan
import cu.suitetecsa.cubacelmanager.ui.components.SimCardsSpinner
import cu.suitetecsa.sdk.android.model.SimCard

@Composable
internal fun ShopRoute(
    viewModel: ShopViewModel = hiltViewModel(),
    setTitle: (String) -> Unit,
    setActions: (@Composable (RowScope.() -> Unit)) -> Unit,
    bottomPadding: PaddingValues,
    topPadding: PaddingValues
) {
    setTitle(stringResource(R.string.to_buy))
    setActions {
        ShopActions(
            simCards = viewModel.state.value.simCards,
            currentSimCard = viewModel.state.value.currentSimCard,
            onSimCardSelect = { sim ->
                sim?.let { viewModel.onEvent(ShopEvent.ChangeSimCard(it)) }
            }
        )
    }

    viewModel.state.value.resultMessage?.let {
        ResultDialog(message = it, onDismiss = { viewModel.onEvent(ShopEvent.DismissDialog) })
    }

    ShopScreen(
        onEvent = viewModel::onEvent,
        bottomPadding = bottomPadding,
        topPadding = topPadding,
        canRun = viewModel.state.value.canRun
    )
}

@Composable
fun ShopScreen(
    onEvent: (ShopEvent) -> Unit = { },
    bottomPadding: PaddingValues = PaddingValues(),
    topPadding: PaddingValues = PaddingValues(),
    canRun: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottomPadding)
    ) {
        Box(modifier = Modifier.height(topPadding.calculateTopPadding()))
        SectionDataPlans(VibrantTangerineOrange, VibrantPink, canRun, onEvent)
        SectionPlans(VibrantGreen, VividRed, canRun, onEvent)
    }
}

@Composable
private fun SectionDataPlans(
    allNetworksColor: Color,
    onlyLteColor: Color,
    canRun: Boolean = false,
    onEvent: (ShopEvent) -> Unit,
) {
    Text(
        text = stringResource(R.string.data_plans),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentWidth(),
        style = MaterialTheme.typography.titleLarge
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color = allNetworksColor)
            ) {}
            Text(text = stringResource(R.string.all_networks), modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color = onlyLteColor)
            ) {}
            Text(text = "LTE", modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color = DeepLavender)
            ) {}
            Text(text = "Extra", modifier = Modifier.padding(start = 8.dp))
        }
    }
    SectionAllNetwork(allNetworksColor, canRun, onEvent)
    SectionOnlyLte(onlyLteColor, canRun, onEvent)
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        allNetworksData = "4.00 GB",
        lteNetworkData = "12.00 GB",
        nationalVoucherData = "300 MB",
        minutesCount = "0 MIN",
        smsCount = "0 SMS",
        planPrice = "$950",
        color = DeepLavender,
        ussdCode = "*133*1*4*3*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
}

@Composable
private fun SectionOnlyLte(
    onlyLteColor: Color,
    canRun: Boolean = false,
    onEvent: (ShopEvent) -> Unit
) {
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        allNetworksData = "0 B",
        lteNetworkData = "1.00 GB",
        nationalVoucherData = "300 MB",
        minutesCount = "0 MIN",
        smsCount = "0 SMS",
        planPrice = "$100",
        color = onlyLteColor,
        ussdCode = "*133*1*4*1*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        allNetworksData = "0 B",
        lteNetworkData = "2.50 GB",
        nationalVoucherData = "300 MB",
        minutesCount = "0 MIN",
        smsCount = "0 SMS",
        planPrice = "$200",
        color = onlyLteColor,
        ussdCode = "*133*1*4*2*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
}

@Composable
private fun SectionAllNetwork(
    allNetworksColor: Color,
    canRun: Boolean = false,
    onEvent: (ShopEvent) -> Unit
) {
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        allNetworksData = "600 MB",
        lteNetworkData = "800 MB",
        nationalVoucherData = "300 MB",
        minutesCount = "16 MIN",
        smsCount = "20 SMS",
        planPrice = "$110",
        color = allNetworksColor,
        ussdCode = "*133*5*1*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        allNetworksData = "1.50 GB",
        lteNetworkData = "2.00 GB",
        nationalVoucherData = "300 MB",
        minutesCount = "35 MIN",
        smsCount = "40 SMS",
        planPrice = "$250",
        color = allNetworksColor,
        ussdCode = "*133*5*2*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        allNetworksData = "3.50 GB",
        lteNetworkData = "4.50 GB",
        nationalVoucherData = "300 MB",
        minutesCount = "75 MIN",
        smsCount = "80 SMS",
        planPrice = "$500",
        color = allNetworksColor,
        ussdCode = "*133*5*3*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
}

@Composable
private fun SectionPlans(
    smsColor: Color,
    minutesColor: Color,
    canRun: Boolean = false,
    onEvent: (ShopEvent) -> Unit
) {
    Text(
        text = "Planes",
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentWidth(),
        style = MaterialTheme.typography.titleLarge
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color = smsColor)
            ) {}
            Text(text = "SMS", modifier = Modifier.padding(start = 8.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(color = minutesColor)
            ) {}
            Text(text = "MIN", modifier = Modifier.padding(start = 8.dp))
        }
    }
    SectionSms(smsColor, canRun, onEvent)
    SectionMinutes(minutesColor, canRun, onEvent)
}

@Composable
private fun SectionMinutes(
    minutesColor: Color,
    canRun: Boolean = false,
    onEvent: (ShopEvent) -> Unit
) {
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        minutesCount = "5 MIN",
        planPrice = "$37.50",
        color = minutesColor,
        ussdCode = "*133*3*1*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        minutesCount = "10 MIN",
        planPrice = "$72.50",
        color = minutesColor,
        ussdCode = "*133*3*2*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        minutesCount = "15 MIN",
        planPrice = "$105.50",
        color = minutesColor,
        ussdCode = "*133*3*3*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        minutesCount = "25 MIN",
        planPrice = "$162.50",
        color = minutesColor,
        ussdCode = "*133*3*4*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        minutesCount = "40 MIN",
        planPrice = "$250",
        color = minutesColor,
        ussdCode = "*133*3*5*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
}

@Composable
private fun SectionSms(
    smsColor: Color,
    canRun: Boolean = false,
    onEvent: (ShopEvent) -> Unit
) {
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        smsCount = "20 SMS",
        planPrice = "$15",
        color = smsColor,
        ussdCode = "*133*2*1*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        smsCount = "50 SMS",
        planPrice = "$30",
        color = smsColor,
        ussdCode = "*133*2*2*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        smsCount = "90 SMS",
        planPrice = "$50",
        color = smsColor,
        ussdCode = "*133*2*3*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
    DataPlan(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        smsCount = "120 SMS",
        planPrice = "$60",
        color = smsColor,
        ussdCode = "*133*2*4*1${Uri.parse("#")}",
        canRun = canRun,
        onEvent = onEvent
    )
}

@Composable
fun ShopActions(
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

@Preview
@Composable
private fun ShopScreenPreview() {
    SuitEtecsaTheme {
        Surface {
            ShopScreen()
        }
    }
}
