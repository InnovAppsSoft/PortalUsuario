package com.marlon.portalusuario.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.marlon.portalusuario.PUNotifications.PUNotificationsActivity
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ViewModel.PunViewModel
import com.marlon.portalusuario.components.SetLTEModeDialog
import com.marlon.portalusuario.databinding.ActivityMainBinding
import com.marlon.portalusuario.databinding.DialogSetOnlyLteBinding
import com.marlon.portalusuario.errores_log.LogFileViewerActivity
import com.marlon.portalusuario.huella.BiometricCallback
import com.marlon.portalusuario.huella.BiometricManager
import com.marlon.portalusuario.promotions.PromotionsConfig
import com.marlon.portalusuario.promotions.PromotionsViewModel
import com.marlon.portalusuario.trafficbubble.FloatingBubbleService
import com.marlon.portalusuario.une.UneActivity
import com.marlon.portalusuario.view.fragments.CuentasFragment
import com.marlon.portalusuario.view.fragments.PaquetesFragment
import com.marlon.portalusuario.view.fragments.ServiciosFragment
import cu.suitetecsa.nautanav.ui.ConnectivityFragment
import cu.uci.apklisupdate.ApklisUpdate
import cu.uci.apklisupdate.UpdateCallback
import cu.uci.apklisupdate.model.AppUpdateInfo
import cu.uci.apklisupdate.view.ApklisUpdateDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BiometricCallback {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var connectivityFragment: ConnectivityFragment
    private val promotionsViewModel by viewModels<PromotionsViewModel>()

    // Promotions
    private var showPromotions by Delegates.notNull<Boolean>()
    private lateinit var promotionsConfig: PromotionsConfig

    // Network change listener
    private var showTrafficBubble by Delegates.notNull<Boolean>()
    private lateinit var connectivityManager: ConnectivityManager
    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Log.d("NetworkCallback", "Network is available: $network")
            val networkType = getNetworkType()
            if (showTrafficBubble) {
                Log.d("NetworkCallback", "Starting floating bubble service :: $networkType")
                stopService(Intent(applicationContext, FloatingBubbleService::class.java))
                startService(Intent(applicationContext, FloatingBubbleService::class.java).apply {
                    this.putExtra("networkType", networkType)
                })
            }
        }

        override fun onLost(network: Network) {
            // La red se ha perdido
            Log.d("NetworkCallback", "Network is lost: $network")
            val networkType = getNetworkType()
            Log.d("NetworkCallback", "Stopping floating bubble service :: $networkType")
            stopService(Intent(applicationContext, FloatingBubbleService::class.java))

            networkType?.also {
                Log.d("NetworkCallback", "Starting floating bubble service :: $it")
                startService(Intent(applicationContext, FloatingBubbleService::class.java).apply {
                    this.putExtra("networkType", it)
                })
            }
        }

        private fun getNetworkType(): String? {
            val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            return manager.activeNetwork?.let { network ->
                manager.getNetworkCapabilities(network)?.let { capabilities ->
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile"
                        else -> null
                    }
                }
            }
        }
    }

    // VARS
    private var appName: String? = null

    // SETTINGS
    lateinit var settings: SharedPreferences

    private var mBiometricManager: BiometricManager? = null

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
                "show_etecsa_promo_carousel" -> {
                    promotionsConfig.setCarouselVisibility(preferences.getBoolean(it, true))
                }

                "keynoche" -> when (preferences.getString("keynoche", "oscuro")) {
                    "claro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "oscuro" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

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
                                    Uri.parse("package:" + this.packageName)
                                )
                            )
                        } else {
                            this.stopService(Intent(this, FloatingBubbleService::class.java))
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

        // Settings preferences
        settings = getDefaultSharedPreferences(this)

        // Promotions
        showPromotions = settings.getBoolean("show_etecsa_promo_carousel", true)
        setupPromotions()

        // Traffic bubble
        showTrafficBubble = settings.getBoolean("show_traffic_speed_bubble", false)
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Listen preferences
        settings.registerOnSharedPreferenceChangeListener(::listenPreferences)

        punViewModel = ViewModelProvider(this)[PunViewModel::class.java]
        appName = packageName

        // VALORES POR DEFECTO EN LAS PREFERENCIAS
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        requestPermissions()

        context = this
        // drawer Nav View
        setUpDrawer()

        // Huella Seguridad
        if (settings.getBoolean("show_fingerprint", false)) {
            startFingerprint()
        }

        // Actualizacion de Aplicacion Apklis
        if (settings.getBoolean("start_checking_for_updates", true)) {
            ApklisUpdate.hasAppUpdate(
                this,
                object : UpdateCallback {
                    override fun onError(e: Throwable) {
                        // Not yet implemented
                    }

                    override fun onNewUpdate(appUpdateInfo: AppUpdateInfo) {
                        ApklisUpdateDialog(
                            this@MainActivity,
                            appUpdateInfo,
                            ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
                        ).show()
                    }

                    override fun onOldUpdate(appUpdateInfo: AppUpdateInfo) {
                        // Not yet implemented
                    }
                }
            )
        }

        // check if there are unseen notifications
        setupBadge()
        binding.contentMain.btNotification.setOnClickListener {
            val i = Intent(this@MainActivity, PUNotificationsActivity::class.java)
            startActivity(i)
            Toast.makeText(
                this@MainActivity,
                "Espera la nueva funcionalidad en próximas versiones 😉",
                Toast.LENGTH_SHORT
            ).show()
        }

        //
        setFragment(CuentasFragment(), "Servicios")
    }

    private fun setUpDrawer() {
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.micuenta -> setFragment(CuentasFragment(), "Mi Cuenta")
                R.id.services -> setFragment(ServiciosFragment<Any?>(), "Servicios")
                R.id.plans -> setFragment(PaquetesFragment(), "Planes")
                R.id.connectivity -> setFragment(connectivityFragment, "Conectividad")
                R.id.networkChange -> SetLTEModeDialog(this)
                R.id.une -> startActivity(Intent(this@MainActivity, UneActivity::class.java))
                R.id.errors_register ->
                    startActivity(
                        Intent(
                            this@MainActivity,
                            LogFileViewerActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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

                R.id.telegram_channel -> startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://t.me/portalusuario") // TODO: Actualizar la url del canal
                })

                R.id.facebook -> startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://www.facebook.com/portalusuario")
                })

                R.id.whatsapp -> startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "https://chat.whatsapp.com/HT6bKjpXHrN4FAyTAcy1Xn") // TODO: Creo que este grupo ya no existe
                })

                R.id.betatesters -> startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://t.me/portalusuarioBT") // TODO: Actualizar la url del grupo
                })

                R.id.invite ->  inviteUser()

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

    private fun setupBadge() {
        val sharedPreferences = getDefaultSharedPreferences(this)
        val count = sharedPreferences.getInt("notifications_count", 0)
        Log.e("UNSEE NOTIFICATIONS", count.toString())
        if (count == 0) {
            if (binding.contentMain.cartBadgeCount.visibility != View.GONE) {
                binding.contentMain.cartBadgeCount.visibility = View.GONE
            }
        } else {
            if (binding.contentMain.cartBadgeCount.visibility != View.VISIBLE) {
                binding.contentMain.cartBadgeCount.visibility = View.VISIBLE
            }
        }
        binding.contentMain.cartBadgeCount.text = count.toString()
    }

    // setUp carousel state
    private fun setupPromotions() {
        promotionsConfig = PromotionsConfig.Builder()
            .activity(this)
            .viewModel(promotionsViewModel)
            .binding(binding.contentMain)
            .build()
        promotionsConfig.setup(showPromotions)
    }

    // Huella de Seguridad
    private fun startFingerprint() {
        val settings = getDefaultSharedPreferences(this)
        val showFingerprint = settings.getBoolean("show_fingerprint", false)
        if (showFingerprint) {
            mBiometricManager = BiometricManager.BiometricBuilder(this@MainActivity)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setDescription(getString(R.string.biometric_description))
                .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                .build()

            // start authentication
            mBiometricManager!!.authenticate(this@MainActivity)
        }
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
            )) {
            startActivity(Intent(this, PermissionActivity::class.java))
        }
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }


    public override fun onResume() {
        super.onResume()
        settings.registerOnSharedPreferenceChangeListener(::listenPreferences)
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
        settings.unregisterOnSharedPreferenceChangeListener(::listenPreferences)
    }

    public override fun onDestroy() {
        super.onDestroy()
        settings.unregisterOnSharedPreferenceChangeListener(::listenPreferences)
    }

    override fun onSdkVersionNotSupported() {
        Toast.makeText(
            applicationContext,
            getString(R.string.biometric_error_sdk_not_supported),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationNotSupported() {
        Toast.makeText(
            applicationContext,
            getString(R.string.biometric_error_hardware_not_supported),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationNotAvailable() {
        Toast.makeText(
            applicationContext,
            getString(R.string.biometric_error_fingerprint_not_available),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(
            applicationContext,
            getString(R.string.biometric_error_permission_not_granted),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBiometricAuthenticationInternalError(error: String) {
        Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() { /* no-op */
    }

    override fun onAuthenticationCancelled() {
        mBiometricManager!!.cancelAuthentication()
        finish()
    }

    override fun onAuthenticationSuccessful() { /* no-op */
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) { /* no-op */
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        mBiometricManager!!.cancelAuthentication()
        finish()
    }

    companion object {
        private var context: Context? = null

        private var punViewModel: PunViewModel? = null

        @JvmStatic
        fun insertNotification() {
            punViewModel!!.insertPUN(null)
        }
    }
}
