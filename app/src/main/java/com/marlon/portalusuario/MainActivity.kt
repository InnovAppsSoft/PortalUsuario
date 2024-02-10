package com.marlon.portalusuario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import com.marlon.portalusuario.navigation.RootNavGraph
import com.marlon.portalusuario.presentation.apklisupdate.components.ApklisUpdateDialog
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.uci.apklisupdate.ApklisUpdate
import cu.uci.apklisupdate.UpdateCallback
import cu.uci.apklisupdate.model.AppUpdateInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var appUpdateInfo: AppUpdateInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
