package com.example.tournaMake.sampledata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "NOTIFICATION",
    foreignKeys = [
        ForeignKey(
            entity = MainProfile::class,
            parentColumns = ["email"],
            childColumns = ["email"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["email"])]
)
data class Notification(
    @PrimaryKey
    @ColumnInfo(name = "notificationID")
    val notificationID: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "email")
    val email: String
)

@Entity(tableName = "ACHIEVEMENT", primaryKeys = ["achievementID"])
data class Achievement(
    val achievementID: String,
    val name: String,
    val description: String,
    val imagePath: String,
)

@Entity(
    tableName = "ACHIEVEMENT_PLAYER",
    primaryKeys = ["achievementID", "email"],
    foreignKeys = [
        ForeignKey(
            entity = Achievement::class,
            parentColumns = ["achievementID"],
            childColumns = ["achievementID"]
        ),
        ForeignKey(
            entity = MainProfile::class,
            parentColumns = ["email"],
            childColumns = ["email"]
        )
    ])
data class AchievementPlayer(
    val achievementID: String,
    val status: Int,
    val email: String
)

@Entity(tableName = "GAME", primaryKeys = ["gameID"])
data class Game(
    val gameID: String,
    val name: String,
    val favorites: Int,
    val description: String?,
    val duration: Int?,
    val minPlayers: Int,
    val maxPlayers: Int
)

@Entity(
    tableName = "GUEST_PARTICIPANT",
    primaryKeys = ["teamID", "username"],
    foreignKeys = [
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamID"],
            childColumns = ["teamID"]
        ),
        ForeignKey(
            entity = GuestProfile::class,
            parentColumns = ["username"],
            childColumns = ["username"]
        )
    ])
data class GuestParticipant(
    val username: String,
    val teamID: String
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

@Entity(
    tableName = "MAIN_PARTICIPANT",
    primaryKeys = ["teamID", "email"],
    foreignKeys = [
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamID"],
            childColumns = ["teamID"]
        ),
        ForeignKey(
            entity = MainProfile::class,
            parentColumns = ["email"],
            childColumns = ["email"]
        )
    ])
data class MainParticipant(
    val teamID: String,
    val email: String
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
    @ColumnInfo(name = "favorites") val favorites: Int,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "isOver") val isOver: Int,
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
    @ColumnInfo(name = "matchTmID") val matchTmID: String,
    val score: Int,
    val isWinner: Int
)

@Entity(tableName = "TEAM", primaryKeys = ["teamID"])
data class Team(
    val teamID: String,
    val name: String
)

@Entity(tableName = "TOURNAMENT", primaryKeys = ["tournamentID"])
data class Tournament(
    val tournamentID: String,
    val name: String,
    val favorites: Int,
    val isOver: Int,
    val scheduledDate: Long?,
    val tournamentTypeID: String
)

@Entity(tableName = "TOURNAMENT_TYPE", primaryKeys = ["tournamentTypeID"])
data class TournamentType(
    val tournamentTypeID: String,
    val name: String,
    val description: String
)


