package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.R
import com.marlon.portalusuario.components.SetLTEModeDialog
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.navigation.PortalUsuarioNavHost
import com.marlon.portalusuario.navigation.Route
import com.marlon.portalusuario.trafficbubble.FloatingBubbleService
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.util.NetworkConnectivityObserver
import com.marlon.portalusuario.util.Utils.hasPermissions
import com.marlon.portalusuario.viewmodel.PunViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appPreferencesManager: AppPreferencesManager

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    private lateinit var settings: SharedPreferences

    private fun listenPreferences(
        preferences: SharedPreferences,
        key: String?,
    ) {
        key?.let {
            when (it) {
                "show_traffic_speed_bubble" -> {
                    if (preferences.getBoolean("show_traffic_speed_bubble", false)) {
                        if (!Settings.canDrawOverlays(this)) {
                            Toast.makeText(
                                this,
                                "Otorgue a Portal Usuario los permisos requeridos",
                                Toast.LENGTH_SHORT,
                            ).show()
                            startActivity(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    ("package:" + this.packageName).toUri(),
                                ),
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

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            appPreferencesManager.preferences().collect { preferences ->
                when (preferences.modeNight) {
                    ModeNight.YES -> {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES,
                        )
                        Log.i(TAG, "onCreate: Changed modeNight to Dark")
                    }
                    ModeNight.NO -> {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO,
                        )
                        Log.i(TAG, "onCreate: Changed modeNight to Light")
                    }
                    ModeNight.FOLLOW_SYSTEM -> {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                        )
                        Log.i(TAG, "onCreate: Changed modeNight to System")
                    }
                }

                if (preferences.isShowingTrafficBubble) {
                    Log.d(TAG, "onCreate: isShowingTrafficBubble = true")
                    if (!Settings.canDrawOverlays(this@MainActivity)) {
                        Toast.makeText(
                            this@MainActivity,
                            "Otorgue a Portal Usuario los permisos requeridos",
                            Toast.LENGTH_SHORT,
                        ).show()
                        startActivity(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                ("package:" + this@MainActivity.packageName).toUri(),
                            ),
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

        settings = PreferenceManager.getDefaultSharedPreferences(this)

        settings.registerOnSharedPreferenceChangeListener(::listenPreferences)

        if (hasPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        ) {
            startActivity(
                Intent(
                    this,
                    com.marlon.portalusuario.permisos.PermissionActivity::class.java,
                ),
            )
        }

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        punViewModel = ViewModelProvider(this)[PunViewModel::class.java]

        setContent {
            PortalUsuarioTheme {
                MainScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        settings.unregisterOnSharedPreferenceChangeListener(::listenPreferences)
    }

    companion object {
        private var punViewModel: PunViewModel? = null

        @JvmStatic
        fun insertNotification() {
            punViewModel!!.insertPUN(null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentTitle = routeTitle(currentRoute)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                DrawerContent(
                    currentRoute = currentRoute,
                    onItemClick = { action ->
                        when (action) {
                            is DrawerAction.Navigate -> {
                                navController.navigate(action.route.route) {
                                    launchSingleTop = true
                                }
                            }
                            is DrawerAction.ExternalUrl -> {
                                context.startActivity(Intent(Intent.ACTION_VIEW, action.url.toUri()))
                            }
                            is DrawerAction.SendEmail -> {
                                val intent =
                                    Intent(
                                        Intent.ACTION_SENDTO,
                                        Uri.fromParts("mailto", action.email, null),
                                    ).apply {
                                        putExtra(Intent.EXTRA_EMAIL, arrayOf(action.email))
                                        putExtra(Intent.EXTRA_SUBJECT, action.subject)
                                        putExtra(Intent.EXTRA_TEXT, action.body)
                                    }
                                context.startActivity(Intent.createChooser(intent, "Enviar Feedback usando..."))
                            }
                            is DrawerAction.ShareText -> {
                                ShareCompat.IntentBuilder(context as android.app.Activity)
                                    .setText(action.text)
                                    .setType("text/plain")
                                    .setChooserTitle("Compartir:")
                                    .startChooser()
                            }
                            is DrawerAction.ShowDialog -> {
                                action.show(context)
                            }
                        }
                        scope.launch { drawerState.close() }
                    },
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentTitle) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(),
                )
            },
        ) { padding ->
            PortalUsuarioNavHost(
                navController = navController,
                modifier = Modifier.padding(padding),
                startDestination = Route.MobileServices.route,
            )
        }
    }
}

private sealed class DrawerAction {
    class Navigate(val route: Route) : DrawerAction()

    class ExternalUrl(val url: String) : DrawerAction()

    class SendEmail(
        val email: String,
        val subject: String,
        val body: String,
    ) : DrawerAction()

    class ShareText(val text: String) : DrawerAction()

    class ShowDialog(val show: (android.content.Context) -> Unit) : DrawerAction()
}

@Composable
private fun DrawerContent(
    currentRoute: String?,
    onItemClick: (DrawerAction) -> Unit,
) {
    val context = LocalContext.current
    val inviteText = context.getString(R.string.invite_user)

    val feedbackBody =
        buildString {
            appendLine()
            appendLine()
            appendLine("---")
            appendLine("OS Version: ${System.getProperty("os.version")} (${Build.VERSION.INCREMENTAL})")
            appendLine("Android API: ${Build.VERSION.SDK_INT}")
            appendLine("Model (Device): ${Build.MODEL} (${Build.DEVICE})")
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("---")
        }

    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Portal Usuario",
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        DrawerSection(
            items =
                listOf(
                    DrawerItem("Donar al proyecto", Icons.Default.Favorite) {
                        DrawerAction.Navigate(Route.Donation)
                    },
                ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerSection(
            header = "Mi Cuenta",
            items =
                listOf(
                    DrawerItem("Mi Cuenta") { DrawerAction.Navigate(Route.MobileServices) },
                    DrawerItem("Servicios") { DrawerAction.Navigate(Route.Servicios) },
                    DrawerItem("Planes") { DrawerAction.Navigate(Route.Paquetes) },
                ),
        )

        DrawerSection(
            header = "Útiles",
            items =
                listOf(
                    DrawerItem("Forzar 4G") {
                        DrawerAction.ShowDialog { ctx ->
                            SetLTEModeDialog(ctx as android.app.Activity)
                        }
                    },
                    DrawerItem("Calculadora UNE") { DrawerAction.Navigate(Route.Une) },
                ),
        )

        DrawerSection(
            header = "Feedback",
            items =
                listOf(
                    DrawerItem("Errores") { DrawerAction.Navigate(Route.LogFileViewer) },
                    DrawerItem("Enviar Feedback") {
                        DrawerAction.SendEmail(
                            email = "portalusuarioapp@gmail.com",
                            subject = "Feedback Portal Usuario App",
                            body = feedbackBody,
                        )
                    },
                ),
        )

        DrawerSection(
            header = "Redes Sociales",
            items =
                listOf(
                    DrawerItem("Canal Telegram") { DrawerAction.ExternalUrl("https://t.me/portalusuario") },
                    DrawerItem("Facebook") { DrawerAction.ExternalUrl("https://www.facebook.com/portalusuario") },
                    DrawerItem("Beta Testers") { DrawerAction.ExternalUrl("https://t.me/portalusuarioBT") },
                    DrawerItem(
                        "WhatsApp",
                    ) { DrawerAction.ExternalUrl("https://chat.whatsapp.com/HT6bKjpXHrN4FAyTAcy1Xn") },
                    DrawerItem("Invitar") { DrawerAction.ShareText(inviteText) },
                ),
        )

        DrawerSection(
            header = "Política",
            items =
                listOf(
                    DrawerItem("Política de Privacidad") { DrawerAction.Navigate(Route.Privacy) },
                ),
        )

        DrawerSection(
            header = "Ajustes",
            items =
                listOf(
                    DrawerItem("Configuración") { DrawerAction.Navigate(Route.Settings) },
                    DrawerItem("Acerca de") { DrawerAction.Navigate(Route.About) },
                ),
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawerSection(
    header: String? = null,
    items: List<DrawerItem>,
) {
    header?.let {
        Text(
            text = it,
            modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
    items.forEach { item ->
        NavigationDrawerItem(
            label = { Text(item.label) },
            icon = item.icon?.let { icon -> { Icon(icon, contentDescription = null) } },
            selected = false,
            onClick = { item.onClick() },
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

private data class DrawerItem(
    val label: String,
    val icon: ImageVector? = null,
    val onClick: () -> DrawerAction,
)

@Composable
private fun routeTitle(route: String?): String {
    return when (route) {
        Route.Servicios.route -> "Servicios"
        Route.Paquetes.route -> "Planes"
        Route.MobileServices.route -> "Mi Cuenta"
        Route.Une.route -> "Calculadora UNE"
        Route.LogFileViewer.route -> "Errores"
        Route.Settings.route -> "Configuración"
        Route.About.route -> "Acerca de"
        Route.Privacy.route -> "Política de Privacidad"
        Route.Donation.route -> "Donar"
        else -> "Portal Usuario"
    }
}
