package com.example.tournaMake.data.tournament

import com.example.tournaMake.mylibrary.displaymodels.BracketDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketMatchDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketRoundDisplayModel
import com.example.tournaMake.mylibrary.displaymodels.BracketTeamDisplayModel

object TestTournamentData {
    private val singleEliminationQuarterFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Karmine Corp",
                isWinner = false,
                score = "1",
            ),
            BracketTeamDisplayModel(
                name = "Moist Esports",
                isWinner = true,
                score = "4",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Team Secret",
                isWinner = true,
                score = "4",
            ),
            BracketTeamDisplayModel(
                name = "Version1",
                isWinner = false,
                score = "1",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Oxygen Esports",
                isWinner = false,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "FaZe Clan",
                isWinner = true,
                score = "4",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = true,
                score = "4",
            ),
            BracketTeamDisplayModel(
                name = "Team Liquid",
                isWinner = false,
                score = "3",
            ),
            tournamentData = null
        ),
    )

    private val singleEliminationSemiFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Moist Esports",
                isWinner = true,
                score = "4",
            ),
            BracketTeamDisplayModel(
                name = "Team Secret",
                isWinner = false,
                score = "2",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "FaZe Clan",
                isWinner = false,
                score = "1",
            ),
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = true,
                score = "4",
            ),
            tournamentData = null
        ),
    )

    private val singleEliminationGrandFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Moist Esports",
                isWinner = false,
                score = "2",
            ),
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = true,
                score = "4",
            ),
            tournamentData = null
        ),
    )

    private val singleEliminationBracketRounds = listOf(
        BracketRoundDisplayModel(
            name = "Quarter Finals",
            matches = singleEliminationQuarterFinals,
        ),
        BracketRoundDisplayModel(
            name = "Semi Finals",
            matches = singleEliminationSemiFinals,
        ),
        BracketRoundDisplayModel(
            name = "Grand Finals",
            matches = singleEliminationGrandFinals,
        ),
    )

    val singleEliminationBracket = BracketDisplayModel(
        name = "RLCS Fall Major",
        rounds = singleEliminationBracketRounds,
    )

    private val upperBracketRound1 = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "FaZe Clan",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "hey bro",
                isWinner = false,
                score = "0",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Spacestation",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "M80",
                isWinner = false,
                score = "0",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "FURIA",
                isWinner = false,
                score = "1",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Dignitas",
                isWinner = false,
                score = "1",
            ),
            BracketTeamDisplayModel(
                name = "NRG",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Complexity",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "Zero2One",
                isWinner = false,
                score = "0",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Version1",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "Shopify Rebellion",
                isWinner = false,
                score = "0",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "G2 Esports",
                isWinner = false,
                score = "2",
            ),
            BracketTeamDisplayModel(
                name = "sup",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "OpTic Gaming",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "KOI",
                isWinner = false,
                score = "1",
            ),
            tournamentData = null
        ),
    )

    private val upperBracketQuarterfinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "FaZe Clan",
                isWinner = false,
                score = "2",
            ),
            BracketTeamDisplayModel(
                name = "Spacestation",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "NRG",
                isWinner = false,
                score = "0",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Complexity",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "Version1",
                isWinner = false,
                score = "0",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "sup",
                isWinner = false,
                score = "0",
            ),
            BracketTeamDisplayModel(
                name = "OpTic Gaming",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
    )

    private val upperBracketSemiFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Spacestation",
                isWinner = false,
                score = "1",
            ),
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = true,
                score = "4",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Complexity",
                isWinner = true,
                score = "4",
            ),
            BracketTeamDisplayModel(
                name = "OpTic Gaming",
                isWinner = false,
                score = "1",
            ),
            tournamentData = null
        ),
    )

    private val upperBracketFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = false,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "Complexity",
                isWinner = true,
                score = "4",
            ),
            tournamentData = null
        ),
    )

    private val doubleEliminationGrandFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Complexity",
                isWinner = true,
                score = "4",
            ),
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = false,
                score = "2",
            ),
            tournamentData = null
        ),
    )

    private val lowerBracketRound1 = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "hey bro",
                isWinner = false,
                score = "1",
            ),
            BracketTeamDisplayModel(
                name = "M80",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "FURIA",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "Dignitas",
                isWinner = false,
                score = "0",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Zero2One",
                isWinner = false,
                score = "0",
            ),
            BracketTeamDisplayModel(
                name = "Shopify Rebellion",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "G2 Esports",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "KOI",
                isWinner = false,
                score = "2",
            ),
            tournamentData = null
        ),
    )

    val lowerBracketRound2 = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "sup",
                isWinner = true,
                score = "3",
            ),
            BracketTeamDisplayModel(
                name = "M80",
                isWinner = false,
                score = "2",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Version1",
                isWinner = false,
                score = "0",
            ),
            BracketTeamDisplayModel(
                name = "FURIA",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "NRG",
                isWinner = false,
                score = "1",
            ),
            BracketTeamDisplayModel(
                name = "Shopify Rebellion",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "FaZe Clan",
                isWinner = false,
                score = "0",
            ),
            BracketTeamDisplayModel(
                name = "G2 Esports",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
    )

    val lowerBracketRound3 = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "sup",
                isWinner = false,
                score = "0",
            ),
            BracketTeamDisplayModel(
                name = "FURIA",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Shopify Rebellion",
                isWinner = false,
                score = "0",
            ),
            BracketTeamDisplayModel(
                name = "G2 Esports",
                isWinner = true,
                score = "3",
            ),
            tournamentData = null
        ),
    )

    val lowerBracketQuarterFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Spacestation",
                isWinner = true,
                score = "4",
            ),
            BracketTeamDisplayModel(
                name = "FURIA",
                isWinner = false,
                score = "2",
            ),
            tournamentData = null
        ),
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "OpTic Gaming",
                isWinner = true,
                score = "4",
            ),
            BracketTeamDisplayModel(
                name = "G2 Esports",
                isWinner = false,
                score = "2",
            ),
            tournamentData = null
        ),
    )

    val lowerBracketSemiFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Spacestation",
                isWinner = false,
                score = "2",
            ),
            BracketTeamDisplayModel(
                name = "OpTic Gaming",
                isWinner = true,
                score = "4",
            ),
            tournamentData = null
        ),
    )

    val lowerBracketFinals = listOf(
        BracketMatchDisplayModel(
            BracketTeamDisplayModel(
                name = "Gen.G Mobil1 Racing",
                isWinner = true,
                score = "4",
            ),
            BracketTeamDisplayModel(
                name = "OpTic Gaming",
                isWinner = false,
                score = "1",
            ),
            tournamentData = null
        ),
    )

    val upperBracketRounds = listOf(
        BracketRoundDisplayModel("UB Round 1", upperBracketRound1),
        BracketRoundDisplayModel("UB Quarterfinals", upperBracketQuarterfinals),
        BracketRoundDisplayModel("UB Semifinals", upperBracketSemiFinals),
        BracketRoundDisplayModel("UB Final", upperBracketFinals),
        BracketRoundDisplayModel("Grand Final", doubleEliminationGrandFinals),
    )

    val lowerBracketRounds = listOf(
        BracketRoundDisplayModel("LB Round 1", lowerBracketRound1),
        BracketRoundDisplayModel("LB Round 2", lowerBracketRound2),
        BracketRoundDisplayModel("LB Round 3", lowerBracketRound3),
        BracketRoundDisplayModel("LB Quarterfinals", lowerBracketQuarterFinals),
        BracketRoundDisplayModel("LB Semifinal", lowerBracketSemiFinals),
        BracketRoundDisplayModel("LB Final", lowerBracketFinals),
    )
}
