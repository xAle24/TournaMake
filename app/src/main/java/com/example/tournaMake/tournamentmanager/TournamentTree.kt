package com.example.tournaMake.tournamentmanager

import com.example.tournaMake.sampledata.MatchTM
import okhttp3.internal.toImmutableList
import java.util.UUID
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
    var matchesList: List<MatchTM?>
    private set
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
        for (i in (roundsNumber - 1 - r) until (roundsNumber)) {
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

    /**
     * Insertion of null matches is not allowed.
     * Overwriting existing matches is not allowed.
     * */
    fun setMatchAtIndex(index: Int, match: MatchTM) {
        assert(isIndexInArrayBounds(index))
        assert(matchesList[index] == null) // Don't allow insertion where there's an existing match
        val newList = mutableListOf<MatchTM?>()
        matchesList.forEach { elem -> newList.add(elem) }
        newList[index] = match
        matchesList = newList
    }

    /**
     * New lists are allowed, as long as they contain all the matches already created in this
     * tournament tree. */
    fun setMatches(matchList: List<MatchTM?>) {
        assert(matchList.size == totalMatches)
        val nonNullMatches = matchesList.filterNotNull()
        /* The old matches must be in the new list and they must occupy
        * the same index as in the old list. */
        nonNullMatches.forEach {
            assert(matchList.contains(it) && matchList.indexOf(it) == matchesList.indexOf(it))
        }
        matchesList = matchList
    }

    fun canSetMatches(matchList: List<MatchTM?>): Boolean {
        val nonNullMatches = matchesList.filterNotNull()
        return matchList.size == totalMatches &&
                nonNullMatches.all { matchList.contains(it) &&
                    matchList.indexOf(it) == matchesList.indexOf(it)
                }
    }

    fun canSetMatchAtIndex(index: Int): Boolean {
        return isIndexInArrayBounds(index) && matchesList[index] == null
    }

    /**
     * If the round exists and there are no matches in it, it automatically
     * generates all the matches for the given round.
     * @return the list of generated matches.
     * */
    fun autoGenerateMatchesAtRound(
        r: Int,
        tournamentID: String,
        gameID: String,
        duration: Int = 0
    ): List<MatchTM> {
        assert(isRoundInBounds(r))
        val roundIndexes = getAllMatchIndexesFromRound(r)
        roundIndexes.forEach { assert(canSetMatchAtIndex(it)) }
        val newMatchesList = mutableListOf<MatchTM>()
        roundIndexes.forEach {
            val matchTM = MatchTM(
                matchTmID = UUID.randomUUID().toString(),
                favorites = 0,
                date = System.currentTimeMillis(),
                duration = duration,
                isOver = 0,
                gameID = gameID,
                tournamentID = tournamentID
            )
            setMatchAtIndex(it, matchTM)
            newMatchesList.add(matchTM)
        }
        return newMatchesList.toImmutableList()
    }

    fun endMatchAtIndex(index: Int): Boolean {
        assert(isIndexInArrayBounds(index))
        assert(matchesList[index] != null)
        val matchAtIndex = matchesList[index]!!
        if (matchAtIndex.isOver == 1) {
            return false
        }
        val newMatch = MatchTM(
            matchTmID = matchAtIndex.matchTmID,
            favorites = matchAtIndex.favorites,
            date = matchAtIndex.date,
            duration = matchAtIndex.duration,
            isOver = 1, // setting it to over
            gameID = matchAtIndex.gameID,
            tournamentID = matchAtIndex.tournamentID
        )
        val newList = mutableListOf<MatchTM?>()
        matchesList.forEach { newList.add(it) }
        newList[index] = newMatch
        matchesList = newList.toImmutableList()
        return true
    }
}