package com.marlon.portalusuario

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.arr.bugsend.BugSend
import com.marlon.portalusuario.navigation.RootNavGraph
import com.marlon.portalusuario.presentation.apklisupdate.components.ApklisUpdateDialog
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.uci.apklisupdate.ApklisUpdate
import cu.uci.apklisupdate.UpdateCallback
import cu.uci.apklisupdate.model.AppUpdateInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var appUpdateInfo: AppUpdateInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BugSend(this)
            .setTitle(getString(R.string.bug_send_title))
            .setIcon(R.drawable.outline_bug_report_24)
            .setMessage(getString(R.string.bug_send_message))
            .setEmail(BuildConfig.SUPPORT_EMAIL)
            .setSubject("REPORT/PortalUsuario")
            .setExtraText("App Version: ${BuildConfig.VERSION_NAME}")
            .show()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val isShowingDialog = mutableStateOf(false)

        ApklisUpdate.hasAppUpdate(
            this,
            object : UpdateCallback {
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

                override fun onNewUpdate(appUpdateInfo: AppUpdateInfo) {
                    isShowingDialog.value = true
                    this@MainActivity.appUpdateInfo = appUpdateInfo
                }

                override fun onOldUpdate(appUpdateInfo: AppUpdateInfo) {
                    println(appUpdateInfo.last_release.version_name)
                }
            }
        )

        setContent {
            SuitEtecsaTheme {
                if (isShowingDialog.value) {
                    ApklisUpdateDialog(appUpdateInfo = appUpdateInfo!!) {
                        isShowingDialog.value = false
                    }
                }

                Content()
            }
        }
    }
}

@Composable
fun Content() {
    RootNavGraph(navController = rememberNavController())
}
