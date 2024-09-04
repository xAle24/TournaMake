package com.example.tournaMake.mylibrary.displaymodels

import com.example.tournaMake.sampledata.TournamentMatchData

/**
 * Defines a matchup between a [topTeam] and a [bottomTeam] within a bracket.
 *
 * The [topTeam] is NOT necessarily the winner, but just the team that
 * will appear on the top of the [com.adammcneilly.tournament.bracket.internal.BracketMatchItem] UI.
 */
data class BracketMatchDisplayModel(
    var topTeam: BracketTeamDisplayModel,
    var bottomTeam: BracketTeamDisplayModel,
    val tournamentData: TournamentMatchData? // Alin's addition; used to know how to navigate to the match screen when clicked
)
