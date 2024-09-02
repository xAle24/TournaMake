package com.example.tournaMake.data.constants

enum class MatchStatus {ONGOING, ENDED}

fun mapIntegerToMatchStatus(n: Int) : MatchStatus {
    return when (n) {
        0 -> MatchStatus.ONGOING
        1 -> MatchStatus.ENDED
        else -> MatchStatus.ENDED
    }
}

enum class MatchResult {Loser, Winner, Draw}

fun mapMatchResultToInteger(mr: MatchResult) : Int {
    return when (mr) {
        MatchResult.Loser -> 0
        MatchResult.Winner -> 1
        MatchResult.Draw -> 2
    }
}