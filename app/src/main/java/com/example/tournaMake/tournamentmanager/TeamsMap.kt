package com.example.tournaMake.tournamentmanager

import com.example.tournaMake.sampledata.TournamentMatchData

class TeamsMap(
    tournamentMatchData: List<TournamentMatchData>
) {
    // Pairs of MatchID, pair of Team1MatchData and Team2MatchData
    private val teamsMap: Map<String, Pair<TournamentMatchData, TournamentMatchData>>

    init {
        teamsMap = tournamentMatchData
            .groupBy { it.matchTmID }
            .map { it -> it.key to Pair(it.value[0], it.value[1])}
            .toMap()
    }

    fun getTeamsInMatch(matchID: String): Pair<TournamentMatchData, TournamentMatchData>? {
        return teamsMap[matchID]
    }
}