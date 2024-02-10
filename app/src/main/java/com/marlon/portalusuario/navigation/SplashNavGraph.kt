package com.marlon.portalusuario.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.marlon.portalusuario.presentation.onboarding.PermissionsScreen
import com.marlon.portalusuario.presentation.splash.SplashScreen

fun NavGraphBuilder.splashGraph(navController: NavHostController) {
    navigation(
        route = "splash_graph",
        startDestination = "splash_screen"
    ) {
        val navigateToMain = {
            navController.popBackStack()
            navController.navigate("main_graph")
        }
        val navigateToOnBoarding = {
            navController.popBackStack()
            navController.navigate("on_boarding_screen")
        }

        composable(route = "splash_screen") {
            SplashScreen(
                navigateToMain = navigateToMain,
                navigateToOnBoarding = navigateToOnBoarding
            )
        }

        composable(route = "on_boarding_screen") {
            PermissionsScreen(navigateToMainScreen = navigateToMain)
        }
    }
}
