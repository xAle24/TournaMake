package com.example.tournaMake.sampledata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AchievementDao {
    @Query("SELECT * FROM ACHIEVEMENT")
    fun getAll(): List<Achievement>

    @Insert
    fun insertAll(vararg achievements: Achievement)

    @Delete
    fun delete(achievement: Achievement)
}

@Dao
interface AchievementPlayerDao {
    @Query("SELECT * FROM ACHIEVEMENT_PLAYER")
    fun getAll(): List<AchievementPlayer>

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
        JOIN MAIN_PARTICIPANT_SCORE ON TEAM.teamID = MAIN_PARTICIPANT_SCORE.teamID
        WHERE MAIN_PARTICIPANT_SCORE.email = :email
        GROUP BY GAME.name;
    """)
    fun getPlayedGames(email: String): List<PlayedGame>


    @Insert
    fun insertAll(vararg games: Game)

    @Delete
    fun delete(game: Game)
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM `MATCH_TM`")
    fun getAll(): List<MatchTM>

    @Query("SELECT MATCH_TM.*\n" +
            "FROM MAIN_PROFILE\n" +
            "JOIN MAIN_PARTICIPANT_SCORE ON MAIN_PROFILE.email = MAIN_PARTICIPANT_SCORE.email\n" +
            "JOIN MATCH_TM ON MAIN_PARTICIPANT_SCORE.teamID = MATCH_TM.teamID\n" +
            "WHERE MAIN_PROFILE.email = :email;\n")
    fun getMyMatch(email: String): List<MatchTM>

    @Insert
    fun insertAll(vararg matches: MatchTM)

    @Delete
    fun delete(match: MatchTM)
}

@Dao
interface MatchScoreGuestDao {
    @Query("SELECT * FROM GUEST_PARTICIPANT_SCORE")
    fun getAll(): List<GuestParticipantScore>

    @Insert
    fun insertAll(vararg matchScoreGuests: GuestParticipantScore)

    @Delete
    fun delete(matchScoreGuest: GuestParticipantScore)
}

@Dao
interface MatchScoreMainDao {
    @Query("SELECT * FROM MAIN_PARTICIPANT_SCORE")
    fun getAll(): List<MainParticipantScore>

    @Insert
    fun insertAll(vararg matchScoreMains: MainParticipantScore)

    @Delete
    fun delete(matchScoreMain: MainParticipantScore)
}

@Dao
interface TournamentDao {
    @Query("SELECT * FROM TOURNAMENT")
    fun getAll(): List<Tournament>

    @Insert
    fun insertAll(vararg tournaments: Tournament)

    @Delete
    fun delete(tournament: Tournament)
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
    @Insert
    fun insert(mainProfiles: MainProfile)

    @Delete
    fun delete(mainProfile: MainProfile)
}

@Dao
interface GuestProfileDao {
    @Query("SELECT * FROM GUEST_PROFILE")
    fun getAll(): List<GuestProfile>

    @Insert
    fun insert(guestProfile: GuestProfile)

    @Delete
    fun delete(guestProfile: GuestProfile)
}
