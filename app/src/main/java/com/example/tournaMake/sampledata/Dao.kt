package com.example.tournaMake.sampledata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface AchievementDao {
    @Query("SELECT * FROM ACHIEVEMENT")
    fun getAll(): List<Achievement>

    @Insert
    fun insertAll(vararg achievements: Achievement)

    @Delete
    fun delete(achievement: Achievement)
}

data class AchievementResult(
    val achievementID: String,
    val name: String,
    val description: String,
    val imagePath: String,
    val status: Char,
    val email: String
)

@Dao
interface AchievementPlayerDao {
    @Query("SELECT * FROM ACHIEVEMENT_PLAYER")
    fun getAll(): List<AchievementPlayer>

    @Query(
        "SELECT ACHIEVEMENT.*, ACHIEVEMENT_PLAYER.status, ACHIEVEMENT_PLAYER.email \n" +
                "FROM ACHIEVEMENT \n" +
                "INNER JOIN ACHIEVEMENT_PLAYER ON ACHIEVEMENT.achievementID = ACHIEVEMENT_PLAYER.achievementID \n" +
                "WHERE ACHIEVEMENT_PLAYER.email = :email\n"
    )
    fun getAchievementsByEmail(email: String): List<AchievementResult>

    @Insert
    fun insertAll(vararg achievementPlayers: AchievementPlayer)

    @Delete
    fun delete(achievementPlayer: AchievementPlayer)
}

data class PlayedGame(
    val gameID: String,
    val name: String,
    val description: String,
    val duration: Int,
    val minPlayers: Int,
    val maxPlayers: Int,
    val timesPlayed: Int
)

@Dao
interface GameDao {
    @Query("SELECT * FROM GAME")
    fun getAll(): List<Game>

    @Query(
        """
        SELECT GAME.*, COUNT(MATCH_TM.matchTmID) as timesPlayed
        FROM GAME
        JOIN MATCH_TM ON GAME.gameID = MATCH_TM.gameID
        JOIN TEAM_IN_TM ON MATCH_TM.matchTmID = TEAM_IN_TM.matchTmID
        JOIN TEAM ON TEAM_IN_TM.teamID = TEAM.teamID
        JOIN MAIN_PARTICIPANT ON TEAM.teamID = MAIN_PARTICIPANT.teamID
        WHERE MAIN_PARTICIPANT.email = :email
        GROUP BY GAME.name;
    """
    )
    fun getPlayedGames(email: String): List<PlayedGame>

    @Query("SELECT * FROM GAME WHERE gameID = :gameID")
    fun getGameFromID(gameID: String): Game
    @Query("""UPDATE GAME SET favorites = 1 WHERE gameID = :gameID""")
    fun setGameFavorites(gameID: String)
    @Query("""UPDATE GAME SET favorites = 0 WHERE gameID = :gameID""")
    fun removeGameFavorites(gameID: String)
    @Insert
    fun insertAll(vararg games: Game)

    @Delete
    fun delete(game: Game)
}

data class MatchGameData(
    val matchTmID: String,
    val favorites: Int,
    val date: Long,
    val duration: Int,
    val isOver: Int,
    val gameID: String,
    val tournamentID: String?,
    val name: String // the game name
)

@Dao
interface MatchDao {
    @Query("SELECT * FROM `MATCH_TM`")
    fun getAll(): List<MatchTM>

    @Query(
        """SELECT MATCH_TM.*, GAME.name
        FROM MATCH_TM
        JOIN GAME ON GAME.gameID = MATCH_TM.gameID"""
    )
    fun getAllWithGamesNames(): List<MatchGameData>

    @Query(
        """SELECT MATCH_TM.*, GAME.name
            FROM MATCH_TM
            JOIN TEAM_IN_TM ON MATCH_TM.matchTmID = TEAM_IN_TM.matchTmID
            JOIN MAIN_PARTICIPANT ON TEAM_IN_TM.teamID = MAIN_PARTICIPANT.teamID
            JOIN GAME ON GAME.gameID = MATCH_TM.gameID
            WHERE MAIN_PARTICIPANT.email = :email"""
    )
    fun getMyMatches(email: String): List<MatchGameData>

    @Query("""SELECT * FROM MATCH_TM WHERE favorites = 1""")
    fun getFavoritesMatch(): List<MatchTM>

