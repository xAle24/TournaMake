package com.example.tournaMake.tournamentmanager

import com.example.tournaMake.activities.TournamentManagerUpdateRequest
import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketMatchDisplayModel
import com.example.tournaMake.sampledata.TournamentMatchData
import java.util.UUID
import java.util.stream.Collectors
import kotlin.math.ceil
import kotlin.math.log2

class TournamentManager {
    /*
    * A map of TeamID -> Team's current Round
    * */
    private val map: MutableMap<String, Int> = mutableMapOf()
    private lateinit var bracket: BracketDisplayModel
    private var wasInitialised = false
    private var tournamentDataList = mutableListOf<TournamentMatchData>()
    private var numberOfRounds = 0

    /**
     * Order of function calls:
     * [setTournamentMatchData]
     * [initMap]
     * [setBracket]
     * */
    fun setTournamentMatchData(tournamentData: List<TournamentMatchData>) {
        this.tournamentDataList = tournamentData.toMutableList()
        this.numberOfRounds = ceil(log2(tournamentData.size.toDouble())).toInt()
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
    private fun mapTeamNameToIndex(teamName: String): Int {
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

    private fun getTeamRound(teamName: String): Int {
        return map[teamName]!!
    }

    private fun getMatchFromTeamNameAndRoundNumber(teamName: String, roundNumber: Int): BracketMatchDisplayModel {
        return this.bracket
            .rounds[roundNumber]
            .matches.first { match ->
                match.topTeam.name == teamName || match.bottomTeam.name == teamName
            }
    }

    fun updateMatch(data: TournamentManagerUpdateRequest) {
        val didFirstTeamWin = data.isFirstTeamWinner
        val didSecondTeamWin = data.isSecondTeamWinner
        val teamRound = this.getTeamRound(data.firstTeamName)
        val match = getMatchFromTeamNameAndRoundNumber(data.firstTeamName, teamRound)
        if (match.topTeam.name == data.firstTeamName) {
            match.topTeam.score = data.firstTeamScore.toString()
            match.topTeam.isWinner = data.isFirstTeamWinner
            match.bottomTeam.score = data.secondTeamScore.toString()
            match.bottomTeam.isWinner = data.isSecondTeamWinner
        } else {
            match.topTeam.score = data.secondTeamScore.toString()
            match.topTeam.isWinner = data.isSecondTeamWinner
            match.bottomTeam.score = data.firstTeamScore.toString()
            match.bottomTeam.isWinner = data.isFirstTeamWinner
        }
        if (didFirstTeamWin) {
            this.teamWon(data.firstTeamName, mapTeamNameToIndex(data.firstTeamName), data)
        } else if (didSecondTeamWin) {
            this.teamWon(data.secondTeamName, mapTeamNameToIndex(data.secondTeamName), data)
        }
    }

    private fun setTeamRound(teamName: String, roundNumber: Int) {
        map[teamName] = roundNumber
    }

    private fun teamWon(teamName: String, oldIndex: Int, data: TournamentManagerUpdateRequest) {
        val newTeamIndex = oldIndex / 2
        val matchIndex = newTeamIndex / 2
        val teamCurrentRound = map[teamName]!!
        if (teamCurrentRound + 1 < bracket.rounds.size) {
            this.setTeamRound(teamName, teamCurrentRound + 1)
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
            // Updating the other data structure
            if (matchToUpdate.topTeam.name != "---" && matchToUpdate.bottomTeam.name != "---") {
                val uuid = UUID.randomUUID().toString()
                this.tournamentDataList.add(
                    TournamentMatchData(
                        matchTmID = uuid,
                        gameID = "",
                        tournamentID = "",
                        teamID = "",
                        name = matchToUpdate.topTeam.name,
                        isWinner = 0,
                        score = 0
                    )
                )
                this.tournamentDataList.add(
                    TournamentMatchData(
                        matchTmID = uuid,
                        gameID = "",
                        tournamentID = "",
                        teamID = "",
                        name = matchToUpdate.bottomTeam.name,
                        isWinner = 0,
                        score = 0
                    )
                )
            }
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

    fun refreshBracket(): BracketDisplayModel {
        val newBracket = BracketDisplayModel(this.bracket.name, this.bracket.rounds)
        this.bracket = newBracket
        return newBracket
    }

    fun refreshTournamentDataList(): List<TournamentMatchData> {
        val newList = this.tournamentDataList.stream().collect(Collectors.toList())
        this.tournamentDataList = newList.toMutableList()
        return newList
    }
}