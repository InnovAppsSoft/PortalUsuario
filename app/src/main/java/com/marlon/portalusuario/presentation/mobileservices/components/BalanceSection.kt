package com.marlon.portalusuario.presentation.mobileservices.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.ui.theme.TealBlue
import io.github.suitetecsa.sdk.android.utils.asDate
import org.ocpsoft.prettytime.PrettyTime

@Composable
fun BalanceSection(
    modifier: Modifier = Modifier,
    balanceCredit: String = "1500.00 CUP",
    lockDate: String = "31/11/2024",
    deletionDate: String = "31/12/2024",
    isSimPaired: Boolean = false,
    onAddBalance: () -> Unit = {},
    onSendBalance: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.balance_credit),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = balanceCredit,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(id = R.string.expires, PrettyTime().format(lockDate.replace("/", "-").asDate)))
        Text(
            text = stringResource(
                id = R.string.elimination,
                PrettyTime().format(deletionDate.replace("/", "-").asDate)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (isSimPaired) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onAddBalance,
                    modifier = Modifier.background(TealBlue, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background
                    )
                }
                IconButton(
                    onClick = onSendBalance,
                    modifier = Modifier.background(TealBlue, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:Galaxy Nexus", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun BalanceCardPreview() {
    PortalUsuarioTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            BalanceSection(modifier = Modifier.padding(16.dp), isSimPaired = true)
        }
    }
}
