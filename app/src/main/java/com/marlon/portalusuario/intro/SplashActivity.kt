package com.marlon.portalusuario.intro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.presentation.splash.screen.SplashScreen
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    @Inject lateinit var preferences: AppPreferences
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        // APP THEME
        when (settings.getString("keynoche", "")) {
            "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) manageBatteryConsumption()
        if (!sharedPreferences.getBoolean("isIntroOpened", false)) {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }

        setContent {
            PortalUsuarioTheme(darkTheme = settings.getString("keynoche", "") == "oscuro") {
                SplashScreen()
            }
        }
    }

    private fun manageBatteryConsumption() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            startActivity(
                Intent().apply {
                    action = android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                    data = android.net.Uri.parse("package:$packageName")
                }
            )
        }
    }
}
