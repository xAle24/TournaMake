package com.example.tournaMake.sampledata

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "ACHIEVEMENT", primaryKeys = ["achievementID"])
data class Achievement(
    @ColumnInfo(name = "achievementID") val achievementID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "imagePath") val imagePath: String,
    @ColumnInfo(name = "achievements_playerID") val achievementsPlayerID: String
)

@Entity(tableName = "ACHIEVEMENT_PLAYER", primaryKeys = ["achievements_playerID"])
data class AchievementPlayer(
    @ColumnInfo(name = "achievements_playerID") val achievementsPlayerID: String,
    @ColumnInfo(name = "achievementID") val achievementID: String?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "email") val email: String
)
@Entity(tableName = "TEAM", primaryKeys = ["teamID"])
data class Team(
    @ColumnInfo(name = "teamID") val teamID: String,
    @ColumnInfo(name = "name") val name: String
)

@Entity(tableName = "GAME", primaryKeys = ["gameID"])
data class Game(
    @ColumnInfo(name = "gameID") val gameID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "favorites") val favorites: Boolean,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "minPlayers") val minPlayers: Int,
    @ColumnInfo(name = "maxPlayers") val maxPlayers: Int
)

@Entity(tableName = "GUEST_PARTICIPANT_SCORE", primaryKeys = ["teamID", "Username"])
data class GuestParticipantScore(
    @ColumnInfo(name = "Username") val username: String,
    @ColumnInfo(name = "teamID") val teamID: String,
    @ColumnInfo(name = "score") val score: Int
)

@Entity(tableName = "GUEST_PROFILE", primaryKeys = ["Username"])
data class GuestProfile(
    @ColumnInfo(name = "Username") val username: String
)

@Entity(tableName = "MAIN_PROFILE", primaryKeys = ["email"])
data class MainProfile(
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "profileImage") val profileImage: String,
    @ColumnInfo(name = "wonTournamentsNumber") val wonTournamentsNumber: Int,
    @ColumnInfo(name = "locationLatitude") val locationLatitude: Float,
    @ColumnInfo(name = "locationLongitude") val locationLongitude: Float
)

@Entity(tableName = "MAIN_TEAM_SCORE", primaryKeys = ["teamID", "email"])
data class MainTeamScore(
    @ColumnInfo(name = "teamID") val teamID: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "score") val score: Int
)

@Entity(tableName = "MATCH_TM", primaryKeys = ["matchID"])
data class Match(
    @ColumnInfo(name = "matchID") val matchID: String,
    @ColumnInfo(name = "favorites") val favorites: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "status") val status: Int,
    @ColumnInfo(name = "gameID") val gameID: String,
    @ColumnInfo(name = "tournamentID") val tournamentID: String?
)

@Entity(tableName = "TEAM_PLAY_MATCH", primaryKeys = ["matchID", "teamID"])
data class TeamPlayMatch(
    @ColumnInfo(name = "matchID") val matchID: String,
    @ColumnInfo(name = "teamID") val teamID: String
)

@Entity(tableName = "TOURNAMENT", primaryKeys = ["tournamentID"])
data class Tournament(
    @ColumnInfo(name = "tournamentID") val tournamentID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "favorites") val favorites: String,
    @ColumnInfo(name = "status") val status: Int,
    @ColumnInfo(name = "locationLatitude") val locationLatitude: Float,
    @ColumnInfo(name = "locationLongitude") val locationLongitude: Float,
    @ColumnInfo(name = "scheduledDate") val scheduledDate: Long,
    @ColumnInfo(name = "tournamentTypeID") val tournamentTypeID: String
)

@Entity(tableName = "TOURNAMENT_TYPE", primaryKeys = ["tournamentTypeID"])
data class TournamentType(
    @ColumnInfo(name = "tournamentTypeID") val tournamentTypeID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String
)

