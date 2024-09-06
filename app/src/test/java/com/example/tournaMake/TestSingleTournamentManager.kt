package com.example.tournaMake

import com.example.tournaMake.sampledata.TournamentMatchData
import com.example.tournaMake.tournamentmanager.TournamentManagerV2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class TestSingleTournamentManager {

    @Test
    fun testBehaviourWhenTournamentIsUpdated() {
        val matches = mutableListOf(
            createSampleMatch(withIndex = 3, isOver = 1),
            createSampleMatch(withIndex = 4, isOver = 1),
            createSampleMatch(withIndex = 5),
            createSampleMatch(withIndex = 6)
        )
        val teamIDs = buildList<String> {
            for (i in 0 until 8) {
                add(UUID.randomUUID().toString())
            }
        }
        val tournamentMatchData = mutableListOf(
            createTournamentMatchData(
                matchID = matches[0].matchTmID,
                indexInTree = matches[0].indexInTournamentTree!!,
                teamID = teamIDs[0],
                teamName = "Team 0",
                isOver = matches[0].isOver,
                isWinner = 1
            ),
            createTournamentMatchData(
                matchID = matches[0].matchTmID,
                indexInTree = matches[0].indexInTournamentTree!!,
                teamID = teamIDs[1],
                teamName = "Team 1",
                isOver = matches[0].isOver,
                isWinner = 0
            ),
            createTournamentMatchData(
                matchID = matches[1].matchTmID,
                indexInTree = matches[1].indexInTournamentTree!!,
                teamID = teamIDs[2],
                teamName = "Team 2",
                isOver = matches[1].isOver,
                isWinner = 1
            ),
            createTournamentMatchData(
                matchID = matches[1].matchTmID,
                indexInTree = matches[1].indexInTournamentTree!!,
                teamID = teamIDs[3],
                teamName = "Team 3",
                isOver = matches[1].isOver,
                isWinner = 0
            )
        )
        for (i in 4 until 8 step 2) {
            tournamentMatchData.add(
                createTournamentMatchData(
                    matchID = matches[i / 2].matchTmID,
                    indexInTree = matches[i / 2].indexInTournamentTree!!,
                    teamID = teamIDs[i],
                    teamName = "Team $i",
                    isOver = matches[i / 2].isOver,
                    isWinner = 0
                )
            )
            tournamentMatchData.add(
                createTournamentMatchData(
                    matchID = matches[i / 2].matchTmID,
                    indexInTree = matches[i / 2].indexInTournamentTree!!,
                    teamID = teamIDs[i + 1],
                    teamName = "Team ${i + 1}",
                    isOver = matches[i / 2].isOver,
                    isWinner = 0
                )
            )
        }
        assertEquals(8, tournamentMatchData.size)

        // Creation of single tournament manager
        val tournamentManager = TournamentManagerV2(
            tournamentDataList = tournamentMatchData,
            dbMatches = matches,
            tournamentName = "Sample Tournament"
        )
        assertTrue(tournamentManager.shouldOtherMatchesBeCreated())
        matches += tournamentManager.generateNextRoundMatches()
        assertEquals(6, matches.size)
        // The screen would now be recreated, and a new tournament manager would be instantiated.
        val tournamentManager2 = TournamentManagerV2(
            tournamentDataList = tournamentMatchData,
            dbMatches = matches,
            tournamentName = "Sample Tournament"
        )
        assertFalse(tournamentManager2.shouldOtherMatchesBeCreated())

        val danglingTeams = tournamentManager.getDanglingTeams()
        assertEquals(2, danglingTeams.size) // team 0 and team 2
        assertEquals(listOf(teamIDs[0], teamIDs[2]), danglingTeams.map { it.teamID })

        val matchesWithAvailableSlots = tournamentManager.filterMatchesWithAvailableSlots()
        assertEquals(listOf(matches[4], matches[5]), matchesWithAvailableSlots)

        val mapOfTeamsAndMatches = tournamentManager.associateTeamsWithNextMatches(
            teams = danglingTeams,
            newMatches = matchesWithAvailableSlots
        )
        assertEquals(2, mapOfTeamsAndMatches.size)
        /* Both team 0 and team 2 should be associated to the match at matches[4],
        * which is the first match in round 1. */
        assertEquals(matches[4], mapOfTeamsAndMatches[tournamentMatchData[0]])
        assertEquals(matches[4], mapOfTeamsAndMatches[tournamentMatchData[2]])

        // The tournament is not over yet
        assertFalse(tournamentManager.isTournamentOver())
    }

    @Test
    fun testTournamentEnd() {
        // A micro-tournament, with only two teams
        val matches = listOf(
            createSampleMatch(withIndex = 0, isOver = 1)
        )
        val tournamentMatchData = listOf(
            createTournamentMatchData(
                matchID = matches[0].matchTmID,
                indexInTree = 0,
                teamName = "Winner Team",
                isWinner = 1,
                isOver = matches[0].isOver
            ),
            createTournamentMatchData(
                matchID = matches[0].matchTmID,
                indexInTree = 0,
                teamName = "Loser Team",
                isWinner = 0,
                isOver = matches[0].isOver
            )
        )
        val tournamentManager = TournamentManagerV2(
            tournamentDataList = tournamentMatchData,
            dbMatches = matches,
            tournamentName = "Sample tournament"
        )
        assertFalse(tournamentManager.shouldOtherMatchesBeCreated())
        assertTrue(tournamentManager.isTournamentOver())
        assertEquals(tournamentMatchData[0], tournamentManager.getTournamentWinner())
        assertNull(tournamentManager.getLoserBracketWinner())
        assertEquals(0, tournamentManager.getDanglingTeams().size)
    }
}