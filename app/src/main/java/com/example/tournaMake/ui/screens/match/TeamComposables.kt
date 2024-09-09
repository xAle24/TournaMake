package com.example.tournaMake.ui.screens.match

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.example.tournaMake.data.models.GuestProfileListViewModel
import com.example.tournaMake.data.models.MatchCreationViewModel
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.common.RectangleContainer
import com.example.tournaMake.ui.screens.tournament.FilteredProfiles
import com.example.tournaMake.ui.screens.tournament.ProfileUtils
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel
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
    override fun toString(): String
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

    override fun toString(): String {
        val profiles = StringBuilder("${this.teamName}: {")
        this.mainProfiles.map { it -> it.username }.forEach { profiles.append("$it, ") }
        this.guestProfiles.map { it -> it.username }.forEach { profiles.append("$it, ") }
        profiles.append("}")
        return profiles.toString()
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

/**
 * A team container is just the rectangle drawn in the middle of the screen,
 * containing the team elements.
 * */
@Composable
fun TeamContainer(
    removeTeam: (TeamUI) -> Unit
) {
    val matchCreationViewModel = koinViewModel<MatchCreationViewModel>()
    val guestListViewModel = koinViewModel<GuestProfileListViewModel>()
    val mainProfiles = matchCreationViewModel.mainProfiles.observeAsState()
    val guestProfiles = guestListViewModel.guestProfileListLiveData.observeAsState()
    val teamsSet by matchCreationViewModel.teamsSet.observeAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp
    RectangleContainer(
        modifier = if (teamsSet != null && teamsSet!!.isNotEmpty())
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxHeight(0.8f)
        else
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .height(0.dp)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        if (teamsSet != null && teamsSet!!.isNotEmpty()) {
            key(teamsSet) {
                LazyColumn(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(teamsSet!!.toList()) { team ->
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
}

/**
 * A team element represents an individual team, with all of the
 * profiles involved as teammates.
 * */
@Composable
fun TeamElement(
    team: TeamUI,
    backgroundColor: Color?,
    backgroundBrush: Brush?,
    mainProfileListFromDatabase: List<MainProfile>,
    guestProfileListFromDatabase: List<GuestProfile>,
    removeTeam: (TeamUI) -> Unit
) {
    val vm = koinViewModel<MatchCreationViewModel>()
    val teamName by remember { derivedStateOf { team.getTeamName() } }
    /**
     * Locally selected profiles contain all profiles that are part
     * of THIS specific team.
     * */
    var locallySelectedMains by remember {
        mutableStateOf(team.getMainProfiles())
    }
    var locallySelectedGuests by remember {
        mutableStateOf(team.getGuestProfiles())
    }

    RectangleContainer(
        modifier = if (backgroundBrush != null) Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundBrush)
            .fillMaxWidth(0.9f)
        else if (backgroundColor != null) Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .fillMaxWidth(0.9f)
        else Modifier
            .clip(RoundedCornerShape(20.dp))
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
                teamName = teamName,
                setTeamName = { team.setTeamName(it) /* boh non posso cambiare il derived*/ }
            )

            Spacer(modifier = Modifier.height(spacerHeight))

            AddMemberButton(
                team,
                mainProfileListFromDatabase,
                guestProfileListFromDatabase,
                addMain = {
                    vm.addMain(it)
                    team.addMainProfile(it)
                    locallySelectedMains = team.getMainProfiles()
                },
                addGuest = {
                    vm.addGuest(it)
                    team.addGuestProfile(it)
                    locallySelectedGuests = team.getGuestProfiles()
                },
            )

            //HorizontalDivider(thickness = 2.dp)
            Spacer(Modifier.height(spacerHeight))

            // Creating the member bubbles
            // first MainProfiles
            locallySelectedMains.forEach { profile ->
                TeamMainMemberBubble(teamMember = profile, removeMain = {
                    vm.removeMain(it)
                    team.removeMainProfile(it)
                    locallySelectedMains = team.getMainProfiles()
                })
                Spacer(modifier = Modifier.height(spacerHeight))
            }
            // then GuestProfiles
            locallySelectedGuests.forEach { profile ->
                TeamGuestMemberBubble(teamMember = profile, removeGuest = {
                    vm.removeGuest(it)
                    team.addGuestProfile(it)
                    locallySelectedGuests = team.getGuestProfiles()
                })
                Spacer(modifier = Modifier.height(spacerHeight))
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
fun TeamOutlinedTextField(
    teamName: String,
    setTeamName: (String) -> Unit
) {
    Log.d("DEV-TEAM-NAME", "In team outlined text field, name = $teamName")
    var currentText by remember { mutableStateOf(teamName) }
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
                },
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        if (shouldDisplayTextField) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = currentText,
                    onValueChange = { currentText = it; setTeamName(it) },
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
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
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

@Composable
fun AddMemberButton(
    team: TeamUI,
    dbMains: List<MainProfile>,
    dbGuests: List<GuestProfile>,
    addMain: (MainProfile) -> Unit,
    addGuest: (GuestProfile) -> Unit,
) {
    val vm = koinViewModel<MatchCreationViewModel>()
    val globallySelectedMains by vm.selectedMainProfiles.observeAsState()
    val globallySelectedGuests by vm.selectedGuestProfiles.observeAsState()
    //Log.d("DEV-TEAMS-GLOBAL", "GloballySelectedMains: ${globallySelectedMains?.map { it.email }}")
    //Log.d("DEV-TEAMS-GLOBAL", "GloballySelectedGuests: ${globallySelectedGuests?.map { it.username }}")
    /**
     * The view model stores correctly the values of the globally selected profiles.
     * I hope this forces the Alert Dialog to keep track of them too.
     * */
    val unselectedMains = globallySelectedMains?.let { vm.filterUnselectedMainMembers(dbMains) } ?: dbMains
    val unselectedGuests = globallySelectedGuests?.let { vm.filterUnselectedGuestMembers(dbGuests) } ?: dbGuests
    //Log.d("DEV-TEAMS", "Unselected Mains: ${unselectedMains.map { it.email }}")
    //Log.d("DEV-TEAMS", "Unselected Guests: ${unselectedGuests.map { it.username }}")
    val showDialog = remember { mutableStateOf(false) }
    val profileUtils = ProfileUtils(unselectedMains, unselectedGuests)
    key(team.getMainProfiles(), team.getGuestProfiles()) {
        if (showDialog.value) {
            ShowAddMember(
                openDialog = showDialog,
                onDismiss = { showDialog.value = false },
                filteredProfileList = profileUtils.filteredProfiles(""),
                team = team,
                addMain = addMain,
                addGuest = addGuest,
            )
        }
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
    removeMain: (MainProfile) -> Unit
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
            removeMain(teamMember)
        })
        Spacer(modifier = Modifier.weight(0.02f))
    }
}

@Composable
fun TeamGuestMemberBubble(
    teamMember: GuestProfile,
    removeGuest: (GuestProfile) -> Unit
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
            removeGuest(teamMember)
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
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Icon(Icons.Filled.Clear, null)
    }
}

/**
 * The modal dialog that allows the user to select a new member
 * */
@Composable
fun ShowAddMember(
    openDialog: MutableState<Boolean>,
    onDismiss: () -> Unit,
    filteredProfileList: FilteredProfiles,
    team: TeamUI,
    addMain: (MainProfile) -> Unit,
    addGuest: (GuestProfile) -> Unit,
) {

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = "Select member to add:", color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                LazyColumn {
                    items(filteredProfileList.mainProfiles) { item ->
                        Button(onClick = {
                            Log.d("DEV", "${team.getMainProfiles()}")
                            addMain(item)
                            Log.d("DEV", "${team.getMainProfiles()}")
                        }) {
                            Text(text = item.username)
                        }
                    }
                    items(filteredProfileList.guestProfile) { item ->
                        Button(onClick = {
                            addGuest(item)
                        }) {
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