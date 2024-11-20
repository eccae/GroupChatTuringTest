package com.adrians.groupchatturing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adrians.groupchatturing.ui.theme.GroupChatTuringTheme

//MVVM

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroupChatTuringTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun DisplayText(str: String, modifier: Modifier = Modifier) {
    Text(
        text = str,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
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
                mainViewModel.getJoinCode(jc)
            }
        )
    }
}

@Composable
fun JoinInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Enter Join Code")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                BasicTextField(
                    value = textState,
                    textStyle = TextStyle(color = Color.White),
                    onValueChange = { textState = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            if (textState.text.isEmpty()) {
                                Text("Type code here", style = TextStyle(color = Color.White))//MaterialTheme.typography.bodyMedium,)
                            }
                            innerTextField()
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(textState.text) }) {
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
fun CreateRoomButton(mainViewModel: MainViewModel, modifier: Modifier = Modifier, callback: () -> Unit = {}) {
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    Button(onClick = {showCreateRoomDialog=true},//onClick = callback,
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
                mainViewModel.saveRoomData(dataDict)
                callback()
            })
    }
}

@Composable
fun SettingsButton(mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    Button(onClick = { showDialog = true },
        modifier = modifier)
    {
        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
    }
    if (showDialog)
    {
        SettingsDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name ->
            showDialog = false
            mainViewModel.saveUsername(name)
        })
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onConfirm: (Map<String, String>) -> Unit
) {
    var textNameState by remember { mutableStateOf(TextFieldValue("")) }
    //TODO max player number, number of rounds etc

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Provide name for Room")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                BasicTextField(
                    value = textNameState,
                    textStyle = TextStyle(color = Color.White),
                    onValueChange = { textNameState = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            if (textNameState.text.isEmpty()) {
                                Text("Type room name here", style = TextStyle(color = Color.White))
                            }
                            innerTextField()
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(mapOf("NAME" to textNameState.text)) }) {
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
fun SettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Enter Your Name")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                BasicTextField(
                    value = textState,
                    textStyle = TextStyle(color = Color.White),
                    onValueChange = { textState = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            if (textState.text.isEmpty()) {
                                Text("Type your name here", style = TextStyle(color = Color.White))
                            }
                            innerTextField()
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(textState.text) }) {
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

//@Preview(showBackground = true)
@Composable
fun MainView(mainViewModel: MainViewModel) {
    var state by remember { mutableIntStateOf(0) }
    if (state == 0){
        GroupChatTuringTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            )
            {
                SettingsButton(mainViewModel,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                )
                Column (verticalArrangement=Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)//Modifier.fillMaxSize()
                )
                {
                    DisplayText("Turing Test\nbut in\nGroup Chat!")
                    CreateRoomButton(mainViewModel = mainViewModel, callback = {state=1}) //State has no effect now TODO
                    JoinByCodeButton(mainViewModel = mainViewModel)
                }
            }
        }
    }
    else if(state == 1)
    {
        GroupChatTuringTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            )
            {
                SettingsButton(mainViewModel,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                )
                Column (verticalArrangement=Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)//Modifier.fillMaxSize()
                )
                {
                    DisplayText("You are now in lobby")
                    Button(onClick = {state=0}) {
                        Text(text = "BACK")
                    }
                }
            }
        }
    }
    }
