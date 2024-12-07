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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.adrians.groupchatturing.ui.theme.GroupChatTuringTheme
//import androidx.compose.ui.tooling.preview.Preview

//MVVM

//TODO only for testing
val usersTMP = listOf(User(userId = "11", userName = "Rel1", anonName = "Giraffe"),
    User(userId = "21", userName = "Rel2", anonName = "Pig"),
    User(userId = "31", userName = "Rel3", anonName = "Horse"))


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
//    var state by remember { mutableIntStateOf(0) }
    if (state == 0){
//        MenuScreen(mainViewModel, stateCallback = { st -> state = st })
        MenuScreen(mainViewModel, stateCallback = {})
    }
    else if(state == 1)
    {
        GroupChatTuringTheme {
            //            LobbyScreen(usersTMP, mainViewModel, stateCallback = { st -> state = st })
            LobbyScreen(usersTMP, mainViewModel, stateCallback = {})
        }
    }
    else if(state == 2)
    {
        GroupChatTuringTheme {
            Box(modifier = Modifier.fillMaxSize())
            {
//                ChatScreen(mainViewModel, stateCallback = { st -> state = st })
                ChatScreen(mainViewModel, stateCallback = {})
            }
        }
    }
    else if(state == 3)
    {
        GroupChatTuringTheme {
//            VotingTable(usersTMP, mainViewModel, stateCallback = { st -> state = st })
            VotingTable(usersTMP, mainViewModel, stateCallback = {})
        }
    }
}
