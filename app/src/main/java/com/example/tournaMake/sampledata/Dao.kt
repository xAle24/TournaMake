package com.example.tournaMake.sampledata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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
    @Query("SELECT ACHIEVEMENT.*, ACHIEVEMENT_PLAYER.status, ACHIEVEMENT_PLAYER.email \n" +
            "FROM ACHIEVEMENT \n" +
            "INNER JOIN ACHIEVEMENT_PLAYER ON ACHIEVEMENT.achievementID = ACHIEVEMENT_PLAYER.achievementID \n" +
            "WHERE ACHIEVEMENT_PLAYER.email = :email\n")
    fun getAchievementsByEmail(email: String): List<AchievementResult>

    @Insert
    fun insertAll(vararg achievementPlayers: AchievementPlayer)

    @Delete
    fun delete(achievementPlayer: AchievementPlayer)
}
data class PlayedGame(val gameID: String,
                      val name: String,
                      val description : String,
                      val duration: Int,
                      val minPlayers: Int,
                      val maxPlayers: Int,
                      val timesPlayed: Int)

@Dao
interface GameDao {
    @Query("SELECT * FROM GAME")
    fun getAll(): List<Game>
    @Query("""
        SELECT GAME.*, COUNT(MATCH_TM.matchTmID) as timesPlayed
        FROM GAME
        JOIN MATCH_TM ON GAME.gameID = MATCH_TM.matchTmID
        JOIN TEAM ON MATCH_TM.matchTmID = TEAM.teamID
        JOIN MAIN_PARTICIPANT ON TEAM.teamID = MAIN_PARTICIPANT.teamID
        WHERE MAIN_PARTICIPANT.email = :email
        GROUP BY GAME.name;
    """)
    fun getPlayedGames(email: String): List<PlayedGame>

    @Query("SELECT * FROM GAME WHERE gameID = :gameID")
    fun getGameFromID(gameID: String): Game

    @Insert
    fun insertAll(vararg games: Game)

    @Delete
    fun delete(game: Game)
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM `MATCH_TM`")
    fun getAll(): List<MatchTM>

    @Query("""SELECT MATCH_TM.*
            FROM MATCH_TM
            JOIN TEAM_IN_TM ON MATCH_TM.matchTmID = TEAM_IN_TM.matchTmID
            JOIN MAIN_PARTICIPANT ON TEAM_IN_TM.teamID = MAIN_PARTICIPANT.teamID
            WHERE MAIN_PARTICIPANT.email = :email""")
    fun getMyMatches(email: String): List<MatchTM>

    @Query("""SELECT * FROM MATCH_TM WHERE favorites = '1'""")
    fun getFavoritesMatch(): List<MatchTM>

    @Query("""UPDATE MATCH_TM SET favorites = '1' WHERE matchTmID = :matchTmID""")
    fun setMatchFavorites(matchTmID: String)

    @Query("SELECT * FROM MATCH_TM WHERE matchTmID = :matchID")
    fun getMatchFromID(matchID: String): MatchTM

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
    val gameID: String,
    val tournamentID: String,
    val teamID: String,
    val name: String, // team name
    val isWinner: Char,
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

    @Query("""
    SELECT 
        MATCH_TM.matchTmID, 
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
    """)
    fun getMatchesAndTeamsFromTournamentID(tournamentID: String): List<TournamentMatchData>
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

    @Delete
    suspend fun delete(teamInTm: TeamInTm)
}
@Dao
interface NotificationDao {
    @Query("SELECT * FROM NOTIFICATION WHERE email = :email")
    fun getNotificationsByEmail(email: String): List<Notification>
}
