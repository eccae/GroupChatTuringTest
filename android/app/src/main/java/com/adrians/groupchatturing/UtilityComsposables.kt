package com.adrians.groupchatturing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AddSpacers(padding: Dp = 8.dp)
{
    Spacer(modifier = Modifier.height(padding))
    HorizontalDivider(color = Color.Gray, thickness = 1.dp)
    Spacer(modifier = Modifier.height(padding))
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
            Text(msg)
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