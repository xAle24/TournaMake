package com.example.tournaMake

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tournaMake.data.models.AchievementsProfileViewModel
import com.example.tournaMake.data.models.AuthenticationViewModel
import com.example.tournaMake.data.models.CoordinatesViewModel
import com.example.tournaMake.data.models.GamesListViewModel
import com.example.tournaMake.data.models.GraphViewModel
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.data.models.MatchDetailsViewModel
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.MatchViewModel
import com.example.tournaMake.data.models.NotificationViewModel
import com.example.tournaMake.data.models.ProfileListViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentCreationViewModel
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.data.models.TournamentListViewModel
import com.example.tournaMake.data.repositories.AuthenticationRepository
import com.example.tournaMake.data.repositories.MatchDetailsRepository
import com.example.tournaMake.data.repositories.MatchRepository
import com.example.tournaMake.data.repositories.ThemeRepository
import com.example.tournaMake.data.repositories.TournamentIDRepository
import com.example.tournaMake.sampledata.AppDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.UUID

val Context.dataStore
        by preferencesDataStore("theme")
val appModule = module {
    single { get<Context>().dataStore }
    single { ThemeRepository(get()) } // basically the call to get() means "get your parameters from the stuff koin has saved here"
    viewModel { ThemeViewModel(get()) }
    single { AuthenticationRepository(get()) }
    viewModel { AuthenticationViewModel(get()) }
    viewModel { ProfileViewModel() }
    viewModel { ProfileListViewModel() }
    viewModel { MatchListViewModel(get()) }
    viewModel { MatchCreationViewModel() }
    viewModel { GraphViewModel() }
    viewModel { GamesListViewModel() }
    viewModel { TournamentListViewModel() }
    viewModel { AchievementsProfileViewModel() }
    viewModel { TournamentCreationViewModel() }
    single { TournamentIDRepository(get()) }
    viewModel { TournamentIDViewModel(get()) }
    viewModel { TournamentDataViewModel() }
    viewModel { CoordinatesViewModel() }
    viewModel { NotificationViewModel() }
    viewModel { MatchDetailsViewModel(get()) }
    viewModel { MatchViewModel(get()) }
    single { MatchDetailsRepository(get()) }
    single { MatchRepository(get()) }
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "tournamake-database"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val uuids = generateSequence(0) { it -> it + 1 }
                    .take(3)
                    .map { UUID.randomUUID() }
                    .toList()
                db.execSQL("INSERT INTO ACHIEVEMENT (achievementID, name, description, imagePath) VALUES ('1', 'Welcome', 'Welcome in TournaMake', '');")
                db.execSQL("INSERT INTO TOURNAMENT_TYPE (tournamentTypeID, name, description) VALUES ('${uuids[0]}', 'Single Bracket', 'Tournament with single elimination bracket');")
                db.execSQL("INSERT INTO TOURNAMENT_TYPE (tournamentTypeID, name, description) VALUES ('${uuids[1]}', 'Double Bracket', 'Tournament with double elimination bracket');")
                db.execSQL("INSERT INTO GAME(gameID, name, favorites, description, duration, minPlayers, maxPlayers) VALUES ('${uuids[2]}', 'Pallavolo', 0, '6 vs 6 game', 30, 12, 24);")
                db.execSQL("CREATE TRIGGER IF NOT EXISTS create_achievement_player AFTER INSERT ON MAIN_PROFILE BEGIN INSERT INTO ACHIEVEMENT_PLAYER (achievementID, status, email) VALUES ('1', '1', NEW.email); END;")
                db.execSQL("CREATE TRIGGER IF NOT EXISTS create_notification AFTER INSERT ON MAIN_PROFILE BEGIN INSERT INTO NOTIFICATION (notificationID, description, email) VALUES (NEW.email, 'You have completed the Welcome achievement!', NEW.email); END;")
            }
        }).build()
    }

}