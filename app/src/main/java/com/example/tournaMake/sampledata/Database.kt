package com.example.tournaMake.sampledata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "ACHIEVEMENT")
data class Achievement(
    @PrimaryKey @ColumnInfo(name = "achievementID") val achievementID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "imagePath") val imagePath: String
)

@Entity(tableName = "ACHIEVEMENT_PLAYER",
    foreignKeys = [
        ForeignKey(entity = Achievement::class,
            parentColumns = ["achievementID"],
            childColumns = ["achievementID"]),
        ForeignKey(entity = MainProfile::class,
            parentColumns = ["email"],
            childColumns = ["email"])
    ])
data class AchievementPlayer(
    @PrimaryKey @ColumnInfo(name = "achievements_playerID") val achievementsPlayerID: String,
    @ColumnInfo(name = "achievementID") val achievementID: String?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "email") val email: String
)

@Entity(tableName = "GAME")
data class Game(
    @PrimaryKey @ColumnInfo(name = "gameID") val gameID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "MinPlayer") val minPlayer: Int,
    @ColumnInfo(name = "maxPlayers") val maxPlayers: Int
)

@Entity(tableName = "GUEST_PROFILE")
data class GuestProfile(
    @PrimaryKey @ColumnInfo(name = "Username") val username: String
)

@Entity(tableName = "MAIN_PROFILE")
data class MainProfile(
    @PrimaryKey @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "profileImage") val profileImage: String,
    @ColumnInfo(name = "wonTournamentsNumber") val wonTournamentsNumber: Int,
    @ColumnInfo(name = "locationLatitude") val locationLatitude: Float,
    @ColumnInfo(name = "locationLongitude") val locationLongitude: Float
)

@Entity(tableName = "MATCH_TM",
    foreignKeys = [
        ForeignKey(entity = Game::class,
            parentColumns = ["gameID"],
            childColumns = ["gameID"]),
        ForeignKey(entity = Tournament::class,
            parentColumns = ["tournamentID"],
            childColumns = ["tournamentID"])
    ])
data class Match(
    @PrimaryKey @ColumnInfo(name = "matchID") val matchID: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "status") val status: Int,
    @ColumnInfo(name = "gameID") val gameID: String,
    @ColumnInfo(name = "tournamentID") val tournamentID: String?
)

@Entity(tableName = "MATCH_SCORE_GUEST",
    foreignKeys = [
        ForeignKey(entity = Match::class,
            parentColumns = ["matchID"],
            childColumns = ["matchID"]),
        ForeignKey(entity = GuestProfile::class,
            parentColumns = ["Username"],
            childColumns = ["Username"])
    ])
data class MatchScoreGuest(
    @PrimaryKey @ColumnInfo(name = "Username") val username: String,
    @ColumnInfo(name = "matchID") val matchID: String,
    @ColumnInfo(name = "score") val score: Int
)

@Entity(tableName = "MATCH_SCORE_MAIN",
    foreignKeys = [
        ForeignKey(entity = MainProfile::class,
            parentColumns = ["email"],
            childColumns = ["email"]),
        ForeignKey(entity = Match::class,
            parentColumns = ["matchID"],
            childColumns = ["matchID"])
    ],
    primaryKeys = ["email", "matchID"])
data class MatchScoreMain(
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "matchID") val matchID: String,
    @ColumnInfo(name = "score") val score: Int
)

@Entity(tableName = "TOURNAMENT",
    foreignKeys = [
        ForeignKey(entity = TournamentType::class,
            parentColumns = ["tournamentTypeID"],
            childColumns = ["tournamentTypeID"])
    ])
data class Tournament(
    @PrimaryKey @ColumnInfo(name = "tournamentID") val tournamentID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "status") val status: Int,
    @ColumnInfo(name = "locationLatitude") val locationLatitude: Float,
    @ColumnInfo(name = "locationLongitude") val locationLongitude: Float,
    @ColumnInfo(name = "scheduledDate") val scheduledDate: Long,
    @ColumnInfo(name = "tournamentTypeID") val tournamentTypeID: String
)

@Entity(tableName = "TOURNAMENT_TYPE")
data class TournamentType(
    @PrimaryKey @ColumnInfo(name = "tournamentTypeID") val tournamentTypeID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String
)

