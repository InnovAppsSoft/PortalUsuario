package com.marlon.portalusuario.une

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UneActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PortalUsuarioTheme {
                Surface {
                    UneScreen(onBack = { finish() })
                }
            }
        }
    }
}
