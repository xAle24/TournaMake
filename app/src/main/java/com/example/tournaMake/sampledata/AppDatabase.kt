package com.example.tournaMake.sampledata

import Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities =
[
    Achievement::class,
    AchievementPlayer::class,
    Team::class,
    Game::class,
    GuestParticipant::class,
    GuestProfile::class,
    MainProfile::class,
    MainParticipant::class,
    MatchTM::class,
    Tournament::class,
    TournamentType::class,
    TeamInTm::class,
    Notification::class
], version = 1)
@TypeConverters(Converters::class) // TODO verify if it works
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievementDao(): AchievementDao
    abstract fun achievementPlayerDao(): AchievementPlayerDao
    abstract fun gameDao(): GameDao
    abstract fun guestProfileDao(): GuestProfileDao
    abstract fun mainProfileDao(): MainProfileDao
    abstract fun matchDao(): MatchDao
    abstract fun guestParticipantsDao(): GuestParticipantsDao
    abstract fun mainParticipantsDao(): MainParticipantsDao
    abstract fun tournamentDao(): TournamentDao
    abstract fun tournamentTypeDao(): TournamentTypeDao
    abstract fun teamDao(): TeamDao
    abstract fun teamInTmDao(): TeamInTmDao
    abstract fun notificationDao(): NotificationDao

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