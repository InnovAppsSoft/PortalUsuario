package com.marlon.portalusuario.activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.navigation.NavigationView
import com.marlon.portalusuario.PUNotifications.PUNotificationsActivity
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ViewModel.PunViewModel
import com.marlon.portalusuario.banner.PromotionsConfig
import com.marlon.portalusuario.banner.PromotionsViewModel
import com.marlon.portalusuario.databinding.ActivityMainBinding
import com.marlon.portalusuario.errores_log.JCLogging
import com.marlon.portalusuario.errores_log.LogFileViewerActivity
import com.marlon.portalusuario.huella.BiometricCallback
import com.marlon.portalusuario.huella.BiometricManager
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
import io.github.suitetecsa.sdk.android.utils.extractShortNumber
import io.github.suitetecsa.sdk.android.utils.validateFormat
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

    private var details: TextView? = null
    private var titleTextView: TextView? = null
    private var log: TextView? = null

    private var nameTV: TextView? = null
    private var mailTV: TextView? = null
    private var profileIV: ImageView? = null

    private var downloadApklis: Button? = null
    private var downloadPs: Button? = null
    private var remindMeLater: Button? = null
    private var notificationBtn: FrameLayout? = null
    private lateinit var menu: ImageView
    private var cartBadge: TextView? = null
    private var drawer: DrawerLayout? = null

    // VARS
    private var appName: String? = null

    // SETTINGS
    lateinit var settings: SharedPreferences

    // LOGGING
    private var jcLogging: JCLogging? = null

    private var mBiometricManager: BiometricManager? = null

    private fun setFragment(fragment: Fragment?, title: String?) {
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.fragmentContainer, fragment!!)
            .commit()
        titleTextView!!.text = title
    }

    // preference dualSim
    private var simPreferences: SharedPreferences? = null
    private var simCard: String? = null

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
                            startActivityForResult(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + this.packageName)
                                ), 0
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
        // Shorcuts
        shorcut()
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

        simPreferences = getDefaultSharedPreferences(this)
        context = this
        // drawer Layout
        drawer = findViewById(R.id.drawer_layout)
        // drawer Nav View
        navigationView = findViewById(R.id.nav_view)
        navigationView!!.setNavigationItemSelectedListener { item ->

            val i: Intent
            when (item.itemId) {
                R.id.micuenta -> setFragment(CuentasFragment(), "Mi Cuenta")
                R.id.services -> setFragment(ServiciosFragment<Any?>(), "Servicios")
                R.id.plans -> setFragment(PaquetesFragment(), "Planes")
                R.id.connectivity -> setFragment(connectivityFragment, "Conectividad")

                R.id.networkChange -> SetLTEModeDialog(context)
                R.id.une -> {
                    i = Intent(this@MainActivity, UneActivity::class.java)
                    startActivity(i)
                }

                R.id.errors_register -> startActivity(
                    Intent(
                        this@MainActivity,
                        LogFileViewerActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )

                R.id.feedback -> {
                    var debugInfo = "\n\n\n---"
                    debugInfo += "\nOS Version: " + System.getProperty("os.version") + " (" + Build.VERSION.INCREMENTAL + ")"
                    debugInfo += "\nAndroid API: " + Build.VERSION.SDK_INT
                    debugInfo += "\nModel (Device): " + Build.MODEL + " (" + Build.DEVICE + ")"
                    debugInfo += "\nManufacturer: " + Build.MANUFACTURER
                    debugInfo += "\n---"
                    val intent = Intent(
                        Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", context!!.getString(R.string.feedback_email), null)
                    )
                    intent.putExtra(
                        Intent.EXTRA_EMAIL,
                        context!!.getString(R.string.feedback_email)
                    )
                    intent.putExtra(
                        Intent.EXTRA_SUBJECT,
                        context!!.getString(R.string.feedback_subject)
                    )
                    intent.putExtra(Intent.EXTRA_TEXT, debugInfo)
                    startActivity(Intent.createChooser(intent, "Enviar Feedback usando..."))
                }

                R.id.telegram_channel -> {
                    val telegramUrl = ("https://t.me/portalusuario")
                    val telegramLaunch = Intent(Intent.ACTION_VIEW)
                    telegramLaunch.data = Uri.parse(telegramUrl)
                    startActivity(telegramLaunch)
                }

                R.id.facebook -> {
                    val facebookUrl = ("https://www.facebook.com/portalusuario")
                    val facebookLaunch = Intent(Intent.ACTION_VIEW)
                    facebookLaunch.data = Uri.parse(facebookUrl)
                    startActivity(facebookLaunch)
                }

                R.id.whatsapp -> {
                    val betaUrl = ("https://chat.whatsapp.com/HT6bKjpXHrN4FAyTAcy1Xn")
                    val betaLaunch = Intent(Intent.ACTION_VIEW)
                    betaLaunch.data = Uri.parse(betaUrl)
                    startActivity(betaLaunch)
                }

                R.id.betatesters -> {
                    val betaUrl = ("https://t.me/portalusuarioBT")
                    val betaLaunch = Intent(Intent.ACTION_VIEW)
                    betaLaunch.data = Uri.parse(betaUrl)
                    startActivity(betaLaunch)
                }

                R.id.invite -> {
                    inviteUser()
                }

                R.id.politicadeprivacidad -> {
                    i = Intent(this@MainActivity, PrivacyActivity::class.java)
                    startActivity(i)
                }

                R.id.settings -> {
                    i = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(i)
                }

                R.id.about -> {
                    i = Intent(this@MainActivity, AboutActivity::class.java)
                    startActivity(i)
                }

                R.id.donate -> {
                    i = Intent(this@MainActivity, DonationActivity::class.java)
                    startActivity(i)
                }
            }
            drawer!!.closeDrawer(GravityCompat.START)
            false
        }
        menu = findViewById(R.id.menu)
        menu.setOnClickListener { drawer!!.openDrawer(GravityCompat.START) }
        titleLayout = findViewById(R.id.titleLayout)
        titleTextView = findViewById(R.id.puTV)
        details = findViewById(R.id.details)
        log = findViewById(R.id.log)
        downloadApklis = findViewById(R.id.download_apklis)
        downloadApklis = findViewById(R.id.download_apklis)
        remindMeLater = findViewById(R.id.remind_me_later)
        nameTV = findViewById(R.id.textname)
        mailTV = findViewById(R.id.correotext)
        profileIV = findViewById(R.id.img_drawer_perfil)

        jcLogging = JCLogging(this)
        downloadApklis = findViewById(R.id.download_apklis)
        downloadPs = findViewById(R.id.download_ps)
        remindMeLater = findViewById(R.id.remind_me_later)

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
        cartBadge = findViewById(R.id.cart_badge)
        setupBadge()
        notificationBtn = findViewById(R.id.notificationBtn)
        notificationBtn!!.setOnClickListener {
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

    private fun setupBadge() {
        val sharedPreferences = getDefaultSharedPreferences(this)
        val count = sharedPreferences.getInt("notifications_count", 0)
        Log.e("UNSEE NOTIFICATIONS", count.toString())
        if (count == 0) {
            if (cartBadge!!.visibility != View.GONE) {
                cartBadge!!.visibility = View.GONE
            }
        } else {
            if (cartBadge!!.visibility != View.VISIBLE) {
                cartBadge!!.visibility = View.VISIBLE
            }
        }
        cartBadge!!.text = count.toString()
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

    private fun String?.showMessage(c: Context?) {
        Toast.makeText(c, this, Toast.LENGTH_SHORT).show()
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
        if ((
                    (
                            (
                                    (
                                            ContextCompat.checkSelfPermission(
                                                this,
                                                Manifest.permission.CALL_PHONE
                                            ) != PackageManager.PERMISSION_GRANTED
                                            ) || ContextCompat.checkSelfPermission(
                                        this,
                                        Manifest.permission.CAMERA
                                    ) != PackageManager.PERMISSION_GRANTED
                                    ) || ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_CONTACTS
                            ) != PackageManager.PERMISSION_GRANTED
                            ) || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    ) || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(Intent(this, PermissionActivity::class.java))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val errorMessage =
            "Cuidado... Faltan caracteres o su número seleccionado no es un número de telefonia móvil."
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                val uri = data!!.data
                val cursor = contentResolver.query(uri!!, null, null, null, null)
                if (cursor!!.moveToFirst()) {
                    val numberColumn =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val phoneNumber = cursor.getString(numberColumn)
                    validateFormat(phoneNumber)?.let {
                        extractShortNumber(it)?.let { shortNumber ->
                            ServiciosFragment.phoneNumber.setText(shortNumber)
                        } ?: run {
                            errorMessage.showMessage(this)
                        }
                    } ?: run {
                        errorMessage.showMessage(this)
                    }
                }
            }
        }

        // FLOATING BUBBLE SERVICE
        if (requestCode == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
                this
            )
        ) {
            startService(Intent(this, FloatingBubbleService::class.java))
        }
    }

    private fun shorcut() {
        if (Build.VERSION.SDK_INT >= 25) {
            val shortcutManager: ShortcutManager? =
                ContextCompat.getSystemService(this, ShortcutManager::class.java)
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:*222" + Uri.encode("#")))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("com.android.phone.force.slot", true)
            intent.putExtra("Cdma_Supp", true)
            if (simCard == "0") {
                for (s in CuentasFragment.simSlotName) {
                    intent.putExtra(s, 0)
                    intent.putExtra("com.android.phone.extra.slot", 0)
                }
            } else if (simCard == "1") {
                for (s in CuentasFragment.simSlotName) {
                    intent.putExtra(s, 1)
                    intent.putExtra("com.android.phone.extra.slot", 1)
                }
            }
            val saldoShortcut = ShortcutInfo.Builder(this, "shortcut_saldo")
                .setShortLabel("Saldo")
                .setLongLabel("Saldo")
                .setIcon(Icon.createWithResource(this, R.drawable.saldosh))
                .setIntent(intent)
                .setRank(2)
                .build()
            // shrtcuts bonos
            val bonos = Intent(Intent.ACTION_CALL, Uri.parse("tel:*222*266" + Uri.encode("#")))
            bonos.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            bonos.putExtra("com.android.phone.force.slot", true)
            bonos.putExtra("Cdma_Supp", true)
            if (simCard == "0") {
                for (s in CuentasFragment.simSlotName) {
                    bonos.putExtra(s, 0)
                    bonos.putExtra("com.android.phone.extra.slot", 0)
                }
            } else if (simCard == "1") {
                for (s in CuentasFragment.simSlotName) {
                    bonos.putExtra(s, 1)
                    bonos.putExtra("com.android.phone.extra.slot", 1)
                }
            }
            val bonosShortcut = ShortcutInfo.Builder(this, "shortcut_bono")
                .setShortLabel("Bonos")
                .setLongLabel("Bonos")
                .setIcon(Icon.createWithResource(this, R.drawable.bolsash))
                .setIntent(bonos)
                .setRank(1)
                .build()
            // shrtcuts datos
            val datos = Intent(Intent.ACTION_CALL, Uri.parse("tel:*222*328" + Uri.encode("#")))
            datos.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            datos.putExtra("com.android.phone.force.slot", true)
            datos.putExtra("Cdma_Supp", true)
            if (simCard == "0") {
                for (s in CuentasFragment.simSlotName) {
                    datos.putExtra(s, 0)
                    datos.putExtra("com.android.phone.extra.slot", 0)
                }
            } else if (simCard == "1") {
                for (s in CuentasFragment.simSlotName) {
                    datos.putExtra(s, 1)
                    datos.putExtra("com.android.phone.extra.slot", 1)
                }
            }
            val datosShortcut = ShortcutInfo.Builder(this, "shortcut_datos")
                .setShortLabel("Datos")
                .setLongLabel("Datos")
                .setIcon(Icon.createWithResource(this, R.drawable.datossh))
                .setIntent(datos)
                .setRank(0)
                .build()
            if (shortcutManager != null) {
                shortcutManager.dynamicShortcuts =
                    listOf(saldoShortcut, bonosShortcut, datosShortcut)
            }
        }
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

    private fun startFloatingBubbleService() {
        if (FloatingBubbleService.isStarted) {
            return
        }
        if (Settings.canDrawOverlays(this)) {
            Log.i("CALLING ON MA", "STARTING SERVICE")
            stopService(Intent(applicationContext, FloatingBubbleService::class.java))
            startService(Intent(applicationContext, FloatingBubbleService::class.java))
        }
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

    // Dialogo de Cambiar red
    inner class SetLTEModeDialog(context: Context?) {
        private val set4GBtn: Button

        init {
            val simDialog = Dialog(context!!)
            simDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            simDialog.setCancelable(true)
            simDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simDialog.setContentView(R.layout.dialog_set_only_lte)
            set4GBtn = simDialog.findViewById(R.id.set_4g)
            set4GBtn.setOnClickListener { v: View? -> openHiddenMenu() }
            simDialog.show()
        }

        fun openHiddenMenu() {
            try {
                val intent = Intent("android.intent.action.MAIN")
                if (Build.VERSION.SDK_INT >= 30) {
                    intent.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo")
                } else {
                    intent.setClassName("com.android.settings", "com.android.settings.RadioInfo")
                }
                startActivity(intent)
            } catch (unused: Exception) {
                Toast.makeText(
                    context,
                    "Su dispositivo no admite esta funcionalidad, lamentamos las molestias :(",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    // Promo ETECSA
    companion object {
        private var context: Context? = null
        private var titleLayout: LinearLayout? = null

        var navigationView: NavigationView? = null
        private var punViewModel: PunViewModel? = null

        @JvmStatic
        fun insertNotification() {
            punViewModel!!.insertPUN(null)
        }

        const val PICK_CONTACT_REQUEST = 1

        @JvmStatic
        fun openLink(link: String?) {
            try {
                // JCLogging.message("Opening PROMO URL::url=" + link, null);
                val url = Uri.parse(link)
                val openUrl = Intent(Intent.ACTION_VIEW, url)
                context!!.startActivity(openUrl)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
