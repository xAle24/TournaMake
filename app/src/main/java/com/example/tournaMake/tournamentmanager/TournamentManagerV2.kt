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
    private val bracket: BracketDisplayModel
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
                    matches = matchesInRound.map { dbMatch ->
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
                                ),/* The second team might be null if the first one is alone in a match
                                * in a higher round and nobody else has completed their match yet. */
                                bottomTeam = if (teams.second != null) BracketTeamDisplayModel(
                                    name = teams.second!!.name,
                                    isWinner = teams.second!!.isWinner == 1,
                                    score = teams.second!!.score.toString()
                                )
                                else createPlaceholderTeam(), tournamentData = tournamentMatchData
                            )
                        }
                    })
            )
        }
        return BracketDisplayModel(name = tournamentName, rounds = roundDisplayModels)
    }

    private fun createMatchWithPlaceholders(): BracketMatchDisplayModel {
        return BracketMatchDisplayModel(
            topTeam = createPlaceholderTeam(),
            bottomTeam = createPlaceholderTeam(),
            tournamentData = null
        )
    }

    private fun createPlaceholderTeam(): BracketTeamDisplayModel {
        return BracketTeamDisplayModel(
            name = "---", isWinner = false, score = "0"
        )
    }

    /**
     * This method should be called immediately after instantiating
     * the Tournament Manager. This class should not be directly
     * involved in database operations, for finer decoupling and
     * separation of concerns.
     * */
    fun shouldOtherMatchesBeCreated(): Boolean {
        // Take all the match data for winning teams
        val victories = tournamentDataList.filter { it.isWinner == 1 }/* If even one of the winning teams is not associated with a match in
        * a successive round, then at least one new match needs to be created. */
        return victories.any { tree.getMatchAtIndex(tree.getIndexOfMatchInNextRound(it.indexInTournamentTree)) == null }
    }

    /**
     * Returns a list of the matches created for the new round.
     * Should be followed by a call to [findDanglingTeams].
     * */
    fun generateNextRoundMatches(): List<MatchTM> {
        // First, find the round to create the matches in
        var nextRound: Int? = null
        for (i in 0 until tree.roundsNumber) {
            val indexesInRound = tree.getAllMatchIndexesFromRound(i)
            if (indexesInRound.all { tree.canSetMatchAtIndex(it) }) {
                nextRound = i
                break
            }
        }
        return if (nextRound != null) {
            tree.autoGenerateMatchesAtRound(
                r = nextRound,
                tournamentID = tournamentDataList.first().tournamentID,
                gameID = tournamentDataList.first().gameID
            )
        } else emptyList()
    }

    /**
     * In a Single Elimination Tournament, a team can continue
     * only if it always won.
     * */
    fun getDanglingTeams(): List<TournamentMatchData> {
        return this.matchesAndTeamsMap.getDanglingTeams { teamParticipations ->
            teamParticipations.all { it.isWinner == 1 }
        }
    }

    fun associateTeamsWithNextMatches(
        teams: List<TournamentMatchData>,
        newMatches: List<MatchTM>
    ): Map<TournamentMatchData, MatchTM> {
        assert(teams.map {
            matchesAndTeamsMap.getLatestMatchData(
                matchesAndTeamsMap.getTeamHistoryInTournament(it.teamID)
            )
        }.containsAll(teams))
        return teams.associateWith { team ->
            newMatches.first { matchTM ->
                matchTM.indexInTournamentTree == tree.getIndexOfMatchInNextRound(
                    team.indexInTournamentTree
                )
            }
        }
    }
}