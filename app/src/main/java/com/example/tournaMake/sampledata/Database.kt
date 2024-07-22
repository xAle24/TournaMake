package com.example.tournaMake.sampledata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "ACHIEVEMENT", primaryKeys = ["achievementID"])
data class Achievement(
    val achievementID: String,
    val name: String,
    val description: String,
    val imagePath: String,
    val achievementsPlayerID: String
)

@Entity(tableName = "ACHIEVEMENT_PLAYER", primaryKeys = ["achievementsPlayerID"])
data class AchievementPlayer(
    val achievementsPlayerID: String,
    val achievementID: String?,
    val status: Char,
    val email: String
)

@Entity(tableName = "GAME", primaryKeys = ["gameID"])
data class Game(
    val gameID: String,
    val name: String,
    val favorites: Char,
    val description: String?,
    val duration: Int?,
    val minPlayers: Int,
    val maxPlayers: Int
)

@Entity(tableName = "GUEST_PARTICIPANT_SCORE", primaryKeys = ["teamID", "username"])
data class GuestParticipantScore(
    val username: String,
    val teamID: String,
    val score: Int
)

@Entity(tableName = "GUEST_PROFILE", primaryKeys = ["username"])
data class GuestProfile(
    val username: String
)

@Entity(tableName = "MAIN_PROFILE", primaryKeys = ["email"])
data class MainProfile(
    val username: String,
    val password: String,
    val email: String,
    val profileImage: String?,
    val wonTournamentsNumber: Int,
    val locationLatitude: Double?,
    val locationLongitude: Double?
)

@Entity(tableName = "MAIN_PARTICIPANT_SCORE", primaryKeys = ["teamID", "email"])
data class MainParticipantScore(
    val teamID: String,
    val email: String,
    val score: Int
)

@Entity(
    tableName = "MATCH_TM",
    primaryKeys = ["matchTmID"],
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["gameID"],
            childColumns = ["gameID"]
        ),
        ForeignKey(
            entity = Tournament::class,
            parentColumns = ["tournamentID"],
            childColumns = ["tournamentID"]
        )
    ]
)
data class MatchTM(
    @ColumnInfo(name = "matchTmID") val matchTmID: String,
    @ColumnInfo(name = "favorites") val favorites: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "status") val status: Int,
    @ColumnInfo(name = "gameID") val gameID: String,
    @ColumnInfo(name = "tournamentID") val tournamentID: String?
)

@Entity(
    tableName = "TEAM_IN_TM",
    primaryKeys = ["teamID", "matchTmID"],
    foreignKeys = [
        ForeignKey(
            entity = MatchTM::class,
            parentColumns = ["matchTmID"],
            childColumns = ["matchTmID"]
        ),
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamID"],
            childColumns = ["teamID"]
        )
    ]
)
data class TeamInTm(
    @ColumnInfo(name = "teamID") val teamID: String,
    @ColumnInfo(name = "matchTmID") val matchTmID: String
)

@Entity(tableName = "TEAM", primaryKeys = ["teamID"])
data class Team(
    val teamID: String,
    val name: String,
    val isWinner: Char,
    val score: Int
)

@Entity(tableName = "TOURNAMENT", primaryKeys = ["tournamentID"])
data class Tournament(
    val tournamentID: String,
    val name: String,
    val favorites: Char,
    val status: Int,
    val locationLatitude: Float?,
    val locationLongitude: Float?,
    val scheduledDate: Long?,
    val tournamentTypeID: String
)

@Entity(tableName = "TOURNAMENT_TYPE", primaryKeys = ["tournamentTypeID"])
data class TournamentType(
    val tournamentTypeID: String,
    val name: String,
    val description: String
)


