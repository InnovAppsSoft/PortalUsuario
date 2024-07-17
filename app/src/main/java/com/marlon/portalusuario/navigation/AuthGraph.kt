package com.marlon.portalusuario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.marlon.portalusuario.presentation.auth.screen.AuthScreen
import com.marlon.portalusuario.presentation.resetpassword.screen.ResetPasswordScreen
import com.marlon.portalusuario.presentation.signup.screen.SignupScreen

@Composable
fun AuthGraph(navController: NavHostController) {
    NavHost(navController = navController, route = "auth_graph", startDestination = "auth") {
        composable(route = "auth") { AuthScreen(navController = navController) }
        composable(route = "reset_password") { ResetPasswordScreen(navController = navController) }
        composable(route = "signup") { SignupScreen(navController = navController) }
    }
}
