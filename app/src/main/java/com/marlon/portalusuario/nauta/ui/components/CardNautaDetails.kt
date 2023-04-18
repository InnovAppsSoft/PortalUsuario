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
fun CardNautaDetails(
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
                text = stringResource(R.string.new_nauta),
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
                detailName = stringResource(R.string.new_account_credit),
                detailValue = user.credit,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_blocking_date),
                detailValue = user.blockingDate,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_date_of_elimination),
                detailValue = user.dateOfElimination,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_account_type),
                detailValue = user.accountType,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_service_type),
                detailValue = user.serviceType,
                modifier = privateModifier
            )
            NautaDetail(
                detailName = stringResource(R.string.new_mail_account),
                detailValue = user.mailAccount,
                modifier = privateModifier
            )
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun NautaDetail(modifier: Modifier = Modifier, detailName: String, detailValue: String) {
    Column(modifier = modifier) {
        Text(
            text = detailName,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = detailValue, fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun CardNautaDetailsPreview() {
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
        mailAccount = "user.name@nauta.cu"
    )
    CardNautaDetails(
        user = user,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    )
}