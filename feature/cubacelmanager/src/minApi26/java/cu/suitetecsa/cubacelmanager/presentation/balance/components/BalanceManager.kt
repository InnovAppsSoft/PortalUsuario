package cu.suitetecsa.cubacelmanager.presentation.balance.components

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.suitetecsa.core.ui.components.ArcProgressbar
import cu.suitetecsa.core.ui.theme.BrightCoralRed
import cu.suitetecsa.core.ui.theme.BrightOrange
import cu.suitetecsa.core.ui.theme.DeepPurple
import cu.suitetecsa.core.ui.theme.SoftRosePink
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.core.ui.theme.TropicalAquamarineGreen
import cu.suitetecsa.core.ui.theme.VibrantGreen
import cu.suitetecsa.core.ui.theme.VibrantTangerineOrange
import cu.suitetecsa.core.ui.theme.VividLavenderPurple
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.domain.model.Balance
import cu.suitetecsa.cubacelmanager.presentation.balance.BalanceEvent
import cu.suitetecsa.sdk.android.kotlin.asDateString
import cu.suitetecsa.sdk.android.kotlin.asRemainingDays
import cu.suitetecsa.sdk.android.kotlin.asSizeString
import cu.suitetecsa.sdk.android.kotlin.asTimeString

@Composable
fun BalanceManager(
    modifier: Modifier = Modifier,
    onEvent: (BalanceEvent) -> Unit = {},
    balance: Balance = Balance(0),
    canRun: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = stringResource(id = R.string.balance_credit),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "$%.2f CUP".format(balance.credit),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp)
        )
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ExpireSection(
                stringResource(id = R.string.sim_active_until),
                balance.activeUntil.asDateString,
                balance.activeUntil.asRemainingDays,
                360,
                BrightOrange
            )
            ExpireSection(
                stringResource(id = R.string.expires),
                balance.dueDate.asDateString,
                balance.dueDate.asRemainingDays,
                330,
                BrightCoralRed
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.usage_based_pricing))
            Switch(
                checked = balance.usageBasedPricing,
                onCheckedChange = { onEvent(BalanceEvent.TurnUsageBasedPricing(it)) },
                enabled = canRun
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        PlansSection(balance = balance)
        BonusSection(balance)
    }
}

@Composable
private fun ExpireSection(
    title: String,
    date: String,
    remainingDays: Int,
    maxDays: Int,
    color: Color
) {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title)
            Text(text = date)
        }
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        ArcProgressbar(
            canvasSize = 50.dp,
            indicatorValue = remainingDays,
            maxIndicatorValue = maxDays,
            smallTextFontSize = 6.sp,
            bigTextFontSize = 8.sp,
            bigTextSuffix = "D",
            backgroundIndicatorStrokeWidth = 20f,
            foregroundIndicatorStrokeWidth = 20f,
            foregroundIndicatorColor = color
        )
    }
}

@Composable
private fun BonusSection(
    balance: Balance,
) {
    if (isFoundBonus(balance)) {
        Text(
            text = "Bonus",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.padding(4.dp))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        balance.bonusCredit?.let { bonusCredit ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.balance_credit),
                dataCount = "$%.2f CUP".format(bonusCredit),
                remainingDays = 0,
                color = TropicalAquamarineGreen
            )
        }
        if (balance.bonusData != null || balance.bonusDataLte != null) {
            val dataCount = if (balance.bonusData != null && balance.bonusDataLte != null) {
                "${balance.bonusData.asSizeString} + ${balance.bonusDataLte.asSizeString}"
            } else if (balance.bonusDataLte != null) {
                "${balance.bonusDataLte.asSizeString} LTE"
            } else {
                balance.bonusData!!.asSizeString
            }
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.data),
                dataCount = dataCount,
                remainingDays = 0,
                color = VibrantGreen
            )
        }
        balance.bonusDataCu?.let { bonusDataCu ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.data_cu),
                dataCount = bonusDataCu.asSizeString,
                remainingDays = balance.bonusDataCuDueDate!!.asRemainingDays,
                color = BrightCoralRed
            )
        }
    }
    if (isFoundBonus(balance)) {
        Spacer(modifier = Modifier.padding(4.dp))
    }
}

@Composable
private fun isFoundBonus(balance: Balance) =
    balance.bonusCredit != null || balance.bonusData != null ||
        balance.bonusDataLte != null || balance.bonusDataCu != null

@Composable
fun PlansSection(balance: Balance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (balance.data != null || balance.dataLte != null) {
            val dataCount = getDataCount(balance)
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.data),
                dataCount = dataCount,
                remainingDays = balance.dataRemainingDays,
                color = DeepPurple
            )
        }
        balance.voice?.let { voice ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.voice),
                dataCount = voice.asTimeString,
                remainingDays = balance.voiceRemainingDays,
                color = VibrantTangerineOrange
            )
        }
        balance.sms?.let { sms ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.sms),
                dataCount = "$sms SMS",
                remainingDays = balance.smsRemainingDays,
                color = VibrantGreen
            )
        }
        balance.dailyData?.let { dailyData ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(R.string.daily_data),
                dataCount = dailyData.asSizeString,
                remainingDays = balance.dailyDataRemainingHours,
                isDailyData = true,
                color = VividLavenderPurple
            )
        }
        balance.mailData?.let { mailData ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(R.string.mail_data),
                dataCount = mailData.asSizeString,
                remainingDays = balance.mailDataRemainingDays,
                color = SoftRosePink
            )
        }
    }
    if (isFoundPlans(balance)) Spacer(modifier = Modifier.padding(4.dp))
}

@Composable
private fun getDataCount(
    balance: Balance,
) = if (balance.data != null && balance.dataLte != null) {
    "${balance.data.asSizeString} + ${balance.dataLte.asSizeString}"
} else if (balance.dataLte != null) {
    "${balance.dataLte.asSizeString} LTE"
} else {
    balance.data!!.asSizeString
}

@Composable
private fun isFoundPlans(balance: Balance) =
    (
        balance.data != null || balance.dataLte != null ||
            balance.sms != null || balance.voice != null || balance.dailyData != null || balance.mailData != null
        )

@Preview(
    showBackground = true,
    showSystemUi = false,
    device = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420",
    locale = "es",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun BalanceManagerPreview() {
    SuitEtecsaTheme {
        Surface {
            BalanceManager(
                balance = Balance(
                    0,
                    credit = 1569.76,
                    usageBasedPricing = true,
                    data = 5684432547,
                    dataLte = 5663892547,
                    dataRemainingDays = 25,
                    dailyData = 166389254,
                    dailyDataRemainingHours = 12,
                    sms = 846,
                    smsRemainingDays = 25,
                    voice = 92,
                    voiceRemainingDays = 25,
                )
            )
        }
    }
}
