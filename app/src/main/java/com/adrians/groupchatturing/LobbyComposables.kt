package com.adrians.groupchatturing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LobbyScreen(users: List<User>, viewModel: MainViewModel, stateCallback: (Int) -> Unit)
{
    val lobbyName = "Test lobby" //TODO get from server
    val isLobbyOwner = true //TODO get from server
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Lobby $lobbyName, owner: ${users[0].userName}", //TODO set correct owner from server
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        users.forEach { user ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = user.userName, fontSize = 22.sp, color = Color.Green)
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(34.dp) // Space between buttons
        )
        {
            Button(onClick = { stateCallback(0) /*TODO add leaving lobby with server confirm*/ }) {
                Text(text = "Leave Lobby")
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            }
            Button(onClick = { stateCallback(2) /*TODO do starting game by server*/ }, enabled = isLobbyOwner) {
                Text(text = "Start Game")
                Icon(imageVector = Icons.Default.ChatBubble, contentDescription = null)
            }
        }
    }
}
