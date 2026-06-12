package com.marlon.portalusuario.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Route(val route: String) {
    data object Splash : Route("splash")
    data object Intro : Route("intro")
    data object Permissions : Route("permissions")
    data object Main : Route("main")
    data object Settings : Route("settings")
    data object About : Route("about")
    data object Donation : Route("donation")
    data object Privacy : Route("privacy")
    data object Sms : Route("sms")
    data object Voz : Route("voz")
    data object PlanAmigos : Route("plan_amigos")
    data object PrivateCall : Route("private_call")
    data object CallForReverseCharge : Route("call_for_reverse_charge")
    data object EmergencyCalls : Route("emergency_calls")
    data object Une : Route("une")
    data object Perfil : Route("perfil")
    data object LogFileViewer : Route("log_file_viewer")
    data object PUNotifications : Route("pu_notifications")
}

@Composable
fun PortalUsuarioNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Splash.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Route.Splash.route) { PlaceholderScreen("Splash") }
        composable(Route.Intro.route) { PlaceholderScreen("Intro") }
        composable(Route.Permissions.route) { PlaceholderScreen("Permissions") }
        composable(Route.Main.route) { PlaceholderScreen("Main") }
        composable(Route.Settings.route) { PlaceholderScreen("Settings") }
        composable(Route.About.route) { PlaceholderScreen("About") }
        composable(Route.Donation.route) { PlaceholderScreen("Donation") }
        composable(Route.Privacy.route) { PlaceholderScreen("Privacy") }
        composable(Route.Sms.route) { PlaceholderScreen("Sms") }
        composable(Route.Voz.route) { PlaceholderScreen("Voz") }
        composable(Route.PlanAmigos.route) { PlaceholderScreen("Plan Amigos") }
        composable(Route.PrivateCall.route) { PlaceholderScreen("Private Call") }
        composable(Route.CallForReverseCharge.route) { PlaceholderScreen("Call for Reverse Charge") }
        composable(Route.EmergencyCalls.route) { PlaceholderScreen("Emergency Calls") }
        composable(Route.Une.route) { PlaceholderScreen("UNE") }
        composable(Route.Perfil.route) { PlaceholderScreen("Perfil") }
        composable(Route.LogFileViewer.route) { PlaceholderScreen("Log File Viewer") }
        composable(Route.PUNotifications.route) { PlaceholderScreen("PU Notifications") }
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
