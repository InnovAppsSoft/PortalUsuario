package cu.suitetecsa.cubacelmanager.presentation.balance.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.DataSaverOff
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.core.ui.components.prettycard.ColorCard
import cu.suitetecsa.core.ui.theme.BrightCoralRed
import cu.suitetecsa.core.ui.theme.SoftRosePink
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.core.ui.theme.TropicalAquamarineGreen
import cu.suitetecsa.core.ui.theme.VibrantTangerineOrange
import cu.suitetecsa.core.ui.theme.VividLavenderPurple
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.presentation.balance.BalanceEvent
import cu.suitetecsa.cubacelmanager.presentation.balance.BalanceEvent.MakeCall

private const val CardWeight = .3f

@Composable
fun BalanceManager(
    modifier: Modifier = Modifier,
    onUssdCall: (BalanceEvent) -> Unit = {},
) {
    Column(modifier = modifier) {
        Row {
            ColorCard(
                detailIcon = Icons.Outlined.AttachMoney,
                detailName = stringResource(R.string.balance_credit),
                backgroundColor = BrightCoralRed,
                modifier = Modifier
                    .weight(CardWeight)
                    .clickable { onUssdCall(MakeCall("*222${Uri.parse("#")}")) }
            )
            Spacer(modifier = Modifier.padding(3.dp))
            ColorCard(
                detailIcon = Icons.Outlined.DataSaverOff,
                detailName = stringResource(R.string.data),
                backgroundColor = TropicalAquamarineGreen,
                modifier = Modifier
                    .weight(CardWeight)
                    .clickable { onUssdCall(MakeCall("*222*328%23")) }
            )
            Spacer(modifier = Modifier.padding(3.dp))
            ColorCard(
                detailIcon = Icons.Outlined.Call,
                detailName = stringResource(R.string.voice),
                backgroundColor = SoftRosePink,
                modifier = Modifier
                    .weight(CardWeight)
                    .clickable { onUssdCall(MakeCall("*222*869%23")) }
            )
        }
        Spacer(modifier = Modifier.padding(3.dp))
        Row {
            ColorCard(
                detailIcon = Icons.Outlined.Sms,
                detailName = stringResource(R.string.sms),
                backgroundColor = VividLavenderPurple,
                modifier = Modifier
                    .weight(CardWeight)
                    .clickable { onUssdCall(MakeCall("*222*767%23")) }
            )
            Spacer(modifier = Modifier.padding(3.dp))
            ColorCard(
                detailIcon = Icons.Outlined.CardGiftcard,
                detailName = stringResource(R.string.bonus),
                backgroundColor = VibrantTangerineOrange,
                modifier = Modifier
                    .weight(CardWeight)
                    .clickable { onUssdCall(MakeCall("*222*266%23")) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun ManualBalanceActionsPreview() {
    SuitEtecsaTheme { BalanceManager(modifier = Modifier.padding(16.dp)) }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES, locale = "es")
@Composable
internal fun ManualBalanceActionsPreviewDark() {
    SuitEtecsaTheme {
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) { BalanceManager(modifier = Modifier.padding(16.dp)) }
    }
}
