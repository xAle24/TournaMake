package com.example.tournaMake.ui.screens.match

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.common.RectangleContainer
import com.example.tournaMake.ui.screens.tournament.FilteredProfiles
import com.example.tournaMake.ui.screens.tournament.ProfileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.stream.Collectors

interface TeamUI {
    fun getMainProfiles(): Set<MainProfile>
    fun getGuestProfiles(): Set<GuestProfile>
    fun getTeamName(): String
    fun setTeamName(name: String)
    fun addMainProfile(profile: MainProfile)
    fun removeMainProfile(profile: MainProfile)
    fun addGuestProfile(profile: GuestProfile)
    fun removeGuestProfile(profile: GuestProfile)
}


// TODO: send this team entities to database
// TODO: profiles need to be fetched from database

class TeamUIImpl(
    private var mainProfiles: Set<MainProfile>,
    private var guestProfiles: Set<GuestProfile>,
    private var teamName: String
) : TeamUI {
    override fun getMainProfiles(): Set<MainProfile> {
        return this.mainProfiles.stream().collect(Collectors.toSet())
    }

    override fun getGuestProfiles(): Set<GuestProfile> {
        return this.guestProfiles
    }

    override fun getTeamName(): String {
        return this.teamName
    }

    override fun setTeamName(name: String) {
        this.teamName = name
    }

    override fun addGuestProfile(profile: GuestProfile) {
        this.guestProfiles = setOf(this.guestProfiles, setOf(profile)).flatten().toSet()
    }

    override fun addMainProfile(profile: MainProfile) {
        this.mainProfiles = setOf(this.mainProfiles, setOf(profile)).flatten().toSet()
    }

    override fun removeMainProfile(profile: MainProfile) {
        this.mainProfiles = this.mainProfiles.filter { it != profile }.toSet()
    }

    override fun removeGuestProfile(profile: GuestProfile) {
        this.guestProfiles = this.guestProfiles.filter { it != profile }.toSet()
    }
}


// Composables

private val spacerHeight = 20.dp

val testTeam1 = TeamUIImpl(
    mainProfiles = setOf(
        MainProfile(
            email = "email1@gmail",
            username = "Alin",
            locationLatitude = 0.0,
            locationLongitude = 0.0,
            password = "",
            profileImage = "",
            wonTournamentsNumber = 0
        ),
        MainProfile(
            email = "email2@gmail",
            username = "Alessio",
            locationLatitude = 0.0,
            locationLongitude = 0.0,
            password = "",
            profileImage = "",
            wonTournamentsNumber = 0
        )
    ),
    guestProfiles = setOf(
        GuestProfile("Banana"),
        GuestProfile("Coconut")
    ),
    teamName = "The Pros"
)
val testTeam2 = TeamUIImpl(
    mainProfiles = setOf(
        MainProfile(
            email = "email3@gmail",
            username = "Lucrezia",
            locationLatitude = 0.0,
            locationLongitude = 0.0,
            password = "",
            profileImage = "",
            wonTournamentsNumber = 0
        )
    ),
    guestProfiles = setOf(
        GuestProfile("Kiwi"),
        GuestProfile("Peanut"),
        GuestProfile("Watermelon")
    ),
    teamName = "The Noobs"
)

@Composable
fun TeamContainer(
    teamsSetStateFlow: StateFlow<Set<TeamUI>>,
    modifier: Modifier = Modifier,
    mainProfileListFromDatabase: LiveData<List<MainProfile>>,
    guestProfileListFromDatabase: LiveData<List<GuestProfile>>,
    removeTeam: (TeamUI) -> Unit
) {
    val mainProfiles = mainProfileListFromDatabase.observeAsState()
    val guestProfiles = guestProfileListFromDatabase.observeAsState()
    val teamsSet by teamsSetStateFlow.collectAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp
    RectangleContainer(
        modifier = if (teamsSet.isNotEmpty())
            modifier
                .height((0.4 * screenHeight).dp)
        else
            modifier
        //.background(MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        if (teamsSet.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(teamsSet.toList()) { team ->
                    TeamElement(
                        team = team,
                        backgroundColor = null,
                        backgroundBrush = null,
                        mainProfileListFromDatabase = mainProfiles.value ?: emptyList(),
                        guestProfileListFromDatabase = guestProfiles.value ?: emptyList(),
                        removeTeam = removeTeam,
                    )
                    Spacer(modifier = Modifier.height(spacerHeight))
                }
            }
        }
    }
}

