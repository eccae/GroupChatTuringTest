package com.adrians.groupchatturing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adrians.groupchatturing.ui.theme.GroupChatTuringTheme
//import androidx.compose.ui.tooling.preview.Preview

//MVVM
class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView(mainViewModel)
        }
    }
}

/*
* STATES
* 0 - Menu (goes to 1)
* 1 - Lobby (goes to 0, 1)
* 2 - Chat (goes to 3, 0 on error)
* 3 - Game summary (goes to 0, 2)
* */
//@Preview(showBackground = true)
@Composable
fun MainView(mainViewModel: MainViewModel) {
    val state by mainViewModel.uiState.collectAsState()
    var isDarkTheme by remember { mutableStateOf(true) }
    val isDebugOn = false
    GroupChatTuringTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            when (state) {
                0 -> {
                    MenuScreen(mainViewModel, isDarkTheme) { isDarkTheme = !isDarkTheme }
                }

                1 -> {
                    LobbyScreen(mainViewModel)
                }

                2 -> {
                    Box(modifier = Modifier.fillMaxSize())
                    {
                        ChatScreen(mainViewModel)
                    }
                }

                3 -> {
                    VotingTable(mainViewModel)
                }
            }
            DebugButtonForceChangeViews(mainViewModel, isDebugOn)
        }
    }
}

@Composable
fun DebugButtonForceChangeViews(mainViewModel: MainViewModel, isDebugOn: Boolean)
{
    var isDebugViewsOn by remember { mutableStateOf(false) }
    Box {
        if (isDebugOn && !isDebugViewsOn) {
            FloatingActionButton(
                onClick = { isDebugViewsOn = true },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                containerColor = Color.LightGray
            ) {
                Icon(
                    Icons.Default.DeveloperBoard,
                    contentDescription = "Debug mode",
                    tint = Color.White
                )
            }
        } else if (isDebugOn && isDebugViewsOn) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                repeat(4) { index ->
                    Button(
                        onClick = { mainViewModel.debugForceView(index) },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("$index")
                    }
                }
            }
        }
    }
}