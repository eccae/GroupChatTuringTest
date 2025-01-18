package com.adrians.groupchatturing

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen(mainViewModel: MainViewModel, isDarkTheme: Boolean, onThemeChange: () -> Unit)
{
        Box(modifier = Modifier.fillMaxSize()) {
            SettingsButton(
                mainViewModel,
                isDarkTheme,
                onThemeChange,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            )
            {
                Text(
                    text = "\nTuring Test\nbut in\nGroup Chat!\n",
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.surface),
                )
                CreateRoomButton(mainViewModel = mainViewModel)
                JoinByCodeButton(mainViewModel = mainViewModel)
            }
        }
}
@Composable
fun JoinByCodeButton(mainViewModel: MainViewModel) {
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
            Button(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun CreateRoomButton(mainViewModel: MainViewModel) {
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
fun SettingsButton(mainViewModel: MainViewModel, isDarkTheme: Boolean, onThemeChange: () -> Unit, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    val userName by mainViewModel.userName.collectAsState()
    val port by mainViewModel.serverPortV2R.collectAsState()
    val ip by mainViewModel.serverIpV2R.collectAsState()
    val prefix by mainViewModel.serverPrefix.collectAsState()

    FloatingActionButton(onClick = { showDialog = true; mainViewModel.pullCurrentServerAddress() },
        modifier = modifier)
    {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
    }
    if (showDialog)
    {
        SettingsDialog(
            username = userName, ip = ip, port = port, prefix = prefix, isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange,
            onDismiss = { showDialog = false },
            onConfirm = { name, _port, _ip, _prefix ->
                showDialog = false
                mainViewModel.setUsername(name)
                mainViewModel.setServerIpV2R(_ip)
                mainViewModel.setServerPortV2R(_port)
                mainViewModel.setServerPrefix(_prefix)
            })
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (MutableMap<String, Int>) -> Unit
) {
    var maxRoundsNumber by remember { mutableIntStateOf(2) }
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
            Button(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun SettingsDialog(username: String,
                   ip: String,
                   port: String,
                   prefix: String,
                   isDarkTheme: Boolean,
                   onThemeChange: () -> Unit,
                   onDismiss: () -> Unit,
                   onConfirm: (String, String, String, String) -> Unit) {
    var newUsername by remember { mutableStateOf(username) }
    var newPort by remember { mutableStateOf(port) }
    var newIpAddress by remember { mutableStateOf(ip) }
    var prefixRadioOption by remember { mutableStateOf(prefix) }
    val prefixOptions = listOf("ws://", "wss://", "http://", "https://", "")
    var themeRadioOption by remember { mutableStateOf(if (isDarkTheme) "Dark" else "Light") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Settings & Debug")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPort,
                    onValueChange = { newPort = it },
                    singleLine = true,
                    label = { Text("Port") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newIpAddress,
                    singleLine = true,
                    onValueChange = { newIpAddress = it },
                    label = { Text("IP Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    prefixOptions.forEach { option ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            RadioButton(
                                selected = (prefixRadioOption == option),
                                onClick = { prefixRadioOption = option }
                            )
                            Text(text = option)
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        RadioButton(
                            selected = (themeRadioOption == "Light"),
                            onClick = { themeRadioOption = "Light"; onThemeChange() }
                        )
                        Text(text = "Light")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        RadioButton(
                            selected = (themeRadioOption == "Dark"),
                            onClick = { themeRadioOption = "Dark"; onThemeChange() }
                        )
                        Text(text = "Dark")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newUsername.isNotBlank() && newPort.isNotBlank() && newIpAddress.isNotBlank()) {
                    onConfirm(newUsername, newPort, newIpAddress, prefixRadioOption)
                } else {
                    println("Invalid input")
                }
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text("Cancel")
            }
        }
    )
}
