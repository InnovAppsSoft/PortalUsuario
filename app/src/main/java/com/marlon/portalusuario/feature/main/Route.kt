package com.marlon.portalusuario.feature.main

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Definición centralizada de todas las rutas de navegación de la aplicación.
 *
 * Cada ruta define su patrón de URL y los argumentos tipados que espera.
 * Usar [createRoute] para navegar a rutas con argumentos opcionales.
 */
sealed class Route(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList(),
) {
    /** Pantalla de splash / carga inicial */
    data object Splash : Route("splash")

    /** Onboarding / tutorial inicial */
    data object Intro : Route("intro")

    /** Solicitud de permisos esenciales */
    data object Permissions : Route("permissions")

    /** Pantalla principal con drawer de navegación */
    data object Main : Route("main")

    /** Configuración de la aplicación */
    data object Settings : Route("settings")

    /** Información de la aplicación */
    data object About : Route("about")

    /** Donaciones / apoyo */
    data object Donation : Route("donation")

    /** Política de privacidad */
    data object Privacy : Route("privacy")

    /** Servicio de mensajería SMS */
    data object Sms : Route("sms")

    /** Servicio de llamadas de voz */
    data object Voz : Route("voz")

    /** Plan Amigos */
    data object PlanAmigos : Route("plan_amigos")

    /** Llamada privada */
    data object PrivateCall : Route("private_call")

    /** Llamada por cobrar */
    data object CallForReverseCharge : Route("call_for_reverse_charge")

    /** Llamadas de emergencia */
    data object EmergencyCalls : Route("emergency_calls")

    /** Consumo eléctrico (UNE) */
    data object Une : Route("une")

    /** Perfil del usuario */
    data object Perfil : Route("perfil")

    /** Servicios móviles (saldo, bonos, planes) */
    data object MobileServices : Route("mobile_services")

    /** Planes y paquetes de datos */
    data object Paquetes : Route("paquetes")

    /** Servicios de red (saldo, llamadas, recargas) */
    data object Servicios : Route("servicios")

    /** Visor de logs de depuración */
    data object LogFileViewer : Route("log_file_viewer")

    /** Notificaciones de Portal Usuario */
    data object PUNotifications : Route(
        route = "pu_notifications?notificationId={notificationId}",
        arguments =
            listOf(
                navArgument("notificationId") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
    ) {
        /** Crea una ruta a notificaciones, opcionalmente con un ID específico. */
        fun createRoute(notificationId: String? = null): String =
            if (notificationId != null) {
                "pu_notifications?notificationId=$notificationId"
            } else {
                "pu_notifications"
            }
    }
}
