package cu.suitetecsa.cubacelmanager.presentation.balance.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cu.suitetecsa.core.ui.theme.SoftRosePink
import cu.suitetecsa.core.ui.theme.VibrantGreen
import cu.suitetecsa.core.ui.theme.VibrantTangerineOrange
import cu.suitetecsa.core.ui.theme.VividLavenderPurple
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.domain.model.Balance
import cu.suitetecsa.sdk.android.utils.LongUtils.asSizeString
import cu.suitetecsa.sdk.android.utils.LongUtils.asTimeString

@Composable
internal fun PlansSection(balance: Balance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DataBalance(
            bonusData = balance.data,
            bonusDataLte = balance.dataLte,
            remainingDays = balance.dataRemainingDays
        )
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
private fun isFoundPlans(balance: Balance) =
    (
        balance.data != null || balance.dataLte != null ||
            balance.sms != null || balance.voice != null || balance.dailyData != null || balance.mailData != null
        )
