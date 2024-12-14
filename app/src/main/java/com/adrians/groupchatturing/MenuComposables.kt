package com.adrians.groupchatturing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adrians.groupchatturing.ui.theme.GroupChatTuringTheme

@Composable
fun MenuScreen(mainViewModel: MainViewModel)
{
    GroupChatTuringTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            SettingsButton(
                mainViewModel,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)//Modifier.fillMaxSize()
            )
            {
                Text(
                    text = "Turing Test\nbut in\nGroup Chat!",
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBFBFBF),
                    lineHeight = 36.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically) // Center vertically
                        .shadow(4.dp, shape = RoundedCornerShape(8.dp)) // Add a subtle shadow
                )
                CreateRoomButton(mainViewModel = mainViewModel)
                JoinByCodeButton(mainViewModel = mainViewModel)
            }
        }
    }
}
@Composable
fun JoinByCodeButton(modifier: Modifier = Modifier, mainViewModel: MainViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    Button(onClick = { showDialog = true },
        modifier = Modifier.padding(vertical = 20.dp)) {
        Text(text = "Join by code",
            modifier = Modifier.padding(vertical = 20.dp))
        Icon(imageVector = Icons.Default.DoubleArrow, contentDescription = null)
    }
    if (showDialog) {
        JoinInputDialog(
            onDismiss = { showDialog = false },
            onConfirm = { jc ->
                showDialog = false
                mainViewModel.joinLobby(jc)
            }
        )
    }
}

@Composable
fun JoinInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var joinCode by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Join Lobby as Player")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = joinCode,
                    onValueChange = { joinCode = it },
                    label = { Text("Join Code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val joinCodeAsInt = joinCode.toIntOrNull() ?: 0
                onConfirm(joinCodeAsInt) }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun CreateRoomButton(mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    Button(onClick = {showCreateRoomDialog=true},
        modifier = Modifier.padding(vertical = 20.dp))
    {
        Text(text = "Create room",
            modifier = Modifier.padding(vertical = 20.dp))
        Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = null)
    }
    if(showCreateRoomDialog)
    {
        CreateRoomDialog(
            onDismiss = { showCreateRoomDialog = false },
            onConfirm = { dataDict ->
                showCreateRoomDialog = false
                mainViewModel.setRoomDataAndCreateRoom(dataDict)
            })
    }
}

@Composable
fun SettingsButton(mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    val userName by mainViewModel.userName.collectAsState()
    FloatingActionButton(onClick = { showDialog = true },
        modifier = modifier)
    {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
    }
    if (showDialog)
    {
        SettingsDialog(
            userName = userName,
            onDismiss = { showDialog = false },
            onConfirm = { name, port, ip ->
                showDialog = false
                mainViewModel.setUsername(name)
                mainViewModel.setServerIpV2R(ip)
                mainViewModel.setServerPortV2R(port)
            })
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (MutableMap<String, Int>) -> Unit
) {
    var maxRoundsNumber by remember { mutableIntStateOf(5) }
    var maxPlayersNumber by remember { mutableIntStateOf(5) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Create a Room")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = maxRoundsNumber.toFloat(),
                    onValueChange = { maxRoundsNumber = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(text = "Rounds: $maxRoundsNumber")
                Slider(
                    value = maxPlayersNumber.toFloat(),
                    onValueChange = { maxPlayersNumber = it.toInt() },
                    valueRange = 2f..10f,
                    steps = 7,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(text = "Max Players: $maxPlayersNumber")
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(mutableMapOf("maxUsers" to maxPlayersNumber, "roundsNumber" to maxRoundsNumber)) }) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun SettingsDialog(userName: String, onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var username by remember { mutableStateOf(userName) }
    var port by remember { mutableStateOf("12345") }
    var ipAddress by remember { mutableStateOf("192.168.0.94") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Settings & Debug")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("IP Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (username.isNotBlank() && port.isNotBlank() && ipAddress.isNotBlank()) {
                    onConfirm(username, port, ipAddress)
                } else {
                    println("Invalid input")
                }
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ErrorDialog(
    onDismiss: () -> Unit,
    msg: String
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "An error has occurred")
        },
        text = {
            Text(msg, style = TextStyle(color = Color.White))//MaterialTheme.typography.bodyMedium,)
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "I understood")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "I did not understood")
            }
        }
    )
}