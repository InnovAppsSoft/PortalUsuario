package com.marlon.portalusuario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.marlon.portalusuario.presentation.main.MainScreen

@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        route = "root_graph",
        startDestination = "splash_graph"
    ) {
        splashGraph(navController)
        composable(route = "main_graph") {
            MainScreen()
        }
    }
}
