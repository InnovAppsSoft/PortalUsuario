package com.marlon.portalusuario.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.navigation.AuthGraph
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        setContent {
            PortalUsuarioTheme(darkTheme = settings.getString("keynoche", "") == "oscuro") {
                Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    AuthGraph(navController = rememberNavController())
                }
            }
        }
    }
}
