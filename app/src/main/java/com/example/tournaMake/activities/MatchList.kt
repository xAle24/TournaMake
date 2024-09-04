package com.example.tournaMake.activities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.tournaMake.activities.navgraph.NavigationRoute
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.sampledata.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent

fun fetchAndUpdateMatches(vm: MatchListViewModel, owner: LifecycleOwner) {
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            val matches = db.value.matchDao().getAllWithGamesNames()
            vm.changeMatchesList(matches)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun addMatchToFavorites(matchTmID: String, owner: LifecycleOwner) {
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            db.value.matchDao().setMatchFavorites(matchTmID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun removeMatchFromFavorites(matchTmID: String, owner: LifecycleOwner) {
    val db = KoinJavaComponent.inject<AppDatabase>(AppDatabase::class.java)
    owner.lifecycleScope.launch(Dispatchers.IO) {
        try {
            db.value.matchDao().removeMatchFavorites(matchTmID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun navigateToSpecifiedMatch(
    matchTmID: String,
    isOver: Boolean,
    vm: MatchListViewModel,
    owner: LifecycleOwner,
    navController: NavController
) {
    owner.lifecycleScope.launch(Dispatchers.IO) {
        // Change selected match in repository
        vm.changeRepository(matchTmID)
        if (isOver) {
            withContext(Dispatchers.Main) {
                navController.navigate(NavigationRoute.MatchDetailsScreen.route)
            }
        } else {
            withContext(Dispatchers.Main) {
                navController.navigate(NavigationRoute.MatchScreen.route)
            }
        }
    }
}
