package com.adrians.groupchatturing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val userList by viewModel.anonUserList.collectAsState()

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
    Dialog(onDismissRequest = { }
//        onDismissRequest = {  },
//        title = { Text(text = "Vote Ended", modifier = Modifier.padding(8.dp)) },
//        text = {
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Text(text = "The real impostor was: $botName", modifier = Modifier.padding(8.dp))
//                scores.forEach { score ->
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(text = "${score.username} : ${score.points}", fontSize = 18.sp, fontWeight = FontWeight.Thin)
//                    }
//                }
//                Text(text = "Rounds left: ${(roomData["roundsNumber"]?:0) - roundNum}", modifier = Modifier.padding(8.dp))
//            }
//        },
//        confirmButton = {}
    )
    {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF35374B),
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
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFF344955), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(text = "The real impostor was: $botName", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                scores.forEach { score ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color(0xFF344955), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(text = "${score.username}'s score: ${score.points}", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFF344955), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(text = "Rounds left: ${(roomData["roundsNumber"]?:0) - roundNum}", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }
            }
        }
        }
}