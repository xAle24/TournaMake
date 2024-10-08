package com.example.tournaMake.mylibrary.internal

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.tournaMake.activities.navigateToSpecifiedMatch
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.mylibrary.displaymodels.BracketMatchDisplayModel
import com.example.tournaMake.mylibrary.ui.BracketColors
import org.koin.androidx.compose.koinViewModel

/**
 * Defines an item that is used to represent a [match] inside of a bracket.
 * This just renders two teams in a vertical column, highlighting the winner in bold.
 *
 * A future improvement of this library would be to provide additional customization about how these
 * match items appear.
 */
@Composable
internal fun BracketMatchItem(
    match: BracketMatchDisplayModel,
    modifier: Modifier = Modifier,
    colors: BracketColors.MatchColors,
    navController: NavController
) {
    val vm = koinViewModel<MatchListViewModel>()
    val owner = LocalLifecycleOwner.current
    Box(
        modifier = modifier
            .clickable {
                if (match.tournamentData != null) {
                    navigateToSpecifiedMatch(
                        matchTmID = match.tournamentData.matchTmID,
                        isOver = match.tournamentData.isOver == 1,
                        vm = vm,
                        owner = owner,
                        navController = navController
                    )
                }
            }, // Code added by Alin
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = colors.borderColor,
                )
                .align(Alignment.Center),
        ) {
            TeamRow(
                team = match.topTeam,
                colors = colors,
            )

            Divider(
                color = colors.dividerColor,
            )

            TeamRow(
                team = match.bottomTeam,
                colors = colors,
            )
        }
    }
}
