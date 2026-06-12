@file:Suppress("UNUSED_PARAMETER")

package com.marlon.portalusuario.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.marlon.portalusuario.paquetes.PaquetesScreen
import com.marlon.portalusuario.perfil.PerfilScreen
import com.marlon.portalusuario.presentation.about.AboutScreen
import com.marlon.portalusuario.presentation.callforreversecharge.CallForReverseChargeScreen
import com.marlon.portalusuario.presentation.donation.DonationScreen
import com.marlon.portalusuario.presentation.emergencycalls.EmergencyCallsScreen
import com.marlon.portalusuario.presentation.mobileservices.screen.MobileServicesScreen
import com.marlon.portalusuario.presentation.planamigos.PlanAmigosScreen
import com.marlon.portalusuario.presentation.privacy.PrivacyScreen
import com.marlon.portalusuario.presentation.privatecall.PrivateCallScreen
import com.marlon.portalusuario.presentation.settings.SettingsScreen
import com.marlon.portalusuario.presentation.sms.SmsScreen
import com.marlon.portalusuario.presentation.voz.VozScreen
import com.marlon.portalusuario.servicios.ServiciosScreen
import com.marlon.portalusuario.une.UneScreen

/**
 * NavHost centralizado con todas las rutas de la aplicación.
 * Cada ruta se mapea a su composable correspondiente.
 * Las rutas con argumentos tipados extraen los valores del [NavBackStackEntry].
 */
@Composable
fun PortalUsuarioNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Splash.route,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Route.Splash.route) { PlaceholderScreen("Splash") }
        composable(Route.Intro.route) { PlaceholderScreen("Intro") }
        composable(Route.Permissions.route) { PlaceholderScreen("Permissions") }
        composable(Route.Main.route) { MobileServicesScreen() }
        composable(Route.Settings.route) { SettingsScreen() }
        composable(Route.About.route) { AboutScreen() }
        composable(Route.Donation.route) { DonationScreen() }
        composable(Route.Privacy.route) { PrivacyScreen() }
        composable(Route.Sms.route) { SmsScreen() }
        composable(Route.Voz.route) { VozScreen() }
        composable(Route.PlanAmigos.route) { PlanAmigosScreen() }
        composable(Route.PrivateCall.route) {
            PrivateCallScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.CallForReverseCharge.route) {
            CallForReverseChargeScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.EmergencyCalls.route) { EmergencyCallsScreen() }
        composable(Route.Une.route) {
            UneScreen(onBack = { navController.popBackStack() })
        }
        composable(Route.Perfil.route) { PerfilScreen() }
        composable(Route.Servicios.route) {
            ServiciosScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Route.Paquetes.route) { PaquetesScreen() }
        composable(Route.LogFileViewer.route) { PlaceholderScreen("Log File Viewer") }
        composable(Route.MobileServices.route) { MobileServicesScreen() }
        composable(
            route = Route.PUNotifications.route,
            arguments =
                listOf(
                    navArgument("notificationId") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                ),
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: ""
            PlaceholderScreen("PU Notifications (id=$notificationId)")
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = name)
    }
}
