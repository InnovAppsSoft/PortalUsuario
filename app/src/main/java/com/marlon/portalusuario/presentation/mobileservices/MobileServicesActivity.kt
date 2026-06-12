package com.marlon.portalusuario.presentation.mobileservices

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.marlon.portalusuario.presentation.mobileservices.screen.MobileServicesScreen
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MobileServicesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PortalUsuarioTheme {
                Surface {
                    MobileServicesScreen()
                }
            }
        }
    }
}
