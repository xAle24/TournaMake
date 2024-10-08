package com.example.tournaMake.activities.navgraph

import android.content.ContentResolver
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tournaMake.ui.screens.login.LoginScreen
import com.example.tournaMake.ui.screens.main.MainScreen
import com.example.tournaMake.ui.screens.match.MatchCreationScreen
import com.example.tournaMake.ui.screens.match.MatchDetailsScreen
import com.example.tournaMake.ui.screens.match.MatchListScreen
import com.example.tournaMake.ui.screens.match.MatchScreen
import com.example.tournaMake.ui.screens.menu.GameDetailsScreen
import com.example.tournaMake.ui.screens.menu.GamesListScreen
import com.example.tournaMake.ui.screens.menu.MenuScreen
import com.example.tournaMake.ui.screens.profile.ChartScreen
import com.example.tournaMake.ui.screens.profile.PlayerMatchesHistoryScreen
import com.example.tournaMake.ui.screens.profile.ProfileListScreen
import com.example.tournaMake.ui.screens.profile.ProfileScreen
import com.example.tournaMake.ui.screens.registration.RegistrationPhotoScreen
import com.example.tournaMake.ui.screens.registration.RegistrationScreen
import com.example.tournaMake.ui.screens.settings.SettingsScreen
import com.example.tournaMake.ui.screens.tournament.TournamentCreationScreen
import com.example.tournaMake.ui.screens.tournament.TournamentListScreen
import com.example.tournaMake.ui.screens.tournament.TournamentScreen

/**
 * To see an example of custom navigation, see class
 * [MatchScreen].
 * Suppose you navigate from A to B to C, then from C
 * you need to know where to go back based on the previous destination.
 * The code to do this is, provided that you're passing a NavController:
 *
 * val backStackEntry = navController.previousBackStackEntry
 * when (backStackEntry?.destination?.route) {
 *      NavigatioRoute.YourRoute.route -> ...
 * }
 * */
sealed class NavigationRoute(
    val route: String,
) {
    data object MainScreen : NavigationRoute("MainScreen")
    data object LoginScreen : NavigationRoute("LoginScreen")
    data object RegistrationScreen : NavigationRoute("RegistrationScreen")
    data object RegistrationPhotoScreen : NavigationRoute("RegistrationPhotoScreen")
    data object MenuScreen : NavigationRoute("MenuScreen")
    data object TournamentsListScreen : NavigationRoute("TournamentsListScreen")
    data object TournamentCreationScreen : NavigationRoute("TournamentCreationScreen")
    data object TournamentScreen : NavigationRoute("TournamentScreen")
    data object MatchesListScreen : NavigationRoute("MatchesListScreen")
    data object MatchDetailsScreen : NavigationRoute("MatchDetailsScreen")
    data object MatchCreationScreen : NavigationRoute("MatchCreationScreen")
    data object MatchScreen : NavigationRoute("MatchScreen")
    data object GamesListScreen : NavigationRoute("GamesListScreen")
    data object GameDetailsScreen : NavigationRoute("GameDetailsScreen")
    data object ProfilesListScreen : NavigationRoute("ProfilesListScreen")
    data object ProfileScreen : NavigationRoute("ProfileScreen")
    data object ChartScreen : NavigationRoute("ChartScreen")
    /* The following route shows the matches played by the player. */
    data object PlayerMatchesHistoryScreen : NavigationRoute("PlayerMatchesHistoryScreen")
    data object SettingsScreen : NavigationRoute("SettingsScreen")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier,
    owner: LifecycleOwner,
    contentResolver: ContentResolver,
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
            MatchScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.GamesListScreen.route) {
            GamesListScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.GameDetailsScreen.route) {
            GameDetailsScreen(navController = navController)
        }
        composable(NavigationRoute.ProfilesListScreen.route) {
            ProfileListScreen(owner = owner, navController = navController)
        }
        composable(NavigationRoute.ProfileScreen.route) {
            ProfileScreen(navController = navController, contentResolver = contentResolver, owner = owner)
        }
        composable(NavigationRoute.ChartScreen.route) {
            ChartScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.PlayerMatchesHistoryScreen.route) {
            PlayerMatchesHistoryScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.MatchDetailsScreen.route) {
            MatchDetailsScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.RegistrationPhotoScreen.route) {
            RegistrationPhotoScreen(navController = navController, owner = owner, contentResolver = contentResolver)
        }
        composable(NavigationRoute.TournamentScreen.route) {
            TournamentScreen(navController = navController, owner = owner)
        }
        composable(NavigationRoute.SettingsScreen.route) {
            SettingsScreen(navController = navController)
        }
    }
}