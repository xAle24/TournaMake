package com.example.tournaMake.ui.screens.match

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.common.RectangleContainer
import com.example.tournaMake.ui.screens.tournament.FilteredProfiles
import com.example.tournaMake.ui.screens.tournament.ProfileUtils
import java.util.stream.Collectors

interface TeamUI {
    fun getMainProfiles(): Set<MainProfile>
    fun getGuestProfiles(): Set<GuestProfile>
    fun getTeamName(): String
    fun setTeamName(name: String)
    fun addMainProfile(profile: MainProfile)
    fun addGuestProfile(profile: GuestProfile)
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
}


// Composables

private val spacerHeight = 20.dp

val testTeam1 = TeamUIImpl(
    mainProfiles = setOf(
        MainProfile(
            email = "email1@gmail",
            username = "Alin",
            locationLatitude = 0f,
            locationLongitude = 0f,
            password = "",
            profileImage = "",
            wonTournamentsNumber = 0
        ),
        MainProfile(
            email = "email2@gmail",
            username = "Alessio",
            locationLatitude = 0f,
            locationLongitude = 0f,
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
            locationLatitude = 0f,
            locationLongitude = 0f,
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
    teamsSet: Set<TeamUI>,
    modifier: Modifier = Modifier,
    mainProfileList: List<MainProfile>,
    guestProfileList: List<GuestProfile>
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    RectangleContainer(
        modifier = modifier
            .height((0.4 * screenHeight).dp)
            //.background(MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(teamsSet.toList()) { team ->
                TeamElement(team, null, null, mainProfileList, guestProfileList)
                Spacer(modifier = Modifier.height(spacerHeight))
            }
        }
    }
}

@Composable
fun TeamElement(
    team: TeamUI,
    backgroundColor: Color?,
    backgroundBrush: Brush?,
    mainProfileList: List<MainProfile>,
    guestProfileList: List<GuestProfile>
) {
    var changeGuestState by remember { mutableStateOf(emptySet<GuestProfile>()) }
    var changeNameState by remember { mutableStateOf(team.getTeamName()) }
    var changeMainState by remember { mutableStateOf(emptySet<MainProfile>()) }
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
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(50.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(3.dp)
                    .border(BorderStroke(3.dp, MaterialTheme.colorScheme.onPrimary))
                    //.background(Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete team",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.height(spacerHeight))
            // Team name
            TeamOutlinedTextField(
                changeTeamName = {changeNameState = it}
            )
            ClickableTextLabel(team, mainProfileList,
                guestProfileList,
                changeMain = { changeMainState = it; Log.d("DEV", it.toString()) },
                changeGuest = { changeGuestState = it },
                changeName = { changeNameState = it },
                )
            HorizontalDivider(thickness = 2.dp)
            Spacer(Modifier.height(spacerHeight))
            // Creating the member bubbles
            // first MainProfiles
            team.getMainProfiles().forEach { profile ->
                TeamMemberBubble(teamMemberName = profile.username)
                Spacer(modifier = Modifier.height(spacerHeight))
            }
            // then GuestProfiles
            team.getGuestProfiles().forEach { profile ->
                TeamMemberBubble(teamMemberName = profile.username)
                Spacer(modifier = Modifier.height(spacerHeight))
            }
        }
    }
}

@Composable
fun TeamOutlinedTextField(changeTeamName: (String) -> Unit) {
    var currentText by remember { mutableStateOf("")}
    OutlinedTextField(
        value = currentText,
        onValueChange = {currentText = it; changeTeamName(it)},
        label = {
            Text(
                text = "Team Name",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        modifier = Modifier
            .fillMaxWidth(),
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
}

@Composable
fun ClickableTextLabel(team: TeamUI,
                       mainProfileList: List<MainProfile>,
                       guestProfileList: List<GuestProfile>,
                       changeMain: (Set<MainProfile>) -> Unit,
                       changeGuest: (Set<GuestProfile>) -> Unit,
                       changeName: (String) -> Unit,
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
            changeName = changeName,
        )
    }
    Button(onClick = { showDialog.value = true },
        modifier = Modifier
           .fillMaxWidth(0.9f)
    ) {
        Text("Add member", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun TeamMemberBubble(
    teamMemberName: String
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
            text = teamMemberName,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.weight(0.1f))
        DeleteTeamMemeberButton(onDelete = {})
        Spacer(modifier = Modifier.weight(0.02f))
    }
}

@Composable
fun DeleteTeamMemeberButton(
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
    changeName: (String) -> Unit,
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
                            Log.d("DEV", "${team.getMainProfiles()}");
                            team.addMainProfile(item);
                            changeMain(team.getMainProfiles());
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