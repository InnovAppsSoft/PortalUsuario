package com.marlon.portalusuario.presentation.splash

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.presentation.splash.screen.SplashScreen
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ActivitySplash"

@AndroidEntryPoint
class ActivitySplash : ComponentActivity() {
    @Inject
    lateinit var appPreferencesManager: AppPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val preferences = appPreferencesManager.preferences().first()

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

            setContent {
                PortalUsuarioTheme(darkTheme = uiMode ?: isSystemInDarkTheme()) {
                    Surface { SplashScreen() }
                }
            }
        }

    }
}
