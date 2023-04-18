package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.nauta.data.entities.User

@Composable
fun CardNautaHomeDetails(
    modifier: Modifier = Modifier,
    user: User,
    isLoading: Boolean = false,
    isFoundErrors: Boolean = false
) {
    PrettyCard(
        modifier = modifier,
        isLoading = isLoading,
        isFoundErrors = isFoundErrors
    ) {
        val privateModifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)

        Column(modifier = privateModifier) {
            Text(
                text = stringResource(R.string.new_link),
                style = MaterialTheme.typography.h4,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
            Surface(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colors.onSurface
            ) {

            }
            Spacer(modifier = Modifier.padding(8.dp))
            NautaDetail(
                detailName = stringResource(R.string.new_offer),
                detailValue = user.offer!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_monthly_fee),
                detailValue = user.monthlyFee!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_download_speed),
                detailValue = user.downloadSpeed!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_upload_speed),
                detailValue = user.uploadSpeed!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_phone),
                detailValue = user.phone!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_link_identifier),
                detailValue = user.linkIdentifiers!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_link_status),
                detailValue = user.linkStatus!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_activation_date),
                detailValue = user.activationDate!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_blocking_date),
                detailValue = user.blockingDateHome!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_date_of_elimination),
                detailValue = user.dateOfEliminationHome!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_quote_fund),
                detailValue = user.quotePaid!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_voucher),
                detailValue = user.voucher!!,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_debt),
                detailValue = user.debt!!,
                modifier = privateModifier
            )
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun CardNautaHomeDetailsPreview() {
    val user = User(
        userName = "user.name",
        password = "somePassword",
        accountNavigationType = NavigationType.INTERNATIONAL,
        lastConnection = 0L,
        blockingDate = "25/12/2025",
        dateOfElimination = "26/1/2026",
        accountType = "Navegacion Internacional",
        serviceType = "Prepago",
        credit = "$59,64 CUP",
        time = "04:23:15",
        mailAccount = "user.name@nauta.cu",
        offer = "NH RESIDENCIAL 1024/512 (40h) - RP",
        monthlyFee = "$300,00 CUP",
        downloadSpeed = "1024 kbps",
        uploadSpeed = "512 kbps",
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
    CardNautaHomeDetails(
        user = user,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    )
}