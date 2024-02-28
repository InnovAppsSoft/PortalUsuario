package cu.suitetecsa.cubacelmanager.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cu.suitetecsa.cubacelmanager.presentation.balance.BalanceRoute
import cu.suitetecsa.cubacelmanager.presentation.servicecall.ServicesCallRoute
import cu.suitetecsa.cubacelmanager.presentation.shop.ShopRoute

const val CubacelGraph = "cubacel_graph"

@Composable
internal fun CubacelNavGraph(
    navController: NavHostController,
    onSetTitle: (String) -> Unit,
    onSetActions: (@Composable (RowScope.() -> Unit)) -> Unit,
    bottomPadding: PaddingValues,
    topPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        route = CubacelGraph,
        startDestination = CubacelScreen.Balance.route
    ) {
        composable(route = CubacelScreen.Balance.route) {
            BalanceRoute(
                onSetTitle = onSetTitle,
                onSetActions = onSetActions,
                bottomPadding = bottomPadding,
                topPadding = topPadding
            )
        }
        composable(route = CubacelScreen.ServicesCall.route) {
            ServicesCallRoute(
                setTitle = onSetTitle,
                setActions = onSetActions,
                bottomPadding = bottomPadding,
                topPadding = topPadding
            )
        }
        composable(route = CubacelScreen.Shop.route) {
            ShopRoute(
                setTitle = onSetTitle,
                setActions = onSetActions,
                bottomPadding = bottomPadding,
                topPadding = topPadding
            )
        }
    }
}
