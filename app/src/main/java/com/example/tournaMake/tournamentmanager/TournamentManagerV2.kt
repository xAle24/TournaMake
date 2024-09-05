package com.example.tournaMake.tournamentmanager

import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketMatchDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketRoundDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketTeamDisplayModel
import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.TournamentMatchData

class TournamentManagerV2(
    private val tournamentDataList: List<TournamentMatchData>,
    private val dbMatches: List<MatchTM>,
    private val tournamentName: String // needed to produce the BracketDisplayModel
) {
    val bracket: BracketDisplayModel
    private val matchesAndTeamsMap: TeamsMap = TeamsMap(tournamentDataList)
    private val tree: TournamentTree = TournamentTree(tournamentDataList, dbMatches)

    init {
        bracket = produceBracket()
    }

    fun produceBracket(): BracketDisplayModel {
        val roundDisplayModels = mutableListOf<BracketRoundDisplayModel>()
        for (i in 0 until tree.roundsNumber) {
            val matchesInRound = tree.getMatchesAtIndexes(tree.getAllMatchIndexesFromRound(i))
            roundDisplayModels.add(
                BracketRoundDisplayModel(
                    name = "Round $i",
                    matches = matchesInRound
                        .map { dbMatch ->
                            /* In case the match is null, the view should display
                            * a placeholder; otherwise, it should show a BracketMatchDisplayModel.
                            * */
                            if (dbMatch == null) {
                                return@map createMatchWithPlaceholders()
                            } else {
                                val teams = matchesAndTeamsMap.getTeamsInMatch(dbMatch.matchTmID)!!
                                val tournamentMatchData =
                                    tournamentDataList.first { it.matchTmID == dbMatch.matchTmID }
                                return@map BracketMatchDisplayModel(
                                    topTeam = BracketTeamDisplayModel(
                                        name = teams.first.name,
                                        isWinner = teams.first.isWinner == 1,
                                        score = teams.first.score.toString()
                                    ),
                                    bottomTeam = BracketTeamDisplayModel(
                                        name = teams.second.name,
                                        isWinner = teams.second.isWinner == 1,
                                        score = teams.second.score.toString()
                                    ),
                                    tournamentData = tournamentMatchData
                                )
                            }
                        }
                )
            )
        }
        return BracketDisplayModel(name = tournamentName, rounds = roundDisplayModels)
    }

    private fun createMatchWithPlaceholders(): BracketMatchDisplayModel {
        return BracketMatchDisplayModel(
            topTeam = BracketTeamDisplayModel(
                name = "---",
                isWinner = false,
                score = "0"
            ),
            bottomTeam = BracketTeamDisplayModel(
                name = "---",
                isWinner = false,
                score = "0"
            ),
            tournamentData = null
        )
    }
}