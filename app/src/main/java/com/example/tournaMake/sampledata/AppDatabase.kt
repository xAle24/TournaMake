package com.example.tournaMake.sampledata

import Converters
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Achievement::class, AchievementPlayer::class, Game::class, GuestProfile::class, MainProfile::class, Match::class, MatchScoreGuest::class, MatchScoreMain::class, Tournament::class, TournamentType::class], version = 1)
@TypeConverters(Converters::class) // TODO verify if it works
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievementDao(): AchievementDao
    abstract fun achievementPlayerDao(): AchievementPlayerDao
    abstract fun gameDao(): GameDao
    abstract fun guestProfileDao(): GuestProfileDao
    abstract fun mainProfileDao(): MainProfileDao
    abstract fun matchDao(): MatchDao
    abstract fun matchScoreGuestDao(): MatchScoreGuestDao
    abstract fun matchScoreMainDao(): MatchScoreMainDao
    abstract fun tournamentDao(): TournamentDao
    abstract fun tournamentTypeDao(): TournamentTypeDao

    /*companion object {
        @Volatile
        private var instance: AppDatabase? = null
        fun getDatabase(ctx: Context) = instance ?: synchronized(this) {
            instance = Room.databaseBuilder(
                ctx,
                AppDatabase::class.java,
                "tournamentDB"
            ).build()
            instance
        }
    }*/
}