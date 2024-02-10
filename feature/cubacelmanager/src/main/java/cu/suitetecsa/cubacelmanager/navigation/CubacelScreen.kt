package cu.suitetecsa.cubacelmanager.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.ui.graphics.vector.ImageVector

internal sealed class CubacelScreen(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    data object Balance : CubacelScreen(
        title = "Balance",
        icon = Icons.Outlined.AccountBalance,
        route = "balance_route"
    )
    data object ServicesCall : CubacelScreen(
        title = "Servicios",
        icon = Icons.Outlined.Call,
        route = "services_call_route"
    )
    data object Shop : CubacelScreen(
        title = "Comprar",
        icon = Icons.Outlined.Shop,
        route = "shop_route"
    )
}
