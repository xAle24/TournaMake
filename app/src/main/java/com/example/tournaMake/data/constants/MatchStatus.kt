package com.example.tournaMake.data.constants

enum class MatchStatus {ONGOING, ENDED}

fun mapIntegerToMatchStatus(n: Int) : MatchStatus {
    return when (n) {
        0 -> MatchStatus.ONGOING
        1 -> MatchStatus.ENDED
        else -> MatchStatus.ENDED
    }
}

fun mapIntegerStatusToString(n: Int) : String {
    return when (n) {
        0 -> "Ongoing"
        1 -> "Ended"
        else -> "Unknown"
    }
}