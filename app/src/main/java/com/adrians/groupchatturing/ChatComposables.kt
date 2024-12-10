package com.adrians.groupchatturing

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatScreen(viewModel: MainViewModel) {
    val roundDurationSec by viewModel.roundDurationSec.collectAsState()
    val topic by viewModel.gameTopic.collectAsState()
    Scaffold {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            Text(text = "Time left for discussion: $roundDurationSec")
            Text(text = "Discussion Topic: $topic")
            ChatMessages(
                viewModel = viewModel,
                activeUserId = viewModel.userId.collectAsState().value,
                onSendMessage = { messageString ->
                    viewModel.postMessage(messageString)
                }
            )
        }
    }
}

@Composable
fun ChatMessages(
    viewModel: MainViewModel,
    activeUserId: Int,
    onSendMessage: (String) -> Unit,
) {
    val messagesList by viewModel.chatMessagesList.collectAsState()
    val hideKeyboardController = LocalSoftwareKeyboardController.current
    val msg = remember {
        mutableStateOf("")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(messagesList) { message ->
                ChatBubble(message = message, activeUserId = activeUserId)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .background(Color.LightGray), verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = msg.value,
                onValueChange = { msg.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(text = "Type a message") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    hideKeyboardController?.hide()
                })
            )
            IconButton(onClick = {
                onSendMessage(msg.value)
                msg.value = ""
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMsg, activeUserId: Int) {
    val isCurrentUser = message.senderId == activeUserId
    val bubbleColor = if (isCurrentUser) {
        Color.Blue
    } else {
        Color.Gray
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        val alignment = if (!isCurrentUser) Alignment.CenterStart else Alignment.CenterEnd
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.align(alignment)
        ) {
            Text(
                text = message.senderNickname,  fontSize = 10.sp,
                color = Color.White, modifier = Modifier.padding(0.dp)
            )
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(color = bubbleColor, shape = RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = message.message, color = Color.White, modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}