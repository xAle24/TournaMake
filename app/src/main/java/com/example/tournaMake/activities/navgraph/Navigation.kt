package com.example.tournaMake.activities.navgraph

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tournaMake.ui.screens.login.LoginScreen
import com.example.tournaMake.ui.screens.main.MainScreen
import com.example.tournaMake.ui.screens.match.MatchCreationScreen
import com.example.tournaMake.ui.screens.match.MatchListScreen
import com.example.tournaMake.ui.screens.match.MatchScreen
import com.example.tournaMake.ui.screens.menu.GamesListScreen
import com.example.tournaMake.ui.screens.menu.MenuScreen
import com.example.tournaMake.ui.screens.profile.ProfileListScreen
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
    data object ProfileScreen : NavigationRoute("ProfileScreen")
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
            MenuScreen(navController, owner)
        }
        composable(NavigationRoute.TournamentsListScreen.route) {
            TournamentListScreen(navController, owner)
        }
        composable(NavigationRoute.TournamentCreationScreen.route) {
            TournamentCreationScreen(navController, owner)
        }
        composable(NavigationRoute.MatchesListScreen.route) {
            MatchListScreen(navController, owner)
        }
        composable(NavigationRoute.MatchCreationScreen.route) {
            MatchCreationScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.MatchScreen.route) {
            val previousStackEntry = navController.previousBackStackEntry
            var callerRoute = previousStackEntry?.destination?.route

            /**
             * Navigation, based on the callerRoute:
             * - if it's the creation screen, go back to matches list
             * - if it's the matches list, go back to it
             * - if it's the tournament, go back to the tournament
             * */
            if (callerRoute == null) {
                Log.e("DEV-NAVGRAPH", "CallerRoute is null in Navigation.kt: " +
                        "somebody is trying to instantiate a MatchScreen without specifying proper arguments!")
            } else if (callerRoute == NavigationRoute.MatchCreationScreen.route || callerRoute == NavigationRoute.MatchesListScreen.route) {
                callerRoute = NavigationRoute.MatchesListScreen.route
            } // TODO: ADD NAVIGATION TO TOURNAMENT
            MatchScreen(callerRoute = callerRoute!!, navController = navController, owner = owner)
        }
        composable(NavigationRoute.GamesListScreen.route) {
            GamesListScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.ProfilesListScreen.route) {
            ProfileListScreen(owner = owner, navController = navController)
        }
        composable(NavigationRoute.ProfileScreen.route) {

        }
    }
}