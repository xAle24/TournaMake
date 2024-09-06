package com.example.tournaMake.tournamentmanager

import com.example.tournaMake.sampledata.TournamentMatchData

class TeamsMap(
    tournamentMatchData: List<TournamentMatchData>
) {
    // Pairs of MatchID, pair of Team1MatchData and Team2MatchData
    // WARNING: if a team is in a match in a higher round, it's not
    // guaranteed that there will also be a team in the second element
    // of the pair!
    private val teamsMap: Map<String, Pair<TournamentMatchData, TournamentMatchData?>>

    init {
        teamsMap = tournamentMatchData
            .groupBy { it.matchTmID }
            .map { it -> it.key to Pair(it.value[0], if (it.value.size == 2) it.value[1] else null)}
            .toMap()
    }

    fun getTeamsInMatch(matchID: String): Pair<TournamentMatchData, TournamentMatchData?>? {
        return teamsMap[matchID]
    }

    /**
     * Dangling teams are those that are ready to continue, but are not
     * associated to any match yet (i.e., there's no Team_in_tm database entry
     * that associates them to the next match in the tournament).
     * This method takes all the team data in the map, that basically represent
     * the teams' histories, and filters all the teams that are able to continue
     * based on the [canContinue] lambda parameter.
     * In a Single Elimination Tournament, a team can continue only if it scored
     * only wins.
     * In a Double Elimination Tournament, a team can continue only if it scored
     * at most one loss.
     * */
    fun getDanglingTeams(canContinue: (List<TournamentMatchData>) -> Boolean): List<TournamentMatchData> {
        return this.teamsMap.values
            .flatMap { pair -> listOf(pair.first, pair.second) }
            .filterNotNull()
            .groupBy { it.teamID }
            .filter { canContinue(it.value) }
            .map { getLatestMatchData(it.value) }
    }

    /**
     * TODO: TEST
     * This should return the latest match played by this team, so the
     * one with the lowest tree index possible.
     * */
    fun getLatestMatchData(teamHistory: List<TournamentMatchData>): TournamentMatchData {
        return teamHistory.minByOrNull { it.indexInTournamentTree }!!
    }

    fun getTeamHistoryInTournament(teamID: String): List<TournamentMatchData> {
        return this.teamsMap.values
            .flatMap { pair -> listOf(pair.first, pair.second) }
            .filterNotNull()
            .filter { it.teamID == teamID }
    }
}