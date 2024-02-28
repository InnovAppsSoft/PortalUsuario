package cu.suitetecsa.cubacelmanager.presentation.balance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.suitetecsa.core.ui.components.ArcProgressbar
import cu.suitetecsa.core.ui.theme.BrightCoralRed
import cu.suitetecsa.core.ui.theme.BrightOrange
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.domain.model.Balance
import cu.suitetecsa.sdk.android.utils.LongUtils.asDateString
import cu.suitetecsa.sdk.android.utils.LongUtils.asRemainingDays

private const val ElevenMonths = 330
private const val TwelveMonths = 360

@Composable
internal fun ExpirationSection(it: Balance) {
    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ExpirationView(
            stringResource(id = R.string.sim_active_until),
            it.activeUntil.asDateString,
            it.activeUntil.asRemainingDays,
            ElevenMonths,
            BrightOrange
        )
        ExpirationView(
            stringResource(id = R.string.expires),
            it.dueDate.asDateString,
            it.dueDate.asRemainingDays,
            TwelveMonths,
            BrightCoralRed
        )
    }
}

@Composable
private fun ExpirationView(
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
            backgroundIndicatorStrokeWidth = 15f,
            foregroundIndicatorStrokeWidth = 15f,
            foregroundIndicatorColor = color
        )
    }
}

