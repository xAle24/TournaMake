package com.example.tournaMake.tournamentmanager

import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.sampledata.TournamentMatchData

class TournamentManager {
    /*
    * A map of TeamID -> Team's current Round
    * */
    private val map: MutableMap<String, Int> = mutableMapOf()
    private lateinit var bracket: BracketDisplayModel
    private var wasInitialised = false
    private var tournamentDataList = emptyList<TournamentMatchData>()

    /**
     * Order of function calls:
     * [setTournamentMatchData]
     * [initMap]
     * [setBracket]
     * */
    fun setTournamentMatchData(tournamentData: List<TournamentMatchData>) {
        this.tournamentDataList = tournamentData
    }

    fun initMap() {
        this.tournamentDataList
            .map { elem -> elem.name }
            .forEach { teamName -> map[teamName] = 0 }
    }

    /**
     * Maps a team name to its index in the current round.
     * This means it returns the row index in which this team is.
     * Example:
     *      Round 0
     *  0
     *  1
     *  2
     *  3   Your team is here!
     *  4
     *  5
     *  6
     *  7
     *
     *  means that this function will return 3.
     * */
    fun mapTeamNameToIndex(teamName: String): Int {
        var index = 0
        bracket.rounds[this.map[teamName]!!].matches.forEach { match ->
            if (match.topTeam.name == teamName) {
                return index
            } else {
                index++
                if (match.bottomTeam.name == teamName) {
                    return index
                }
                index++
            }
        }
        return -1
    }

    fun getTeamRound(teamName: String): Int {
        return map[teamName]!!
    }

    fun setTeamRound(teamName: String, roundNumber: Int) {
        map[teamName] = roundNumber
    }

    fun teamWon(teamName: String, oldIndex: Int) {
        val newTeamIndex = oldIndex / 2
        val matchIndex = newTeamIndex / 2
        val teamCurrentRound = map[teamName]!!
        map[teamName] = teamCurrentRound + 1
        val matchToUpdate = bracket.rounds[teamCurrentRound + 1].matches[matchIndex]
        if (newTeamIndex % 2 == 0) {
            // if the index is even, it means the team has to be inserted in the top team
            matchToUpdate.topTeam.name = teamName
            matchToUpdate.topTeam.isWinner = false
            matchToUpdate.topTeam.score = "0"
        } else {
            matchToUpdate.bottomTeam.name = teamName
            matchToUpdate.bottomTeam.isWinner = false
            matchToUpdate.bottomTeam.score = "0"
        }
    }

    fun setBracket(bracket: BracketDisplayModel) {
        this.bracket = bracket
        wasInitialised = true
    }

    fun wasBracketInitialised(): Boolean {
        return this.wasInitialised
    }

    fun getBracket(): BracketDisplayModel {
        return this.bracket
    }

    fun getTournamentMatchData(): List<TournamentMatchData> {
        return this.tournamentDataList
    }
}