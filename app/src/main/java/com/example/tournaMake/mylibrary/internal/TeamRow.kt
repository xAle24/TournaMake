package com.example.tournaMake.mylibrary.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tournaMake.mylibrary.displaymodels.BracketTeamDisplayModel
import com.example.tournaMake.mylibrary.ui.BracketColors

/**
 * Component to render a [team] inside of a [BracketMatchItem].
 */
@Composable
internal fun TeamRow(
    team: BracketTeamDisplayModel,
    colors: BracketColors.MatchColors,
) {
    val fontWeight: FontWeight? = if (team.isWinner) {
        FontWeight.Bold
    } else {
        null
    }

    Row(
        modifier = Modifier
            .background(Color.White)
    ) {
        Text(
            text = team.name,
            fontWeight = fontWeight,
            color = colors.contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(8.dp)
                .weight(1F),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = team.score,
            fontWeight = fontWeight,
            color = colors.contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(8.dp),
        )
    }
}
