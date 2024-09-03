package com.example.tournaMake.tournamentmanager

import com.example.tournaMake.sampledata.MatchTM
import okhttp3.internal.toImmutableList
import kotlin.math.log2
import kotlin.math.pow

/**
 * A data structure representing a perfectly balanced binary tree,
 * that takes as input the number of teams playing. WARNING: the number
 * of teams must be a power of 2!
 * */
class TournamentTree(private val numberOfTeams: Int) {
    val roundsNumber: Int // n, or tree height
    val leavesNumber: Int
    val totalMatches: Int // m, or matches number
    val matchesList: List<MatchTM?>
    // Kotlin's syntax for a "constructor"
    init {
        if (!isPowerOf2(numberOfTeams)) {
            throw IllegalArgumentException("Cannot create a TournamentTree with $numberOfTeams teams")
        }
        /* The rounds number is equal to the height of the tree. 8 teams will play for 3 rounds,
        * and each round will contain matches of pairs of teams competing against each other. */
        roundsNumber = log2(numberOfTeams.toDouble()).toInt()
        /* The number of qualification matches */
        leavesNumber = 2.0.pow(roundsNumber.toDouble() - 1.0).toInt()
        /* The total matches that will be played in this Tree. It represents the length of the array. */
        totalMatches = 2.0.pow(roundsNumber).toInt() - 1
        val nullMatchesList: MutableList<MatchTM?> = mutableListOf()
        for (i in 0 until totalMatches) {
            nullMatchesList.add(null)
        }
        matchesList = nullMatchesList.toImmutableList()
    }

    fun isIndexInArrayBounds(index: Int): Boolean {
        return index in 0 until totalMatches
    }

    fun isRoundInBounds(roundNumber: Int): Boolean {
        return roundNumber in 0 until roundsNumber
    }

    fun getIndexOfMatchInNextRound(currentMatchIndex: Int): Int {
        assert(isIndexInArrayBounds(currentMatchIndex))
        return (currentMatchIndex - 1) / 2
    }

    /**
     * [r] is the round number.
     * */
    fun getIndexOfFirstMatchInRound(r: Int): Int {
        assert(isRoundInBounds(r))
        var rest: Int = 0
        for (i in (roundsNumber - 1 - r)until(roundsNumber - 1)) {
            rest += 2.0.pow(i).toInt()
        }
        return totalMatches - rest
    }

    fun getIndexOfLastMatchInRound(r: Int): Int {
        assert(isRoundInBounds(r))
        return getIndexOfFirstMatchInRound(r) + 2.0.pow(roundsNumber - 1 - r).toInt() - 1
    }

    fun getAllMatchIndexesFromRound(r: Int): List<Int> {
        assert(isRoundInBounds(r))
        val list = mutableListOf<Int>()
        for (i in getIndexOfFirstMatchInRound(r)..getIndexOfLastMatchInRound(r)) {
            list.add(i)
        }
        return list.toImmutableList()
    }

    fun getMatchAtIndex(index: Int): MatchTM? {
        assert(isIndexInArrayBounds(index))
        return matchesList[index]
    }

    fun getMatchesAtIndexes(indexes: List<Int>): List<MatchTM?> {
        indexes.forEach { assert(isIndexInArrayBounds(it)) }
        return matchesList.filter { indexes.contains(matchesList.indexOf(it)) }
    }
}