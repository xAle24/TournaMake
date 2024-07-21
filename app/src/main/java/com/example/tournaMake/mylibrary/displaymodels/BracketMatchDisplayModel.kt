package com.example.tournaMake.mylibrary.displaymodels

/**
 * Defines a matchup between a [topTeam] and a [bottomTeam] within a bracket.
 *
 * The [topTeam] is NOT necessarily the winner, but just the team that
 * will appear on the top of the [com.adammcneilly.tournament.bracket.internal.BracketMatchItem] UI.
 */
data class BracketMatchDisplayModel(
    var topTeam: BracketTeamDisplayModel,
    var bottomTeam: BracketTeamDisplayModel,
)
