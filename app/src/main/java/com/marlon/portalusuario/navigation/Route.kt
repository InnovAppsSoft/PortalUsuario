package com.marlon.portalusuario.navigation

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