    @Query("""UPDATE MATCH_TM SET favorites = 1 WHERE matchTmID = :matchTmID""")
    fun setMatchFavorites(matchTmID: String)

    @Query("""UPDATE MATCH_TM SET favorites = 0 WHERE matchTmID = :matchTmID""")
    fun removeMatchFavorites(matchTmID: String)

    @Query("SELECT * FROM MATCH_TM WHERE matchTmID = :matchID")
    fun getMatchFromID(matchID: String): MatchTM

    @Query("UPDATE MATCH_TM SET isOver = 1 WHERE matchTmID = :matchID")
    fun endMatch(matchID: String)

    @Query(
        """
        SELECT
            NOT EXISTS (
                SELECT *
                FROM TEAM_IN_TM
                WHERE TEAM_IN_TM.matchTmID = :matchID
                AND TEAM_IN_TM.isWinner <> 2 -- 2 means "Draw"
            ) AS allScoredADraw
    """
    )
    fun isDraw(matchID: String): Boolean

    @Query("SELECT * FROM MATCH_TM WHERE tournamentID = :tournamentID")
    fun getMatchesInTournament(tournamentID: String): LiveData<List<MatchTM>>

    @Insert
    fun insertAll(vararg matches: MatchTM)

    @Delete
    fun delete(match: MatchTM)
}

@Dao
interface GuestParticipantsDao {
    @Query("SELECT * FROM GUEST_PARTICIPANT")
    fun getAll(): List<GuestParticipant>

    @Query("SELECT * FROM GUEST_PARTICIPANT WHERE teamID = :teamID")
    fun getAllGuestParticipantsFromTeam(teamID: String): List<GuestParticipant>

    @Insert
    fun insertAll(matchScoreGuests: List<GuestParticipant>)

    @Insert
    fun insertAll(vararg matchScoreGuest: GuestParticipant)

    @Delete
    fun delete(matchScoreGuest: GuestParticipant)
}

@Dao
interface MainParticipantsDao {
    @Query("SELECT * FROM MAIN_PARTICIPANT")
    fun getAll(): List<MainParticipant>

    @Query("SELECT * FROM MAIN_PARTICIPANT WHERE teamID = :teamID")
    fun getAllMainParticipantsFromTeam(teamID: String): List<MainParticipant>

    @Insert
    fun insertAll(matchScoreMains: List<MainParticipant>)

    @Insert
    fun insertAll(vararg mainParticipants: MainParticipant)

    @Delete
    fun delete(matchScoreMain: MainParticipant)
}

data class TournamentMatchData(
    val matchTmID: String,
    val indexInTournamentTree: Int,
    val isOver: Int,
    val gameID: String,
    val tournamentID: String,
    val teamID: String,
    val name: String, // team name
    val isWinner: Int,
    val score: Int
)

@Dao
interface TournamentDao {
    @Query("SELECT * FROM TOURNAMENT")
    fun getAll(): List<Tournament>

    @Insert
    fun insertAll(vararg tournaments: Tournament)

    @Delete
    fun delete(tournament: Tournament)

    @Query(
        """
    SELECT 
        MATCH_TM.matchTmID,
        MATCH_TM.isOver,
        MATCH_TM.indexInTournamentTree,
        MATCH_TM.gameID, 
        MATCH_TM.tournamentID,
        TEAM.*,
        TEAM_IN_TM.*
    FROM
        MATCH_TM
    JOIN
        TEAM_IN_TM ON TEAM_IN_TM.matchTmID = MATCH_TM.matchTmID
    JOIN
        TEAM ON TEAM.teamID = TEAM_IN_TM.teamID
    WHERE 
        MATCH_TM.tournamentID = :tournamentID
    """
    )
    fun getMatchesAndTeamsFromTournamentID(tournamentID: String): List<TournamentMatchData>

    @Query(
        """
    SELECT 
        MATCH_TM.matchTmID,
        MATCH_TM.isOver,
        MATCH_TM.indexInTournamentTree,
        MATCH_TM.gameID, 
        MATCH_TM.tournamentID,
        TEAM.*,
        TEAM_IN_TM.*
    FROM
        MATCH_TM
    JOIN
        TEAM_IN_TM ON TEAM_IN_TM.matchTmID = MATCH_TM.matchTmID
    JOIN
        TEAM ON TEAM.teamID = TEAM_IN_TM.teamID
    WHERE 
        MATCH_TM.tournamentID = :tournamentID
    """
    )
    fun getTournamentMatchLiveData(tournamentID: String): LiveData<List<TournamentMatchData>>

