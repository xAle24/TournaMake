package com.example.tournaMake.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.ui.screens.common.BasicScreenWithTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileListScreen(
    state: ThemeState,
    profileListLiveData: LiveData<List<String>>,
    backButton: () -> Unit,
    navigateToProfile: () -> Unit
) {
    val profileNamesList = profileListLiveData.observeAsState()

    BasicScreenWithTheme(state = state) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { backButton() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(text = "Guest profile list") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navigateToProfile() }, modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(80.dp)
            ) {
                Text("My profile")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                if (profileNamesList.value != null) {
                    items(profileNamesList.value!!) { item ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Do something when button is clicked */ },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp)
                        ) {
                            Text(item)
                        }
                    }
                }
            }
        }
    }
}
