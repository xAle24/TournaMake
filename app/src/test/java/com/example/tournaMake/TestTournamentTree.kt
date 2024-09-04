package com.example.tournaMake

import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.TournamentMatchData
import com.example.tournaMake.tournamentmanager.TournamentTree
import com.example.tournaMake.tournamentmanager.isPowerOf2
import okhttp3.internal.toImmutableList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import kotlin.math.log2
import kotlin.math.pow

class TestTournamentTree {
    private val staticTree = TournamentTree(8)

    private fun createSampleMatch(withIndex: Int = 0): MatchTM {
        return MatchTM(
            matchTmID = UUID.randomUUID().toString(),
            favorites = 0,
            date = System.currentTimeMillis(),
            duration = 0,
            isOver = 0,
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
    private fun createSampleTournamentMatchData(leavesMatchIDs: List<String>): List<TournamentMatchData> {
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

    @Test
    fun testPowersOf2() {
        assertTrue(isPowerOf2(1))
        assertTrue(isPowerOf2(4))
        assertFalse(isPowerOf2(3))
        assertFalse(isPowerOf2(0))
        assertFalse(isPowerOf2(-2))
        assertFalse(isPowerOf2(6))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTournamentTreeCreationException() {
        TournamentTree(5)
    }

    @Test
    fun testTournamentTreeCreation() {
        val tree = TournamentTree(8)
        assertEquals(3, tree.roundsNumber)
        assertEquals(4, tree.leavesNumber)
        assertEquals(7, tree.totalMatches)
        assertEquals(listOf<MatchTM?>(null, null, null, null, null, null, null), tree.matchesList)
    }

    @Test
    fun testIndexInBounds() {
        assertFalse(staticTree.isIndexInArrayBounds(8))
        assertTrue(staticTree.isIndexInArrayBounds(0))
        assertTrue(staticTree.isIndexInArrayBounds(1))
    }

    @Test
    fun testRoundInBounds() {
        assertFalse(staticTree.isRoundInBounds(3))
        assertTrue(staticTree.isRoundInBounds(0))
        assertTrue(staticTree.isRoundInBounds(2))
    }

    @Test
    fun testFirstMatchIndexInRound() {
        assertEquals(3, staticTree.getIndexOfFirstMatchInRound(0))
        assertEquals(1, staticTree.getIndexOfFirstMatchInRound(1))
        assertEquals(0, staticTree.getIndexOfFirstMatchInRound(2))
    }

    @Test
    fun testLastMatchIndexInRound() {
        assertEquals(6, staticTree.getIndexOfLastMatchInRound(0))
        assertEquals(2, staticTree.getIndexOfLastMatchInRound(1))
        assertEquals(0, staticTree.getIndexOfLastMatchInRound(2))
    }

    @Test
    fun testAllRoundIndexes() {
        assertEquals(listOf(3, 4, 5, 6), staticTree.getAllMatchIndexesFromRound(0))
        assertEquals(listOf(1, 2), staticTree.getAllMatchIndexesFromRound(1))
        assertEquals(listOf(0), staticTree.getAllMatchIndexesFromRound(2))
    }

    /**
     * The numbers represent the match indexes:
     *           0                  round 2 (final)
     *      _____|_____
     *     |          |
     *     1          2             round 1 (semi finals)
     *  ___|___    ___|___
     * |      |   |      |
     * 3      4   5      6          round 0 (quarter finals)
     * */
    @Test
    fun testMatchIndexInNextRound() {
        /* The winning teams from match 3 and 4 in round 0 should end in match 1 in round 1 */
        assertEquals(1, staticTree.getIndexOfMatchInNextRound(3))
        assertEquals(1, staticTree.getIndexOfMatchInNextRound(4))
        /* The winning teams from match 5 and 6 in round 0 should end in match 2 in round 1 */
        assertEquals(2, staticTree.getIndexOfMatchInNextRound(5))
        assertEquals(2, staticTree.getIndexOfMatchInNextRound(6))
        /* The winning teams from match 1 and 2 in round 1 should end in match 0 in round 2, the final. */
        assertEquals(0, staticTree.getIndexOfMatchInNextRound(1))
        assertEquals(0, staticTree.getIndexOfMatchInNextRound(2))
    }

    @Test
    fun testGetSetMatch() {
        val tree = TournamentTree(8)
        val match = createSampleMatch()
        val index = tree.getIndexOfFirstMatchInRound(0)
        assertTrue(tree.canSetMatchAtIndex(index))
        tree.setMatchAtIndex(index, match)
        assertFalse(tree.canSetMatchAtIndex(index))
        assertEquals(match, tree.getMatchAtIndex(index))
    }

    @Test
    fun testGetSetOfMultipleMatches() {
        val tree = TournamentTree(8)
        val match1 = createSampleMatch()
        val match2 = createSampleMatch()
        val matchesList = listOf(match1, match2)
        val indexesOfRound1 = tree.getAllMatchIndexesFromRound(1)
        assertFalse(tree.canSetMatches(matchesList)) // sizes don't match

        val correctlySizedMatchesList = listOf(null, match1, match2, null, null, null, null)
        assertTrue(tree.canSetMatches(correctlySizedMatchesList))
        tree.setMatches(correctlySizedMatchesList)
        /* If the list is the same, it can be set once again.
        * Not sure if this can be useful in any way.
        * */
        assertTrue(tree.canSetMatches(correctlySizedMatchesList))
        /*
        * However, the 'isOver' flag of the matches can't be set from outside,
        * so the tree must expose methods to alter the match statuses.
        * */
        val copyOfMatch1 = MatchTM(
            matchTmID = match1.matchTmID,
            favorites = match1.favorites,
            date = match1.date,
            duration = match1.duration,
            isOver = 1, // setting it to over
            gameID = match1.gameID,
            tournamentID = match1.tournamentID,
            indexInTournamentTree = match1.indexInTournamentTree
        )
        val copyList = listOf(null, copyOfMatch1, match2, null, null, null, null)
        assertFalse(tree.canSetMatches(copyList))

        assertEquals(tree.getMatchesAtIndexes(indexesOfRound1), matchesList)

        val nullList = listOf<MatchTM?>(null, null, null, null, null, null, null)
        assertFalse(tree.canSetMatches(nullList)) // no overwriting of matches

        val match3 = createSampleMatch()
        val overwritingList = listOf(null, match1, match3, null, null, null, null)
        assertFalse(tree.canSetMatches(overwritingList)) // match2 would be overwritten

        val shuffledList = listOf(null, match3, match2, match1, null, null, null)
        assertFalse(tree.canSetMatches(shuffledList)) // match1 position cannot change
    }

    @Test
    fun testAutoGeneration() {
        val tree = TournamentTree(8)
        val round0Matches = tree.autoGenerateMatchesAtRound(0, "sampleTournament", "sampleGame")
        val round1Matches = tree.autoGenerateMatchesAtRound(1, "sampleTournament", "sampleGame")
        val round2Matches = tree.autoGenerateMatchesAtRound(2, "sampleTournament", "sampleGame")
        assertEquals(4, round0Matches.size)
        assertEquals(2, round1Matches.size)
        assertEquals(1, round2Matches.size)
    }

    @Test
    fun testEndMatch() {
        val tree = TournamentTree(2)
        val match = createSampleMatch()
        assertTrue(tree.canSetMatchAtIndex(0))
        tree.setMatchAtIndex(0, match)
        assertTrue(tree.endMatchAtIndex(0))
        assertFalse(tree.endMatchAtIndex(0))
    }

    @Test
    fun testTreeCreationAlternative() {
        val sampleMatches = mutableListOf<MatchTM>()
        var firstIndex = 3
        for (i in 0 until 4) {
            sampleMatches.add(createSampleMatch(firstIndex++))
        }
        val tmMatchData = createSampleTournamentMatchData(sampleMatches.map { it.matchTmID })
        val tree = TournamentTree(
            tournamentMatchDataList = tmMatchData,
            dbMatchesList = sampleMatches
        )
        assertEquals(7, tree.totalMatches)
        assertEquals(
            listOf(
                null, null, null,
                sampleMatches[0], sampleMatches[1], sampleMatches[2], sampleMatches[3]
            ),
            tree.matchesList
        )
        assertEquals(4, tree.leavesNumber)
        assertEquals(3, tree.roundsNumber)
    }
}