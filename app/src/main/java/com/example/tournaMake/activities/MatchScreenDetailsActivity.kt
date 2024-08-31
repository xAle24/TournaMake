package com.example.tournaMake.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tournaMake.sampledata.AppDatabase
import org.koin.android.ext.android.inject
class MatchScreenDetailsActivity: ComponentActivity() {
    private val appDatabase = inject<AppDatabase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}