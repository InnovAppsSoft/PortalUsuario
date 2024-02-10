package cu.suitetecsa.cubacelmanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.cubacelmanager.navigation.CubacelNavGraph
import cu.suitetecsa.cubacelmanager.navigation.CubacelScreen
import cu.suitetecsa.cubacelmanager.navigation.CubacelScreen.Balance
import cu.suitetecsa.cubacelmanager.navigation.CubacelScreen.ServicesCall
import cu.suitetecsa.cubacelmanager.navigation.CubacelScreen.Shop

@Composable
fun CubacelRoute(
    navController: NavHostController = rememberNavController(),
    paddingValues: PaddingValues,
    onSetActions: (@Composable (RowScope.() -> Unit)) -> Unit = { },
    onSetTitle: (String) -> Unit
) {
    CubacelScreen(
        navController = navController,
        paddingValues = paddingValues,
        setTitle = onSetTitle,
        setActions = onSetActions
    )
}

@Composable
internal fun CubacelScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    setTitle: (String) -> Unit,
    setActions: (@Composable (RowScope.() -> Unit)) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) {
        CubacelNavGraph(
            navController = navController,
            onSetTitle = setTitle,
            onSetActions = setActions,
            bottomPadding = it,
            topPadding = paddingValues
        )
    }
}

@Composable
internal fun BottomBar(
    navController: NavHostController
) {
    val screens = listOf(
        Balance,
        ServicesCall,
        Shop
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBardDestination = screens.any { it.route == currentDestination?.route }

    if (bottomBardDestination) {
        NavigationBar(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.small)
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
internal fun RowScope.AddItem(
    screen: CubacelScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        alwaysShowLabel = false,
        icon = {
            Icon(imageVector = screen.icon, contentDescription = "Icono de navegacion")
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CubacelManagerScreenPreview() {
    SuitEtecsaTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CubacelScreen(
                navController = rememberNavController(),
                paddingValues = PaddingValues(),
                setTitle = {},
                setActions = {}
            )
        }
    }
}
