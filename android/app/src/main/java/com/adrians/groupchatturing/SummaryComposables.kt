package com.adrians.groupchatturing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun VotingTable(viewModel: MainViewModel)
{
    var votedUserNickname by remember { mutableStateOf("") }
    val showPopup by viewModel.uiScoreboardState.collectAsState()
    val votingTimeSec by viewModel.votingTimeSec.collectAsState()
    val userList by viewModel.anonUserSet.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Choose who is the imposter!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Time left to vote: $votingTimeSec",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        AddSpacers(1.dp)
        userList.forEach { user ->
            if (user.userId != viewModel.userId.collectAsState().value){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    var icon: ImageVector = Icons.Default.RadioButtonUnchecked
                    var isActive = true
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
                text = "Waiting for vote to end ...",
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
    val roomData by viewModel.roomData.collectAsState()
    val roundNum by viewModel.roundNumber.collectAsState()
    Dialog(onDismissRequest = { })
    {
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Vote Ended",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(text = "The real impostor was: $botName", style = MaterialTheme.typography.bodyLarge)
                }
                AddSpacers()
                scores.forEach { score ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(text = "${score.username}'s score: ${score.points}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                AddSpacers()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(text = "Rounds left: ${(roomData["roundsNumber"]?:0) - roundNum}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}