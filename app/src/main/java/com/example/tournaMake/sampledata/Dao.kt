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

@Dao
interface GameDao {
    @Query("SELECT * FROM GAME")
    fun getAll(): List<Game>

    @Insert
    fun insertAll(vararg games: Game)

    @Delete
    fun delete(game: Game)
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM `MATCH`")
    fun getAll(): List<Match>

    @Query("SELECT MATCH.*\n" +
            "FROM MATCH\n" +
            "INNER JOIN MATCH_SCORE_MAIN\n" +
            "ON MATCH.matchID = MATCH_SCORE_MAIN.matchID\n" +
            "WHERE MATCH_SCORE_MAIN.email = :email")
    fun getMyMatch(email: String): List<Match>

    @Insert
    fun insertAll(vararg matches: Match)

    @Delete
    fun delete(match: Match)
}

@Dao
interface MatchScoreGuestDao {
    @Query("SELECT * FROM MATCH_SCORE_GUEST")
    fun getAll(): List<MatchScoreGuest>

    @Insert
    fun insertAll(vararg matchScoreGuests: MatchScoreGuest)

    @Delete
    fun delete(matchScoreGuest: MatchScoreGuest)
}

@Dao
interface MatchScoreMainDao {
    @Query("SELECT * FROM MATCH_SCORE_MAIN")
    fun getAll(): List<MatchScoreMain>

    @Insert
    fun insertAll(vararg matchScoreMains: MatchScoreMain)

    @Delete
    fun delete(matchScoreMain: MatchScoreMain)
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
