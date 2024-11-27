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
fun VotingTable(users: List<User>, viewModel: MainViewModel, stateCallback: (Int) -> Unit)
{
    var userId by remember { mutableStateOf<String>("") }
    var showPopup by remember { mutableStateOf(false) }
    var showNewLayout by remember { mutableStateOf(false) }
    //TODO add value from server that will change when voting results come by
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Choose who is the imposter",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        users.forEach { user ->
            //TODO if user is client, then skip row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                //default values when userId == ""
                var icon: ImageVector = Icons.Default.RadioButtonUnchecked
                var isActive: Boolean = true
                if(userId != "" && userId == user.userId) //user is the one that was voted for
                {
                    icon = Icons.Default.RadioButtonChecked
                    isActive =  false
                }
                else if(userId != "")
                {
                    isActive =  false
                }
                Text(text = user.anonName, fontSize = 18.sp)
                IconButton(onClick = {
                    userId = user.userId
                    //And send to server TODO
                    },
                    enabled = isActive
                ) {
                        Icon(imageVector = icon, contentDescription = null)
                }
            }
        }
        if(userId != "")
            Text(
                text = "Waiting for all users to vote ...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Thin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        Button(onClick = {showPopup = true}){ Text(text = "Debug end vote")}
        if (showPopup) {
            SummaryDialog(users = users, onDismissCallback = {showPopup = false}, onConfirmCallback = {st -> showPopup = false;stateCallback(st) })
        }
    }
}

@Composable
fun SummaryDialog(users: List<User>, onDismissCallback: () -> Unit, onConfirmCallback: (Int) -> Unit)
{
    AlertDialog(
        onDismissRequest = { onDismissCallback() },
        title = { Text(text = "Vote Ended") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "The real impostor was: \n")//TODO add name
                Text(text = "Those people voted correctly (+1 point):")
                users.forEach { user -> Text(text = "${user.userName}\n") }
            }
        }, //TODO add names
        confirmButton = {
            Button(onClick = {
                onConfirmCallback(0)
                //TODO go to stuff
                //TODO TMP stuff
            }) {
                Text(text = "Continue")
            }
        }
    )
}