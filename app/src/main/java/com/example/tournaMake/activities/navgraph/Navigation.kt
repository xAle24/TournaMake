package com.example.tournaMake.activities.navgraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tournaMake.ui.screens.login.LoginScreen
import com.example.tournaMake.ui.screens.main.MainScreen
import com.example.tournaMake.ui.screens.match.MatchListScreen
import com.example.tournaMake.ui.screens.menu.MenuScreen
import com.example.tournaMake.ui.screens.registration.RegistrationScreen
import com.example.tournaMake.ui.screens.tournament.TournamentCreationScreen
import com.example.tournaMake.ui.screens.tournament.TournamentListScreen

sealed class NavigationRoute(
    val route: String
) {
    data object MainScreen : NavigationRoute("MainScreen")
    data object LoginScreen : NavigationRoute("LoginScreen")
    data object RegistrationScreen : NavigationRoute("RegistrationScreen")
    data object MenuScreen : NavigationRoute("MenuScreen")
    data object TournamentsListScreen : NavigationRoute("TournamentsListScreen")
    data object TournamentCreationScreen : NavigationRoute("TournamentCreationScreen")
    data object TournamentScreen : NavigationRoute("TournamentScreen")
    data object MatchesListScreen : NavigationRoute("MatchesListScreen")
    data object MatchDetailsScreen : NavigationRoute("MatchDetailsScreen")
    data object MatchCreationScreen : NavigationRoute("MatchCreationScreen")
    data object MatchScreen : NavigationRoute("MatchScreen")
    data object GamesListScreen : NavigationRoute("GamesListScreen")
    data object ProfilesListScreen : NavigationRoute("ProfilesListScreen")
    data object SettingsScreen : NavigationRoute("SettingsScreen")
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
            RegistrationScreen(navController, owner)
        }
        composable(NavigationRoute.MenuScreen.route) {
            MenuScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.TournamentsListScreen.route) {
            TournamentListScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.TournamentCreationScreen.route) {
            TournamentCreationScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.MatchesListScreen.route) {
            MatchListScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.MatchesListScreen.route) {

        }
        composable(NavigationRoute.MatchesListScreen.route) {

        }
        composable(NavigationRoute.MatchesListScreen.route) {

        }
        composable(NavigationRoute.GamesListScreen.route) {
            /*TODO*/
        }
        composable(NavigationRoute.MatchesListScreen.route) {

        }
        composable(NavigationRoute.MatchesListScreen.route) {

        }
    }
}