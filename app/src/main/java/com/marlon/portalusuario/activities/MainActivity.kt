package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.marlon.portalusuario.Permisos.PermissionActivity
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ViewModel.PunViewModel
import com.marlon.portalusuario.components.SetLTEModeDialog
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.databinding.ActivityMainBinding
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.errores_log.LogFileViewerActivity
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesFragment
import com.marlon.portalusuario.trafficbubble.FloatingBubbleService
import com.marlon.portalusuario.une.UneActivity
import com.marlon.portalusuario.util.NetworkConnectivityObserver
import com.marlon.portalusuario.util.Utils.hasPermissions
import com.marlon.portalusuario.view.fragments.PaquetesFragment
import com.marlon.portalusuario.view.fragments.ServiciosFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appPreferencesManager: AppPreferencesManager

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    private lateinit var binding: ActivityMainBinding

    // Network change listener
    private var showTrafficBubble by Delegates.notNull<Boolean>()

    // VARS
    private var appName: String? = null

    // SETTINGS
    lateinit var settings: SharedPreferences

    private fun setFragment(fragment: Fragment?, title: String?) {
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.fragmentContainer, fragment!!)
            .commit()
        binding.contentMain.tvAppTitle.text = title
    }

    private fun listenPreferences(preferences: SharedPreferences, key: String?) {
        key?.let {
            when (it) {
                "show_traffic_speed_bubble" -> {
                    if (preferences.getBoolean("show_traffic_speed_bubble", false)) {
                        if (!Settings.canDrawOverlays(this)) {
                            Toast.makeText(
                                this,
                                "Otorgue a Portal Usuario los permisos requeridos",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    ("package:" + this.packageName).toUri()
                                )
                            )
                        } else {
                            val serviceIntent = Intent(this, FloatingBubbleService::class.java)
                            stopService(serviceIntent)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            appPreferencesManager.preferences().collect { preferences ->
                when (preferences.modeNight) {
                    ModeNight.YES -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        Log.i(TAG, "onCreate: Changed modeNight to Dark")
                    }
                    ModeNight.NO -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        Log.i(TAG, "onCreate: Changed modeNight to Light")
                    }
                    ModeNight.FOLLOW_SYSTEM -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        Log.i(TAG, "onCreate: Changed modeNight to System")
                    }
                }

                if (preferences.isShowingTrafficBubble) {
                    Log.d(TAG, "onCreate: isShowingTrafficBubble = true")
                    if (!Settings.canDrawOverlays(this@MainActivity)) {
                        Toast.makeText(
                            this@MainActivity,
                            "Otorgue a Portal Usuario los permisos requeridos",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                ("package:" + this@MainActivity.packageName).toUri()
                            )
                        )
                    } else if (!networkConnectivityObserver.isCallbackRegistered) {
                        Log.d(TAG, "onCreate: isCallbackRegistered = false")
                        networkConnectivityObserver.startMonitoring()
                    }
                } else {
                    Log.d(TAG, "onCreate: isShowingTrafficBubble = false")
                    if (networkConnectivityObserver.isCallbackRegistered) networkConnectivityObserver.stopMonitoring()
                }
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Settings preferences
        settings = getDefaultSharedPreferences(this)

        // Traffic bubble
        showTrafficBubble = settings.getBoolean("show_traffic_speed_bubble", false)

        // Listen preferences
        settings.registerOnSharedPreferenceChangeListener(::listenPreferences)

        punViewModel = ViewModelProvider(this)[PunViewModel::class.java]
        appName = packageName

        // VALORES POR DEFECTO EN LAS PREFERENCIAS
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        requestPermissions()

        // drawer Nav View
        setUpDrawer()
        //
        setFragment(MobileServicesFragment(), "Servicios")
    }

    private fun setUpDrawer() {
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.micuenta -> setFragment(MobileServicesFragment(), "Mi Cuenta")
                R.id.services -> setFragment(ServiciosFragment<Any?>(), "Servicios")
                R.id.plans -> setFragment(PaquetesFragment(), "Planes")
                R.id.networkChange -> SetLTEModeDialog(this)
                R.id.une -> startActivity(Intent(this@MainActivity, UneActivity::class.java))
                R.id.errors_register ->
                    startActivity(
                        Intent(
                            this@MainActivity,
                            LogFileViewerActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )

                R.id.feedback -> {
                    val debugInfo = """
                            
                            
                            ---
                            OS Version: ${System.getProperty("os.version")} (${Build.VERSION.INCREMENTAL})}
                            Android API: ${Build.VERSION.SDK_INT}
                            Model (Device): ${Build.MODEL} (${Build.DEVICE})
                            Manufacturer: ${Build.MANUFACTURER}
                            ---
                    """.trimIndent()
                    val intent = Intent(
                        Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", getString(R.string.feedback_email), null)
                    )
                    intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.feedback_email))
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject))
                    intent.putExtra(Intent.EXTRA_TEXT, debugInfo)
                    startActivity(Intent.createChooser(intent, "Enviar Feedback usando..."))
                }

                R.id.telegram_channel -> startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = "https://t.me/portalusuario".toUri()
                    }
                )

                R.id.facebook -> startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = "https://www.facebook.com/portalusuario".toUri()
                    }
                )

                R.id.whatsapp -> startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = "https://chat.whatsapp.com/HT6bKjpXHrN4FAyTAcy1Xn".toUri()
                    }
                )

                R.id.betatesters -> startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = "https://t.me/portalusuarioBT".toUri()
                    }
                )

                R.id.invite -> inviteUser()

                R.id.politicadeprivacidad -> startActivity(Intent(this@MainActivity, PrivacyActivity::class.java))

                R.id.settings -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))

                R.id.about -> startActivity(Intent(this@MainActivity, AboutActivity::class.java))

                R.id.donate -> startActivity(Intent(this@MainActivity, DonationActivity::class.java))
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            false
        }
        binding.contentMain.menu.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
    }

    // Invitar Usuario
    private fun inviteUser() {
        IntentBuilder(this)
            .setText(getString(R.string.invite_user) + packageName)
            .setType("text/plain")
            .setChooserTitle("Compartir:")
            .startChooser()
    }

    // Permisos Consedidos
    fun requestPermissions() {
        if (hasPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            startActivity(Intent(this, PermissionActivity::class.java))
        }
    }

    public override fun onResume() {
        super.onResume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private var punViewModel: PunViewModel? = null

        @JvmStatic
        fun insertNotification() {
            punViewModel!!.insertPUN(null)
        }
    }
}
