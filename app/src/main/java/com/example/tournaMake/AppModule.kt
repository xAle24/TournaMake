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
import com.example.tournaMake.data.models.MatchListViewModel
import com.example.tournaMake.data.models.NotificationViewModel
import com.example.tournaMake.data.models.ProfileListViewModel
import com.example.tournaMake.data.models.ProfileViewModel
import com.example.tournaMake.data.models.ThemeViewModel
import com.example.tournaMake.data.models.TournamentCreationViewModel
import com.example.tournaMake.data.models.TournamentDataViewModel
import com.example.tournaMake.data.models.TournamentIDViewModel
import com.example.tournaMake.data.models.TournamentListViewModel
import com.example.tournaMake.data.repositories.AuthenticationRepository
import com.example.tournaMake.data.repositories.ThemeRepository
import com.example.tournaMake.data.repositories.TournamentIDRepository
import com.example.tournaMake.sampledata.AppDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

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
    viewModel { MatchListViewModel() }
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
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "tournamake-database"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO ACHIEVEMENT (achievementID, name, description, imagePath, achievementsPlayerID) VALUES ('1', 'Welcome', 'Welcome in TournaMake', '', '1');")
                db.execSQL("CREATE TRIGGER IF NOT EXISTS create_achievement_player AFTER INSERT ON MAIN_PROFILE BEGIN INSERT INTO ACHIEVEMENT_PLAYER (achievementsPlayerID, achievementID, status, email) VALUES (NEW.email, '1', 'C', NEW.email); END;")
                db.execSQL("CREATE TRIGGER IF NOT EXISTS create_notification AFTER INSERT ON MAIN_PROFILE BEGIN INSERT INTO NOTIFICATION (notificationID, description, email) VALUES (NEW.email, 'You have completed the Welcome achievement!', NEW.email); END;")
            }
        }).build()
    }

}