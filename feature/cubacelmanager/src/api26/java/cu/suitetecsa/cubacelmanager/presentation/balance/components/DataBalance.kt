package cu.suitetecsa.cubacelmanager.presentation.balance.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cu.suitetecsa.core.ui.theme.VibrantGreen
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.sdk.android.utils.LongUtils.asSizeString

@Composable
internal fun DataBalance(
    bonusData: Long?,
    bonusDataLte: Long?,
    remainingDays: Int?,
) {
    if (bonusData != null || bonusDataLte != null) {
        val dataCount = if (bonusData != null && bonusDataLte != null) {
            "${bonusData.asSizeString} + ${bonusDataLte.asSizeString}"
        } else if (bonusDataLte != null) {
            "${bonusDataLte.asSizeString} LTE"
        } else {
            bonusData!!.asSizeString
        }
        PlanCard(
            modifier = Modifier.padding(horizontal = 4.dp),
            planTitle = stringResource(id = R.string.data),
            dataCount = dataCount,
            remainingDays = remainingDays,
            color = VibrantGreen
        )
    }
}
