package com.adrians.groupchatturing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.adrians.groupchatturing.ui.theme.GroupChatTuringTheme
//import androidx.compose.ui.tooling.preview.Preview

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
    when (state) {
        0 -> {
            MenuScreen(mainViewModel)
        }
        1 -> {
            GroupChatTuringTheme {
                LobbyScreen(mainViewModel)
            }
        }
        2 -> {
            GroupChatTuringTheme {
                Box(modifier = Modifier.fillMaxSize())
                {
                    ChatScreen(mainViewModel)
                }
            }
        }
        3 -> {
            GroupChatTuringTheme {
                VotingTable(mainViewModel)
            }
        }
    }
}
