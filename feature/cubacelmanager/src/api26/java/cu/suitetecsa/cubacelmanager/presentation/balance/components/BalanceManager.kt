package cu.suitetecsa.cubacelmanager.presentation.balance.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.core.ui.theme.VibrantGreen
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.domain.model.Balance
import cu.suitetecsa.cubacelmanager.presentation.balance.BalanceEvent

@Composable
fun BalanceManager(
    modifier: Modifier = Modifier,
    onEvent: (BalanceEvent) -> Unit = {},
    balance: Balance? = null,
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
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = balance?.let { "$%.2f CUP".format(it.credit) } ?: "???",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp)
            )
            IconButton(
                onClick = { onEvent(BalanceEvent.OnChangeTopUpSheetVisibility(true)) }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                    Divider(thickness = 5.dp, color = VibrantGreen)
                }
            }
            IconButton(
                onClick = { onEvent(BalanceEvent.OnChangeTransferSheetVisibility(true)) }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = null)
                    Divider(thickness = 5.dp, color = VibrantGreen)
                }
            }
        }
        balance?.let {
            ExpirationSection(it)
        }

        balance?.let {
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
                    checked = it.usageBasedPricing,
                    onCheckedChange = { active -> onEvent(BalanceEvent.TurnUsageBasedPricing(active)) },
                    enabled = canRun
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            PlansSection(balance = it)
            BonusSection(it)
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    device = "spec:width=411dp,height=891dp",
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
                    bonusSms = 846,
                    bonusSmsRemainingDays = 25,
                    bonusVoice = 92,
                    bonusVoiceRemainingDays = 25,
                )
            )
        }
    }
}
