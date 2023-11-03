package com.marlon.portalusuario.activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.marlon.portalusuario.R
import com.marlon.portalusuario.activities.MainActivity.Companion.setCarouselVisibility
import com.marlon.portalusuario.burbuja_trafico.FloatingBubbleService

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
        private var showTrafficSpeedBubble: SwitchPreferenceCompat? = null
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            // INIT
            showTrafficSpeedBubble = findPreference("show_traffic_speed_bubble")
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        override fun onResume() {
            super.onResume()
            // Registrar escucha
            preferenceScreen.sharedPreferences
                ?.registerOnSharedPreferenceChangeListener(this)
            //
            showTrafficSpeedBubble!!.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            //
            if (!Settings.canDrawOverlays(context)) {
                showTrafficSpeedBubble!!.isChecked = false
            }
        }

        override fun onPause() {
            super.onPause()
            // Eliminar registro de la escucha
            preferenceScreen.sharedPreferences
                ?.unregisterOnSharedPreferenceChangeListener(this)
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
            if (key == "keynoche") {
                if (sharedPreferences.getString("keynoche", "") == "claro") {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else if (sharedPreferences.getString("keynoche", "") == "oscuro") {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            } else if (key == "show_traffic_speed_bubble") {
                if (sharedPreferences.getBoolean("show_traffic_speed_bubble", false)) {
                    if (!Settings.canDrawOverlays(context)) {
                        Toast.makeText(
                            context,
                            "Otorgue a Portal Usuario los permisos requeridos",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivityForResult(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + requireContext().packageName)
                            ),
                            0
                        )
                    }
                } else {
                    requireContext().stopService(Intent(context, FloatingBubbleService::class.java))
                }
            } else if (key == "show_etecsa_promo_carousel") {
                val b = sharedPreferences.getBoolean("show_etecsa_promo_carousel", true)
                setCarouselVisibility(b)
            }
        }
    }
}
