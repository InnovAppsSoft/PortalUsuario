package com.marlon.portalusuario.feature.mobileservices.presentation.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.core.theme.BrightOrange
import com.marlon.portalusuario.core.theme.PortalUsuarioTheme
import com.marlon.portalusuario.core.theme.VibrantGreen
import com.marlon.portalusuario.core.theme.VividRed
import io.github.suitetecsa.sdk.android.utils.asDate
import org.ocpsoft.prettytime.PrettyTime

@Composable
fun BalanceSection(
    modifier: Modifier = Modifier,
    balanceCredit: String = "1500.00 CUP",
    lockDate: String = "31/11/2024",
    deletionDate: String = "31/12/2024",
) {
    val lockDays = computeRemainingDays(lockDate)
    val deletionDays = computeRemainingDays(deletionDate)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.balance_credit),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = balanceCredit,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                DateLabel(
                    label = stringResource(id = R.string.expires, "").substringBefore("%"),
                    date = formatDate(lockDate),
                    remainingDays = lockDays,
                )
                HorizontalDivider(
                    modifier = Modifier.height(40.dp).width(1.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                DateLabel(
                    label = stringResource(id = R.string.elimination, "").substringBefore("%"),
                    date = formatDate(deletionDate),
                    remainingDays = deletionDays,
                )
            }
        }
    }
}

@Composable
private fun DateLabel(
    label: String,
    date: String,
    remainingDays: Int,
) {
    val color = dateColor(remainingDays)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = color,
        )
        Text(
            text = remainingDaysText(remainingDays),
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f),
        )
    }
}

private fun computeRemainingDays(dateStr: String): Int {
    return try {
        val date = dateStr.replace("/", "-").asDate
        val now = System.currentTimeMillis()
        val diff = date!!.time - now
        (diff / (1000 * 60 * 60 * 24)).toInt()
    } catch (_: Exception) {
        Int.MAX_VALUE
    }
}

private fun formatDate(dateStr: String): String {
    return try {
        val date = dateStr.replace("/", "-").asDate
        PrettyTime().format(date)
    } catch (_: Exception) {
        dateStr
    }
}

private fun dateColor(remainingDays: Int): Color {
    return when {
        remainingDays < 0 -> VividRed
        remainingDays < 7 -> VividRed
        remainingDays < 30 -> BrightOrange
        else -> VibrantGreen
    }
}

private fun remainingDaysText(remainingDays: Int): String {
    return when {
        remainingDays < 0 -> "Vencido"
        remainingDays == 0 -> "Hoy"
        remainingDays == 1 -> "1 día"
        remainingDays < 30 -> "$remainingDays días"
        else -> "+30 días"
    }
}

@Preview(showBackground = true, device = "id:Galaxy Nexus", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun BalanceCardPreview() {
    PortalUsuarioTheme {
        BalanceSection(modifier = Modifier.padding(16.dp))
    }
}
