package com.marlon.portalusuario.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marlon.portalusuario.R
import com.marlon.portalusuario.components.SetLTEModeDialog
import com.marlon.portalusuario.data.preferences.IAppPreferencesManager
import com.marlon.portalusuario.domain.model.ModeNight
import com.marlon.portalusuario.navigation.PortalUsuarioNavHost
import com.marlon.portalusuario.navigation.Route
import com.marlon.portalusuario.trafficbubble.FloatingBubbleService
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.util.NetworkConnectivityObserver
import com.marlon.portalusuario.util.Utils.hasPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.ui.res.stringResource

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appPreferencesManager: IAppPreferencesManager

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    private var isDynamicColor by mutableStateOf(true)

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startIntro = intent.getBooleanExtra("start_intro", false)
        val openNotifications = intent.getBooleanExtra("open_notifications", false)
        val permissionsMissing =
            hasPermissions(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        manageBatteryConsumption()

        lifecycleScope.launch {
            appPreferencesManager.preferences().collect { preferences ->
                isDynamicColor = preferences.isDynamicColor
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
                        Toast
                            .makeText(
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

                if (preferences.isShowingTrafficSpeedBubble) {
                    if (!Settings.canDrawOverlays(this@MainActivity)) {
                        Toast
                            .makeText(
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
                    } else {
                        stopService(Intent(this@MainActivity, FloatingBubbleService::class.java))
                    }
                }
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PortalUsuarioTheme(dynamicColor = isDynamicColor) {
                val startDestination =
                    when {
                        openNotifications -> Route.PUNotifications.createRoute()
                        startIntro -> Route.Intro.route
                        permissionsMissing -> Route.Permissions.route
                        else -> Route.MobileServices.route
                    }
                MainScreen(startDestination = startDestination)
            }
        }
    }

    private fun manageBatteryConsumption() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            startActivity(
                Intent().apply {
                    action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = "package:$packageName".toUri()
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(startDestination: String = Route.MobileServices.route) {
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
                                ShareCompat
                                    .IntentBuilder(context as android.app.Activity)
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
                startDestination = startDestination,
            )
        }
    }
}

private sealed class DrawerAction {
    class Navigate(
        val route: Route,
    ) : DrawerAction()

    class ExternalUrl(
        val url: String,
    ) : DrawerAction()

    class SendEmail(
        val email: String,
        val subject: String,
        val body: String,
    ) : DrawerAction()

    class ShareText(
        val text: String,
    ) : DrawerAction()

    class ShowDialog(
        val show: (android.content.Context) -> Unit,
    ) : DrawerAction()
}

private data class DrawerItem(
    val label: String,
    val icon: ImageVector? = null,
    val route: String? = null,
    val onClick: () -> DrawerAction,
)

@Composable
private fun DrawerContent(
    currentRoute: String?,
    onItemClick: (DrawerAction) -> Unit,
) {
    val context = LocalContext.current
    val inviteText = stringResource(R.string.invite_user)
    val versionName =
        try {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (_: Exception) {
            "?"
        }

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
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 28.dp, vertical = 24.dp),
        ) {
            Column {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Portal Usuario",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "v$versionName",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }
        }

        val sections =
            listOf(
                DrawerSectionData(
                    items =
                        listOf(
                            DrawerItem("Donar al proyecto", Icons.Default.Favorite, Route.Donation.route) {
                                DrawerAction.Navigate(Route.Donation)
                            },
                        ),
                ),
                DrawerSectionData(
                    header = "Mi Cuenta",
                    items =
                        listOf(
                            DrawerItem("Mi Cuenta", Icons.Default.Person, Route.MobileServices.route) {
                                DrawerAction.Navigate(Route.MobileServices)
                            },
                            DrawerItem("Servicios", Icons.Default.Build, Route.Servicios.route) {
                                DrawerAction.Navigate(Route.Servicios)
                            },
                            DrawerItem("Planes", Icons.Default.List, Route.Paquetes.route) {
                                DrawerAction.Navigate(Route.Paquetes)
                            },
                        ),
                ),
                DrawerSectionData(
                    header = "Útiles",
                    items =
                        listOf(
                            DrawerItem("Forzar 4G", Icons.Default.SignalCellularAlt) {
                                DrawerAction.ShowDialog { ctx ->
                                    SetLTEModeDialog(ctx as android.app.Activity)
                                }
                            },
                            DrawerItem("Calculadora UNE", Icons.Default.Calculate, Route.Une.route) {
                                DrawerAction.Navigate(Route.Une)
                            },
                        ),
                ),
                DrawerSectionData(
                    header = "Feedback",
                    items =
                        listOf(
                            DrawerItem("Errores", Icons.Default.BugReport, Route.LogFileViewer.route) {
                                DrawerAction.Navigate(Route.LogFileViewer)
                            },
                            DrawerItem("Enviar Feedback", Icons.Default.Email) {
                                DrawerAction.SendEmail(
                                    email = "portalusuarioapp@gmail.com",
                                    subject = "Feedback Portal Usuario App",
                                    body = feedbackBody,
                                )
                            },
                        ),
                ),
                DrawerSectionData(
                    header = "Redes Sociales",
                    items =
                        listOf(
                            DrawerItem("Canal Telegram", Icons.Default.Send) {
                                DrawerAction.ExternalUrl("https://t.me/portalusuario")
                            },
                            DrawerItem("Facebook", Icons.Default.Share) {
                                DrawerAction.ExternalUrl("https://www.facebook.com/portalusuario")
                            },
                            DrawerItem("Beta Testers", Icons.Default.Person) {
                                DrawerAction.ExternalUrl("https://t.me/portalusuarioBT")
                            },
                            DrawerItem("WhatsApp", Icons.Default.Send) {
                                DrawerAction.ExternalUrl("https://chat.whatsapp.com/HT6bKjpXHrN4FAyTAcy1Xn")
                            },
                            DrawerItem("Invitar", Icons.Default.Person) {
                                DrawerAction.ShareText(inviteText)
                            },
                        ),
                ),
                DrawerSectionData(
                    header = "Política",
                    items =
                        listOf(
                            DrawerItem("Política de Privacidad", Icons.Default.Lock, Route.Privacy.route) {
                                DrawerAction.Navigate(Route.Privacy)
                            },
                        ),
                ),
                DrawerSectionData(
                    header = "Ajustes",
                    items =
                        listOf(
                            DrawerItem("Configuración", Icons.Default.Settings, Route.Settings.route) {
                                DrawerAction.Navigate(Route.Settings)
                            },
                            DrawerItem("Acerca de", Icons.Default.Info, Route.About.route) {
                                DrawerAction.Navigate(Route.About)
                            },
                        ),
                ),
            )

        sections.forEachIndexed { index, section ->
            if (index > 0) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
            DrawerSection(
                header = section.header,
                items = section.items,
                currentRoute = currentRoute,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private data class DrawerSectionData(
    val header: String? = null,
    val items: List<DrawerItem>,
)

@Composable
private fun DrawerSection(
    header: String? = null,
    items: List<DrawerItem>,
    currentRoute: String?,
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
        val isSelected = item.route != null && item.route == currentRoute
        NavigationDrawerItem(
            label = { Text(item.label) },
            icon = item.icon?.let { icon -> { Icon(icon, contentDescription = null) } },
            selected = isSelected,
            onClick = { item.onClick() },
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

@Composable
private fun routeTitle(route: String?): String =
    when (route) {
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
