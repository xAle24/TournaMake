package com.example.tournaMake

import com.example.tournaMake.sampledata.MatchTM
import com.example.tournaMake.sampledata.TournamentMatchData
import com.example.tournaMake.tournamentmanager.TeamsMap
import com.example.tournaMake.tournamentmanager.TournamentTree
import org.junit.Test
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue


class TestTeamsMap {

    @Test
    fun testRemoveAt() {
        val list = mutableListOf(0, 1, 2, 3, 4)
        list.removeAt(0)
        assertEquals(0, list.indexOf(1))
    }

    /**
     * Creates a realistic team map for a single team.
     * The main role of this function is to realistically create the List<TournamentMatchData>
     * taken by the TeamsMap() constructor as a parameter.
     * To do so realistically, the team must play a match within different,
     * successive rounds, so the MatchTM objects have to have their index in tournament tree
     * correctly set. This is achieved by using a [TournamentTree] helper.
     * Also, in the case of a single elimination tournament, teams cannot score
     * more wins after they lose, so this method ensures to create all wins first and
     * all losses later. This might not be realistic enough for a double elimination
     * tournament though.
     * */
    private fun createTeamMapSample(
        matchesList: List<MatchTM> = emptyList(),
        teamID: String = UUID.randomUUID().toString(),
        numberOfTeams: Int,
        numberOfMatches: Int,
        numberOfWins: Int
    ): TeamsMap {
        assert(numberOfWins <= numberOfMatches)
        val tree = TournamentTree(numberOfTeams)
        val aMatchForEachRound = mutableListOf<MatchTM>()
        for (i in 0 until tree.roundsNumber) {
            aMatchForEachRound.add(
                tree.autoGenerateMatchesAtRound(
                    r = i,
                    tournamentID = "sampleTournament",
                    gameID = "sampleGame"
                )[0]
            )
        }
        assert(aMatchForEachRound.size >= numberOfMatches)
        var matchIndex = 0
        var winsToCreate = numberOfWins
        var lossesToCreate = numberOfMatches - numberOfWins
        while (winsToCreate-- > 0) {
            val teamMatchInARoundIndex = aMatchForEachRound[matchIndex].indexInTournamentTree!!
            tree.endMatchAtIndex(teamMatchInARoundIndex)
            /* Changing the match in the list to the finished one */
            aMatchForEachRound[matchIndex] = tree.getMatchAtIndex(teamMatchInARoundIndex)!!
            matchIndex++
        }
        while (lossesToCreate-- > 0) {
            val teamMatchInARoundIndex = aMatchForEachRound[matchIndex].indexInTournamentTree!!
            tree.endMatchAtIndex(teamMatchInARoundIndex)
            /* Changing the match in the list to the finished one */
            aMatchForEachRound[matchIndex] = tree.getMatchAtIndex(teamMatchInARoundIndex)!!
            matchIndex++
        }
        // Remove additional matches, if number of matches is less than the size of aMatchForEachRound
        val teamMatches = aMatchForEachRound.take(numberOfMatches)
        return TeamsMap(
            createTeamHistory(
                teamID = teamID,
                matches = matchesList.ifEmpty { teamMatches },
                wins = numberOfWins,
                losses = numberOfMatches - numberOfWins
            )!!
        )
    }

    @Test
    fun testSampleMapCreation() {
        val teamID = UUID.randomUUID().toString()
        val teamsMap = createTeamMapSample(
            teamID = teamID,
            numberOfTeams = 8,
            numberOfMatches = 3, // the team has reached the finals
            numberOfWins = 2
        )
        assertEquals(3, teamsMap.teamsMap.size) // 3 matches, so three pairs match-team
        assertEquals(3, teamsMap.getTeamHistoryInTournament(teamID).size)
        assertEquals(2, teamsMap.getTeamHistoryInTournament(teamID).filter { team -> team.isWinner == 1 }.size)
        assertEquals(1, teamsMap.getTeamHistoryInTournament(teamID).filter { team -> team.isWinner == 0 }.size)
    }

    @Test
    fun testDanglingTeams() {
        val team1ID = UUID.randomUUID().toString()
        val team2ID = UUID.randomUUID().toString()
        val team3ID = UUID.randomUUID().toString()
        val team1Matches = listOf(
            createSampleMatch(withIndex = 3, isOver = 1),
            createSampleMatch(withIndex = 1, isOver = 0)
        )
        val team2Matches = listOf(
            createSampleMatch(withIndex = 5, isOver = 1)
        )
        val team3Matches = listOf(team1Matches[0]) // team 3 played against team 1 in round 0
        val team1History = listOf(
            TournamentMatchData(
                matchTmID = team1Matches[0].matchTmID,
                indexInTournamentTree = team1Matches[0].indexInTournamentTree!!,
                isOver = team1Matches[0].isOver,
                gameID = "sampleGame",
                tournamentID = "sampleTournament",
                teamID = team1ID,
                name = "Team 1",
                isWinner = 1, // the team won the first match
                score = 0
            ),
            TournamentMatchData(
                matchTmID = team1Matches[1].matchTmID,
                indexInTournamentTree = team1Matches[1].indexInTournamentTree!!,
                isOver = team1Matches[1].isOver,
                gameID = "sampleGame",
                tournamentID = "sampleTournament",
                teamID = team1ID,
                name = "Team 1",
                isWinner = 0, // the team hasn't finished the match yet, so it has the default value
                score = 0
            )
        )
        val team2History = listOf(
            TournamentMatchData(
                matchTmID = team2Matches[0].matchTmID,
                indexInTournamentTree = team2Matches[0].indexInTournamentTree!!,
                isOver = team2Matches[0].isOver,
                gameID = "sampleGame",
                tournamentID = "sampleTournament",
                teamID = team2ID,
                name = "Team 2",
                isWinner = 1, // the team has won its match and it's dangling now (waiting for a new match)
                score = 0
            )
        )
        val team3History = listOf(
            TournamentMatchData(
                matchTmID = team3Matches[0].matchTmID,
                indexInTournamentTree = team3Matches[0].indexInTournamentTree!!,
                isOver = team3Matches[0].isOver,
                gameID = "sampleGame",
                tournamentID = "sampleTournament",
                teamID = team3ID,
                name = "Team 3",
                isWinner = 0, // the team has lost its match against team 1. In single it should not be dangling, in double it should
                score = 0
            )
        )
        val teamsMap = TeamsMap(team1History + team2History)
        assertEquals(1, teamsMap.getDanglingTeams(::canContinueSingle).size)
        assertEquals(team2ID, teamsMap.getDanglingTeams(::canContinueSingle).first().teamID)

        val teamsMap2 = TeamsMap(team1History + team2History + team3History)
        assertEquals(1, teamsMap.getDanglingTeams(::canContinueSingle).size)
        assertEquals(2, teamsMap.getDanglingTeams(::canContinueDouble).size)
        assertTrue(teamsMap2.getDanglingTeams(::canContinueDouble).map { it.teamID }.contains(team3ID))
        assertFalse(teamsMap2.getDanglingTeams(::canContinueSingle).map { it.teamID }.contains(team3ID))
    }
}