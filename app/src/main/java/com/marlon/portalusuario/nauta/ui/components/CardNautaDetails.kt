package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme
import com.marlon.portalusuario.nauta.core.toPriceString
import com.marlon.portalusuario.nauta.domain.model.UserModel

@Composable
fun CardNautaDetails(
    modifier: Modifier = Modifier,
    user: UserModel,
    isLoading: Boolean,
    loginStatus: Pair<Boolean, String?>
) {
    val (isOk, _) = loginStatus
    Column(modifier = modifier) {
        Text(
            text = "Nauta",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .padding(4.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.ic_baseline_attach_money_24),
                detailName = stringResource(id = R.string.new_account_credit),
                detailValue = "${user.credit.toPriceString()} CUP",
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFFF4747),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_lock_clock_24),
                detailName = stringResource(id = R.string.new_blocking_date),
                detailValue = user.blockingDate,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF00BCD4),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.outline_auto_delete_24),
                detailName = stringResource(id = R.string.new_date_of_elimination),
                detailValue = user.dateOfElimination,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF553096),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.outline_wifi_24),
                detailName = stringResource(id = R.string.new_account_type),
                detailValue = user.accountType,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFE28906),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_payment_24),
                detailName = stringResource(id = R.string.new_service_type),
                detailValue = if (user.serviceType == NavigationType.INTERNATIONAL) "Internacional" else "Nacional",
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF009688),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_alternate_email_24),
                detailName = stringResource(id = R.string.new_mail_account),
                detailValue = user.email,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF3F51B5),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
    }
}

@Composable
fun NautaDetail(
    modifier: Modifier = Modifier,
    detailIcon: ImageVector,
    detailName: String,
    detailValue: String,
    backgroundColor: Color,
    isLoading: Boolean,
    isFoundErrors: Boolean
) {
    PrettyCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        isLoading = isLoading,
        isFoundErrors = isFoundErrors
    ) {
        Column(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = detailIcon,
                contentDescription = detailName,
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.TopEnd),
                tint = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = detailValue,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.BottomStart),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

val user = UserModel(
    id = 5,
    username = "user.name",
    password = "somePassword",
    blockingDate = "25/12/2025",
    dateOfElimination = "26/1/2026",
    accountType = "Prepago",
    serviceType = NavigationType.INTERNATIONAL,
    credit = 59.64f,
    remainingTime = 5234,
    email = "user.name@nauta.cu"
)

@Preview(showBackground = true)
@Composable
fun CardNautaDetailsPreview() {
    SuitEtecsaTheme {
        CardNautaDetails(
            user = user,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            loginStatus = Pair(true, null),
            isLoading = false
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardNautaDetailsPreviewDark() {
    SuitEtecsaTheme {
        CardNautaDetails(
            user = user,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            loginStatus = Pair(true, null),
            isLoading = false
        )
    }
}