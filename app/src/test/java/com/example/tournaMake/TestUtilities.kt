package com.example.tournaMake

import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.TournamentMatchData
import okhttp3.internal.toImmutableList
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import kotlin.math.log2
import kotlin.math.pow

fun createSampleMatch(
    withIndex: Int = 0,
    isOver: Int = 0,
): MatchTM {
    return MatchTM(
        matchTmID = UUID.randomUUID().toString(),
        favorites = 0,
        date = System.currentTimeMillis(),
        duration = 0,
        isOver = isOver,
        gameID = "sampleGame",
        tournamentID = "sampleTournament",
        indexInTournamentTree = withIndex
    )
}

/**
 * [leavesMatchIDs] is the list of match ids in round 0.
 * The index of each match is calculated as:
 * - first index in round = sum for i = roundsNumber - 1 - r
 * to i = roundsNumber - 1 of 2^i (but r equals 0, so
 * first index in round 0 is matchesNumber - 2^(roundsNumber - 1),
 * which for 7 matches with 3 rounds would be 7 - 2^2 = 3)
 * - number of indexes: 2^(roundsNumber - 1 - r)
 * */
fun createSampleTournamentMatchData(leavesMatchIDs: List<String>): List<TournamentMatchData> {
    val mutableList = mutableListOf<TournamentMatchData>()
    val teamsNumber = leavesMatchIDs.size * 2
    val roundsNumber = log2(teamsNumber.toDouble()).toInt()
    val matchesNumber = (2.0.pow(roundsNumber) - 1).toInt()
    val subtrahend = 2.0.pow(roundsNumber - 1).toInt()
    val firstIndexInRound0 = matchesNumber - subtrahend

    var indexToPlace = firstIndexInRound0
    for (i in leavesMatchIDs.indices) {
        mutableList.add(
            TournamentMatchData(
                matchTmID = leavesMatchIDs[i],
                indexInTournamentTree = indexToPlace,
                isOver = 0,
                gameID = "sampleGame",
                tournamentID = "sampleTournament",
                teamID = UUID.randomUUID().toString(),
                name = "Team${i}1",
                isWinner = 0,
                score = 0
            )
        )
        mutableList.add(
            TournamentMatchData(
                matchTmID = leavesMatchIDs[i],
                indexInTournamentTree = indexToPlace,
                isOver = 0,
                gameID = "sampleGame",
                tournamentID = "sampleTournament",
                teamID = UUID.randomUUID().toString(),
                name = "Team${i}2",
                isWinner = 0,
                score = 0
            )
        )
        indexToPlace++
    }
    return mutableList.toImmutableList()
}

fun createTournamentMatchData(
    matchID: String,
    indexInTree: Int,
    teamID: String = UUID.randomUUID().toString(),
    isOver: Int = 0,
    isWinner: Int = 0
): TournamentMatchData {
    return TournamentMatchData(
        matchTmID = matchID,
        indexInTournamentTree = indexInTree,
        isOver = isOver,
        gameID = "sampleGame",
        tournamentID = "sampleTournament",
        teamID = teamID,
        name = "Team",
        isWinner = isWinner,
        score = 0
    )
}

fun canContinueSingle(teamHistory: List<TournamentMatchData>): Boolean {
    return teamHistory.all { it.isWinner == 1 }
}

fun canContinueDouble(teamHistory: List<TournamentMatchData>): Boolean {
    return teamHistory.filter { it.isWinner == 0 }.size <= 1
}

/**
 * For the results to make sense, the matchIDs
 * should be sorted by index in tree (descending).
 * Returns null if wins + losses != matches.size
 * */
fun createTeamHistory(
    teamID: String,
    matches: List<MatchTM>,
    wins: Int,
    losses: Int
): List<TournamentMatchData>? {
    if (wins + losses != matches.size) {
        return null
    }
    val history = mutableListOf<TournamentMatchData>()
    var winsToCreate = wins
    var lossesToCreate = losses
    val matchesQueue: Queue<MatchTM> = LinkedList()
    matches.forEach { matchesQueue.add(it) }
    while (winsToCreate-- > 0) {
        val match = matchesQueue.poll()!!
        history.add(createTournamentMatchData(
            matchID = match.matchTmID,
            indexInTree = match.indexInTournamentTree!!,
            teamID = teamID,
            isOver = 1,
            isWinner = 1
        ))
    }
    while (lossesToCreate-- > 0) {
        val match = matchesQueue.poll()!!
        history.add(createTournamentMatchData(
            matchID = match.matchTmID,
            indexInTree = match.indexInTournamentTree!!,
            teamID = teamID,
            isOver = 1,
            isWinner = 0
        ))
    }
    return history
}