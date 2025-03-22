package com.marlon.portalusuario.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.presentation.settings.SettingsScreen
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.util.NetworkConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsActivity"

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    @Inject
    lateinit var appPreferencesManager: AppPreferencesManager

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            appPreferencesManager.preferences().collect { preferences ->
                val uiMode = when (preferences.modeNight) {
                    ModeNight.YES -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        Log.i(TAG, "onCreate: Changed modeNight to Dark")
                        true
                    }

                    ModeNight.NO -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        Log.i(TAG, "onCreate: Changed modeNight to Light")
                        false
                    }

                    ModeNight.FOLLOW_SYSTEM -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        Log.i(TAG, "onCreate: Changed modeNight to System")
                        null
                    }
                }

                if (preferences.isShowingTrafficBubble) {
                    if (!Settings.canDrawOverlays(this@SettingsActivity)) {
                        Toast.makeText(
                            this@SettingsActivity,
                            "Otorgue a Portal Usuario los permisos requeridos",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                ("package:" + this@SettingsActivity.packageName).toUri()
                            )
                        )
                    } else if (!networkConnectivityObserver.isCallbackRegistered) networkConnectivityObserver.startMonitoring()
                } else {
                    if (networkConnectivityObserver.isCallbackRegistered) networkConnectivityObserver.stopMonitoring()
                }

                setContent {
                    PortalUsuarioTheme(darkTheme = uiMode ?: isSystemInDarkTheme()) { Surface { SettingsScreen() } }
                }
            }
        }
    }
}