@Composable
fun TeamElement(
    team: TeamUI,
    backgroundColor: Color?,
    backgroundBrush: Brush?,
    mainProfileListFromDatabase: List<MainProfile>,
    guestProfileListFromDatabase: List<GuestProfile>,
    removeTeam: (TeamUI) -> Unit
) {
    var mainProfilesState by remember { mutableStateOf(emptySet<MainProfile>()) }
    var guestProfilesState by remember { mutableStateOf(emptySet<GuestProfile>()) }

    RectangleContainer(
        modifier = if (backgroundBrush != null) Modifier
            .background(backgroundBrush)
            .fillMaxWidth(0.9f)
        else if (backgroundColor != null) Modifier
            .background(backgroundColor)
            .fillMaxWidth(0.9f)
        else Modifier
            .background(MaterialTheme.colorScheme.tertiary)
            .fillMaxWidth(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Spacer(modifier = Modifier.height(spacerHeight))

            // Team name
            TeamOutlinedTextField(
                team = team
            )

            Spacer(modifier = Modifier.height(spacerHeight))

            /**
             * Here are the callbacks that should trigger recompositions.
             * */
            AddMemberButton(
                team,
                mainProfileListFromDatabase,
                guestProfileListFromDatabase,
                changeMain = { mainProfilesState = it },
                changeGuest = { guestProfilesState = it }
            )

            //HorizontalDivider(thickness = 2.dp)
            Spacer(Modifier.height(spacerHeight))

            // Creating the member bubbles
            // first MainProfiles
            key(mainProfilesState) {
                team.getMainProfiles().forEach { profile ->
                    TeamMainMemberBubble(teamMember = profile, team, { mainProfilesState = it })
                    Spacer(modifier = Modifier.height(spacerHeight))
                }
            }
            // then GuestProfiles
            key(guestProfilesState) {
                team.getGuestProfiles().forEach { profile ->
                    TeamGuestMemberBubble(teamMember = profile, team) { guestProfilesState = it }
                    Spacer(modifier = Modifier.height(spacerHeight))
                }
            }

            // Delete team button
            IconButton(
                onClick = { removeTeam(team) },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(50.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete team",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun TeamOutlinedTextField(team: TeamUI) {
    var currentText by remember { mutableStateOf(team.getTeamName()) }
    var bigDisplayedText by remember { mutableStateOf(currentText) }
    var shouldDisplayTextField by remember { mutableStateOf(bigDisplayedText.isEmpty()) }
    val focusManager = LocalFocusManager.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (bigDisplayedText.isNotEmpty()) {
            Text(
                text = bigDisplayedText,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.clickable {
                    shouldDisplayTextField = !shouldDisplayTextField
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        if (shouldDisplayTextField) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentText,
                    onValueChange = { currentText = it; team.setTeamName(it) },
                    label = {
                        Text(
                            text = "Team Name",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.85f),
                    placeholder = {
                        Text(
                            text = "Insert Team Name"
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        focusedBorderColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                IconButton(
                    onClick = {
                        bigDisplayedText = currentText
                        shouldDisplayTextField = currentText.isEmpty()
                        focusManager.clearFocus()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MyTeamTextField() {
    TeamOutlinedTextField(team = testTeam1)
}

@Composable
fun AddMemberButton(
    team: TeamUI,
    mainProfileList: List<MainProfile>,
    guestProfileList: List<GuestProfile>,
    changeMain: (Set<MainProfile>) -> Unit,
    changeGuest: (Set<GuestProfile>) -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }
    val profileUtils = ProfileUtils(mainProfileList, guestProfileList)
    if (showDialog.value) {
        ShowAddMember(
            openDialog = showDialog,
            onDismiss = { showDialog.value = false },
            filteredProfileList = profileUtils.filteredProfiles(""),
            team = team,
            changeMain = changeMain,
            changeGuest = changeGuest,
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center // to center the button in this row
    ) {
        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            Text("Add member", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun TeamMainMemberBubble(
    teamMember: MainProfile,
    team: TeamUI,
    changeMain: (Set<MainProfile>) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(0.7f)
                .padding(start = 20.dp),
            text = teamMember.username,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.weight(0.1f))
        DeleteTeamMemberButton(onDelete = {
            team.removeMainProfile(teamMember)
            changeMain(team.getMainProfiles())
        })
        Spacer(modifier = Modifier.weight(0.02f))
    }
}
@Composable
fun TeamGuestMemberBubble(
    teamMember: GuestProfile,
    team: TeamUI,
    changeGuest: (Set<GuestProfile>) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(0.7f)
                .padding(start = 20.dp),
            text = teamMember.username,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.weight(0.1f))
        DeleteTeamMemberButton(onDelete = {
            team.removeGuestProfile(teamMember)
            changeGuest(team.getGuestProfiles())
        })
        Spacer(modifier = Modifier.weight(0.02f))
    }
}

@Composable
fun DeleteTeamMemberButton(
    onDelete: () -> Unit
) {
    IconButton(
        onClick = onDelete,
        modifier = Modifier
            .size(25.dp)
            .clip(CircleShape),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Icon(Icons.Filled.Clear, null)
    }
}

@Composable
fun ShowAddMember(
    openDialog: MutableState<Boolean>,
    onDismiss: () -> Unit,
    filteredProfileList: FilteredProfiles,
    team: TeamUI,
    changeMain: (Set<MainProfile>) -> Unit,
    changeGuest: (Set<GuestProfile>) -> Unit,
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = "Select member to add:")
            },
            text = {
                LazyColumn {
                    items(filteredProfileList.mainProfiles) { item ->
                        Button(onClick = {
                            Log.d("DEV", "${team.getMainProfiles()}")
                            team.addMainProfile(item)
                            changeMain(team.getMainProfiles())
                            Log.d("DEV", "${team.getMainProfiles()}")
                        }) {
                            Text(text = item.username)
                        }
                    }
                    items(filteredProfileList.guestProfile) { item ->
                        Button(onClick = { team.addGuestProfile(item); changeGuest(team.getGuestProfiles()) }) {
                            Text(text = item.username)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
            }
        )
    }
}