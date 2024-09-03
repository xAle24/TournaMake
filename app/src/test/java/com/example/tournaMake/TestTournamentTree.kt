package com.example.tournaMake

import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.tournamentmanager.TournamentTree
import com.example.tournaMake.tournamentmanager.isPowerOf2
import org.junit.Test

class TestTournamentTree {
    @Test
    fun testPowersOf2() {
        assert(isPowerOf2(1))
        assert(isPowerOf2(4))
        assert(!isPowerOf2(3))
        assert(!isPowerOf2(0))
        assert(!isPowerOf2(-2))
        assert(!isPowerOf2(6))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTournamentTreeCreationException() {
        TournamentTree(5)
    }

    @Test
    fun testTournamentTreeCreation() {
        val tree = TournamentTree(8)
        assert(tree.roundsNumber == 3)
        assert(tree.leavesNumber == 4)
        assert(tree.totalMatches == 7)
        assert(tree.matchesList == listOf<MatchTM?>(null, null, null, null, null, null, null))
    }
}