package com.marlon.portalusuario.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cu.suitetecsa.cubacelmanager.navigation.CubacelGraph
import cu.suitetecsa.cubacelmanager.presentation.CubacelRoute

@Composable
fun MainNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onSetTitle: (String) -> Unit,
    onSetActions: (@Composable (RowScope.() -> Unit)) -> Unit
) {
    NavHost(
        navController = navController,
        route = "main_graph",
        startDestination = CubacelGraph
    ) {
        composable(route = CubacelGraph) {
            CubacelRoute(
                paddingValues = paddingValues,
                onSetTitle = onSetTitle,
                onSetActions = onSetActions
            )
        }
    }
}
