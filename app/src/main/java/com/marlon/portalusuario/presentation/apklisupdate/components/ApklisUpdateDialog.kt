package com.marlon.portalusuario.presentation.apklisupdate.components

import android.content.res.Configuration
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import cu.suitetecsa.core.ui.components.prettycard.PrettyCard
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.uci.apklisupdate.model.AppUpdateInfo
import cu.uci.apklisupdate.model.Developer
import cu.uci.apklisupdate.model.LastRelease

@Composable
fun ApklisUpdateDialog(
    appUpdateInfo: AppUpdateInfo,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PrettyCard(modifier = Modifier.padding(16.dp).sizeIn(maxWidth = 360.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(4.dp))
                AsyncImage(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    model = appUpdateInfo.last_release.icon,
                    contentDescription = null,
                )
                Text(text = appUpdateInfo.name)
                Text(text = "v${appUpdateInfo.last_release.version_name}")
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Column {
                Html(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = appUpdateInfo.last_release.changelog
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Button(
                    shape = MaterialTheme.shapes.small,
                    onClick = {}
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        text = "Ver en APKLis"
                    )
                }
            }
        }
    }
}

@Composable
fun Html(text: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
            }
        }
    )
}

@Preview(
    device = "id:Nexus S",
    name = "PortalUsuario update",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ApklisUpdateDialogPreview() {
    SuitEtecsaTheme {
        Surface {
            ApklisUpdateDialog(
                appUpdateInfo = AppUpdateInfo(
                    listOf(),
                    false,
                    "",
                    Developer(ByteArray(0), "", "", "", 0, false, "", 0, ""),
                    0,
                    0,
                    LastRelease(
                        "",
                        "",
                        "",
                        "",
                        0,
                        listOf(),
                        false,
                        "",
                        listOf(),
                        "",
                        0,
                        0,
                        "8.0",
                        0,
                        "",
                        0,
                        ""
                    ),
                    "", "PortalUsuario", 0, "", 0, false, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, "", ""
                )
            ) {}
        }
    }
}
