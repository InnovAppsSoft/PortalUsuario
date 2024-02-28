package cu.suitetecsa.cubacelmanager.presentation.balance.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cu.suitetecsa.core.ui.theme.BrightCoralRed
import cu.suitetecsa.core.ui.theme.TropicalAquamarineGreen
import cu.suitetecsa.core.ui.theme.VibrantGreen
import cu.suitetecsa.core.ui.theme.VibrantTangerineOrange
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.domain.model.Balance
import cu.suitetecsa.sdk.android.utils.LongUtils.asSizeString
import cu.suitetecsa.sdk.android.utils.LongUtils.asTimeString

@Composable
internal fun BonusSection(
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
        DataBalance(balance.bonusData, balance.bonusDataLte, balance.bonusDataRemainingDays)
        balance.bonusVoice?.let { voice ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.voice),
                dataCount = voice.asTimeString,
                remainingDays = balance.voiceRemainingDays,
                color = VibrantTangerineOrange
            )
        }
        balance.bonusSms?.let { sms ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.sms),
                dataCount = "$sms SMS",
                remainingDays = balance.smsRemainingDays,
                color = VibrantGreen
            )
        }
        balance.bonusDataCu?.let { bonusDataCu ->
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = stringResource(id = R.string.data_cu),
                dataCount = bonusDataCu.asSizeString,
                remainingDays = balance.bonusDataCuRemainingDays!!,
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
        balance.bonusDataLte != null || balance.bonusDataCu != null ||
        balance.bonusVoice != null || balance.bonusSms != null
