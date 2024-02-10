package com.marlon.portalusuario.presentation.main

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.marlon.portalusuario.R
import com.marlon.portalusuario.navigation.MainNavGraph
import cu.suitetecsa.core.ui.components.TopBar
import cu.suitetecsa.cubacelmanager.navigation.CubacelGraph

@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var actions: @Composable (RowScope.() -> Unit) by remember { mutableStateOf({}) }
    var title: String by remember { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                TextButton(onClick = { navController.navigate(CubacelGraph) }) {
                    Text(text = stringResource(id = R.string.balance_credit))
                }
                TextButton(onClick = { navController.navigate("nauta_route") }) {
                    Text(text = "Conectividad")
                }
            }
        }
    ) {
        Scaffold(
            topBar = { TopBar(title, scope, drawerState, actions) }
        ) { paddingValues ->
            MainNavGraph(
                navController = navController,
                paddingValues = paddingValues,
                setTitle = { title = it },
                setActions = { actions = it }
            )
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
