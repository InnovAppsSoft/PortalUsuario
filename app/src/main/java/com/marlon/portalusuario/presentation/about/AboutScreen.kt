package com.marlon.portalusuario.presentation.about

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val versionName =
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (_: PackageManager.NameNotFoundException) {
            "?"
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        Text(
            text = "Portal Usuario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Versión $versionName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Acerca de",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text =
                "Portal Usuario es una aplicación que te permite gestionar tus servicios " +
                    "móviles en Cuba de forma rápida y sencilla.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Redes del desarrollador",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SocialLinkCard("Facebook", "https://facebook.com/javyalejandro99")
        SocialLinkCard("Instagram", "https://www.instagram.com/jalexoasismusic")
        SocialLinkCard("Twitter", "https://twitter.com/javyalejandro99")
        SocialLinkCard("Telegram", "https://t.me/jalexcode")
        SocialLinkCard("GitHub", "https://github.com/jalexcode")

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Descargar desde",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SocialLinkCard("Google Play", "https://play.google.com/store/apps/details?id=com.marlon.portalusuario")
        SocialLinkCard("Apklis", "https://www.apklis.cu/application/com.marlon.portalusuario")
        SocialLinkCard("APKPure", "https://m.apkpure.com/es/portal-usuario/com.marlon.portalusuario")
    }
}

@Composable
private fun SocialLinkCard(
    label: String,
    url: String,
) {
    val context = LocalContext.current

    Card(
        onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
