package com.example.tournaMake.ui.screens.match

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tournaMake.data.models.ThemeEnum
import com.example.tournaMake.data.models.ThemeState
import com.example.tournaMake.sampledata.GuestProfile
import com.example.tournaMake.sampledata.MainProfile
import com.example.tournaMake.ui.screens.common.RectangleContainer
import com.example.tournaMake.ui.theme.ColorConstants
import com.example.tournaMake.ui.theme.getThemeColors

interface Team {
    fun getMainProfiles(): Set<MainProfile>
    fun getGuestProfiles(): Set<GuestProfile>
    fun getTeamName(): String
    fun setTeamName(name: String)
    fun addMainProfile(profile: MainProfile)
    fun addGuestProfile(profile: GuestProfile)
}

// TODO: send this team entities to database
// TODO: profiles need to be fetched from database
class TeamImpl(
    private var mainProfiles: Set<MainProfile>,
    private var guestProfiles: Set<GuestProfile>,
    private var teamName: String
) : Team {
    override fun getMainProfiles(): Set<MainProfile> {
        return this.mainProfiles
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

val testTeam1 = TeamImpl(
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
val testTeam2 = TeamImpl(
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
    teamsSet: Set<Team>,
    modifier: Modifier = Modifier,
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
                TeamElement(team, null, null)
                Spacer(modifier = Modifier.height(spacerHeight))
            }
        }
    }
}

@Composable
fun TeamElement(
    team: Team,
    backgroundColor: Color?,
    backgroundBrush: Brush?
) {
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
            TeamOutlinedTextField()
            ClickableTextLabel()
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
fun TeamOutlinedTextField() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
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
fun ClickableTextLabel() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* TODO: add member */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add team member",
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = "Add Members",
            onValueChange = {
                            // list di roba dal db
                            // filtra finché value = elementolista.name
                            // aggiungi il membro all'oggetto teamImpl
            },
            textStyle = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
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

@Preview
@Composable
fun MyTeamPreview() {
    val colorConstants: ColorConstants = getThemeColors(themeState = ThemeState(ThemeEnum.Light))
    TeamElement(
        team = TeamImpl(
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
        ),
        backgroundColor = null,
        backgroundBrush = colorConstants.getButtonBackground()
    )
}

@Preview
@Composable
fun MyTeamsPreview() {
    val teamsSet = setOf(
        TeamImpl(
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
        ),
        TeamImpl(
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
    )
    TeamContainer(teamsSet = teamsSet)
}