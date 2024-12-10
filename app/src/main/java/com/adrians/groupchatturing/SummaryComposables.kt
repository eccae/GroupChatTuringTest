package com.adrians.groupchatturing

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//two "layouts" - one for voting, second for summary after a match
//1st - text with instruction, list of users excluding user and buttons to vote for each one, after selecting one button,
// vote is locked and SEND to server, when receive response display who was the impostor, give points and reset to chat (need to randomise names)
// or go to summary
//popup - who was bad guy and list of users who got points (real usernames.
//2st - summary, winner after X rounds, points of everyone (real usernames), back button (goes to menu, resets connection to server, in effect - deletes room)

@Composable
fun VotingTable(viewModel: MainViewModel)
{
    var votedUserNickname by remember { mutableStateOf("") }
    val showPopup by viewModel.uiScoreboardState.collectAsState()
//    var showNewLayout by remember { mutableStateOf(false) }

    val votingTimeSec by viewModel.votingTimeSec.collectAsState()
    val userList by viewModel.anonUserList.collectAsState() //TMP

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Choose who is the imposter \n time to vote: $votingTimeSec",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        userList.forEach { user ->
            if (user.userId != viewModel.userId.collectAsState().value){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    var icon: ImageVector = Icons.Default.RadioButtonUnchecked
                    var isActive: Boolean = true
                    if(votedUserNickname == user.nickName)
                    {
                        icon = Icons.Default.RadioButtonChecked
                    }
                    if(votedUserNickname != "")
                    {
                        isActive =  false
                    }
                    Text(text = user.nickName, fontSize = 18.sp)
                    IconButton(onClick = {
                        votedUserNickname = user.nickName
                        viewModel.vote(votedUserNickname)
                        },
                        enabled = isActive
                    ) {
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
            }
        }
        if(votedUserNickname != "")
            Text(
                text = "Waiting for all users to vote ...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Thin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        if (showPopup) {
            SummaryDialog(viewModel)
        }
    }
}

@Composable
fun SummaryDialog(viewModel : MainViewModel)
{
    val scores by viewModel.scoreboardList.collectAsState()
    val botName by viewModel.currentBotNickname.collectAsState()
    AlertDialog(
        onDismissRequest = {  },
        title = { Text(text = "Vote Ended") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "The real impostor was: $botName")
//                Text(text = "Those people voted correctly (+1 point):")
                scores.forEach { score ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${score.username} : ${score.points}", fontSize = 18.sp)
                    }
                }
            }
        },
        confirmButton = {
//            Button(onClick = {
//                onConfirmCallback(0)
//                //TODO go to stuff
//                //TODO TMP stuff
//            }) {
//                Text(text = "Continue")
//            }
        }
    )
}