package com.example.tournaMake.activities.navgraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tournaMake.ui.screens.login.LoginScreen
import com.example.tournaMake.ui.screens.main.MainScreen
import com.example.tournaMake.ui.screens.match.MatchListScreen

sealed class NavigationRoute(
    val route: String
) {
    data object MainScreen : NavigationRoute("MainScreen")
    data object LoginScreen : NavigationRoute("LoginScreen")
    data object MenuScreen : NavigationRoute("MenuScreen")
    data object RegistrationScreen : NavigationRoute("RegistrationScreen")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier,
    owner: LifecycleOwner
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.MainScreen.route,
        modifier = modifier
    ) {
        composable(NavigationRoute.MainScreen.route) {
            MainScreen(navController)
        }
        composable(NavigationRoute.LoginScreen.route) {
            LoginScreen(navController, owner)
        }
        composable(NavigationRoute.RegistrationScreen.route) {

        }
    }
}