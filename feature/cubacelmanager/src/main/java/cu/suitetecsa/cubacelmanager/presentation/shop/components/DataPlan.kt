package cu.suitetecsa.cubacelmanager.presentation.shop.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cu.suitetecsa.core.ui.components.prettycard.PrettyCard
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.cubacelmanager.R
import cu.suitetecsa.cubacelmanager.presentation.shop.ShopEvent

private const val ColumnWeight = .3f

@Suppress("LongMethod")
@Composable
fun DataPlan(
    modifier: Modifier = Modifier,
    allNetworksData: String = "0 B",
    lteNetworkData: String = "0 B",
    nationalVoucherData: String = "0 B",
    minutesCount: String = "0 MIN",
    smsCount: String = "0 SMS",
    planPrice: String = "",
    color: Color,
    ussdCode: String,
    canRun: Boolean = false,
    onEvent: (ShopEvent) -> Unit,
) {
    val isLoading = remember { mutableStateOf(false) }
    PrettyCard(modifier = modifier, isLoading = isLoading.value) {
        ConstraintLayout {
            val (colorIndicator, information) = createRefs()

            Box(
                modifier = Modifier
                    .width(20.dp)
                    .background(color)
                    .constrainAs(colorIndicator) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(information.start)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    }
            ) {}

            Column(
                Modifier
                    .padding(8.dp)
                    .constrainAs(information) {
                        top.linkTo(parent.top)
                        start.linkTo(colorIndicator.end)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(ColumnWeight)) {
                        Text(
                            text = stringResource(R.string.all_network),
                            style = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                        Text(
                            text = allNetworksData,
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(ColumnWeight)) {
                        Text(
                            text = stringResource(R.string.lte_4g),
                            style = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                        Text(
                            text = lteNetworkData,
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(ColumnWeight)) {
                        Text(
                            text = stringResource(R.string.national),
                            style = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                        Text(
                            text = nationalVoucherData,
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(ColumnWeight)) {
                        Text(
                            text = stringResource(R.string.minutes),
                            style = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                        Text(
                            text = minutesCount,
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(ColumnWeight)) {
                        Text(
                            text = stringResource(id = R.string.sms),
                            style = TextStyle(fontSize = 10.sp),
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                        Text(
                            text = smsCount,
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(ColumnWeight)) {
                        Button(
                            onClick = { onEvent(ShopEvent.Buy(ussdCode) { isLoading.value = it }) },
                            shape = MaterialTheme.shapes.small,
                            contentPadding = PaddingValues(8.dp),
                            enabled = canRun,
                            modifier = Modifier.fillMaxWidth().wrapContentWidth()
                        ) {
                            Text(text = planPrice, style = TextStyle(fontSize = 12.sp))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DataPlanPreview() {
    SuitEtecsaTheme {
        Surface {
            DataPlan(color = MaterialTheme.colorScheme.error, ussdCode = "") {}
        }
    }
}
