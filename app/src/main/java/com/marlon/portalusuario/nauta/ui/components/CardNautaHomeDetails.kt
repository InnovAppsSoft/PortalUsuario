package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme
import com.marlon.portalusuario.nauta.domain.model.UserModel

@Composable
fun CardNautaHomeDetails(
    modifier: Modifier = Modifier,
    user: UserModel,
    isLoading: Boolean,
    loginStatus: Pair<Boolean, String?>
) {
    val (isOk, _) = loginStatus
    Column(modifier = modifier) {
        Text(
            text = "Enlace",
            style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onBackground)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.ic_baseline_attach_money_24),
                detailName = stringResource(id = R.string.new_monthly_fee),
                detailValue = user.monthlyFee!!.replace("$", ""),
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFFF9AA2),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_download_24),
                detailName = stringResource(id = R.string.new_download_speed),
                detailValue = user.downloadSpeeds!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFB2C1FA),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_upload_24),
                detailName = stringResource(id = R.string.new_upload_speed),
                detailValue = user.uploadSpeeds!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFEC9F6B),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_numbers_24),
                detailName = stringResource(id = R.string.new_phone),
                detailValue = user.phone!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFF44336),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_phonelink_24),
                detailName = stringResource(id = R.string.new_link_identifier),
                detailValue = user.linkIdentifiers!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF009688),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_link_24),
                detailName = stringResource(id = R.string.new_link_status),
                detailValue = user.linkStatus!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFB57DAD),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_local_offer_24),
                detailName = stringResource(id = R.string.new_offer),
                detailValue = user.offer!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF673AB7),
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_lock_clock_24),
                detailName = stringResource(id = R.string.new_blocking_date),
                detailValue = user.blockingDateHome!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF03A9F4),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_lock_clock_24),
                detailName = stringResource(id = R.string.new_blocking_date),
                detailValue = user.blockingDateHome,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFF59070),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_auto_delete_24),
                detailName = stringResource(id = R.string.new_date_of_elimination),
                detailValue = user.dateOfEliminationHome!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF828686),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.baseline_request_quote_24),
                detailName = stringResource(id = R.string.new_quote_fund),
                detailValue = user.quotePaid!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFFADAF62),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.ic_baseline_attach_money_24),
                detailName = stringResource(id = R.string.new_voucher),
                detailValue = user.voucher!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF857564),
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
            NautaDetail(
                detailIcon = ImageVector.vectorResource(id = R.drawable.ic_baseline_attach_money_24),
                detailName = stringResource(id = R.string.new_debt),
                detailValue = user.debt!!,
                isLoading = isLoading,
                isFoundErrors = !isOk,
                backgroundColor = Color(0xFF046B79),
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                    .weight(.5f)
            )
        }
    }
}

val nautaUserHome = UserModel(
    id = 5,
    username = "user.name",
    password = "somePassword",
    blockingDate = "25/12/2025",
    dateOfElimination = "26/1/2026",
    accountType = "Prepago",
    serviceType = NavigationType.INTERNATIONAL,
    credit = 59.64f,
    remainingTime = 5342,
    email = "user.name@nauta.cu",
    offer = "NH RESIDENCIAL 1024/512 (40h) - RP",
    monthlyFee = "$300,00 CUP",
    downloadSpeeds = "1024 kbps",
    uploadSpeeds = "512 kbps",
    phone = "########",
    linkIdentifiers = "H ED######",
    linkStatus = "HABILITADO",
    activationDate = "25/02/2021",
    blockingDateHome = "10/04/2023",
    dateOfEliminationHome = "10/05/2023",
    quotePaid = "$0,81 CUP",
    voucher = "$0,00 CUP",
    debt = "$0,00 CUP"
)

@Preview(showBackground = true)
@Composable
fun CardNautaHomeDetailsPreview() {
    SuitEtecsaTheme {
        CardNautaHomeDetails(
            user = nautaUserHome,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            loginStatus = Pair(true, null),
            isLoading = false
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardNautaHomeDetailsPreviewDark() {
    SuitEtecsaTheme {
        CardNautaHomeDetails(
            user = nautaUserHome,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            loginStatus = Pair(true, null),
            isLoading = false
        )
    }
}