    @Query("SELECT * FROM TOURNAMENT WHERE tournamentID = :tournamentID")
    fun getTournamentFromID(tournamentID: String): Tournament

    @Query("""
    UPDATE 
        TOURNAMENT 
    SET 
        isOver = 1 
    WHERE 
        tournamentID = :tournamentID;
    """)
    fun endTournament(tournamentID: String)

    @Query("""
    UPDATE 
        MAIN_PROFILE
    SET
        wonTournamentsNumber = wonTournamentsNumber + 1
    WHERE
        MAIN_PROFILE.email
    IN (
        SELECT MAIN_PROFILE.email
        FROM MAIN_PROFILE
        JOIN MAIN_PARTICIPANT ON MAIN_PARTICIPANT.email = MAIN_PROFILE.email
        JOIN TEAM ON TEAM.teamID = MAIN_PARTICIPANT.teamID
        WHERE TEAM.teamID = :teamID
    )
    """)
    fun incrementWonTournamentsNumberOfMembersInTeam(teamID: String)
}

@Dao
interface TournamentTypeDao {
    @Query("SELECT * FROM TOURNAMENT_TYPE")
    fun getAll(): List<TournamentType>

    @Insert
    fun insertAll(vararg tournamentTypes: TournamentType)

    @Delete
    fun delete(tournamentType: TournamentType)
}

@Dao
interface MainProfileDao {
    @Query("SELECT * FROM MAIN_PROFILE")
    fun getAll(): List<MainProfile>

    @Query("SELECT * FROM MAIN_PROFILE WHERE email = :email")
    fun getProfileByEmail(email: String): MainProfile

    @Query("SELECT MAIN_PROFILE.password FROM MAIN_PROFILE WHERE email = :email")
    fun checkPassword(email: String): String

    @Query("""SELECT COUNT(*) 
        FROM MAIN_PROFILE 
        WHERE email = :email;
        """)
    fun checkEmail(email: String): Int

    @Insert
    fun insert(mainProfiles: MainProfile)

    @Delete
    fun delete(mainProfile: MainProfile)

    @Upsert
    suspend fun upsert(mainProfile: MainProfile)
}

@Dao
interface GuestProfileDao {
    @Query("SELECT * FROM GUEST_PROFILE")
    fun getAll(): List<GuestProfile>

    @Query("SELECT * FROM GUEST_PROFILE WHERE username = :username")
    fun getFromUsername(username: String): GuestProfile

    @Insert
    fun insert(guestProfile: GuestProfile)

    @Delete
    fun delete(guestProfile: GuestProfile)
}

@Dao
interface TeamDao {
    @Query("SELECT * FROM TEAM")
    fun getAll(): List<Team>

    @Query("SELECT * FROM TEAM WHERE teamID = :teamID")
    fun getTeamsFromTeamInTm(teamID: String): List<Team>

    @Insert
    fun insertAll(teams: List<Team>)

    @Insert
    fun insert(team: Team)

    @Delete
    fun delete(teams: Team)
}

@Dao
interface TeamInTmDao {
    @Insert
    suspend fun insert(teamInTm: TeamInTm)

    @Query("SELECT * FROM TEAM_IN_TM")
    suspend fun getAll(): List<TeamInTm>

    @Query("SELECT * FROM TEAM_IN_TM WHERE teamID = :teamID AND matchTmID = :matchTmID")
    suspend fun findByID(teamID: String, matchTmID: String): TeamInTm

    @Query("SELECT * FROM TEAM_IN_TM WHERE matchTmID = :matchTmID")
    fun getTeamsInTmFromMatch(matchTmID: String): List<TeamInTm>

    @Update
    fun updateTeamInTms(teamInTMs: List<TeamInTm>)

    @Insert
    fun insertAll(vararg teamInTm: TeamInTm)

    @Delete
    suspend fun delete(teamInTm: TeamInTm)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM NOTIFICATION WHERE email = :email")
    fun getNotificationsByEmail(email: String): LiveData<List<Notification>>

    @Delete
    fun removeNotification(notificationID: Notification)
}
