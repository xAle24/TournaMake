package com.example.tournaMake.activities.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text

class TestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var clicks by remember { mutableIntStateOf(0) }
            var set by remember { mutableStateOf(emptySet<String>()) }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Magenta)
            ) {
                Text("A", style = MaterialTheme.typography.headlineLarge)
                Text(
                    text = "Set content: $set"
                )
                Button(onClick = {
                    clicks++
                    set = setOf(set, setOf(clicks.toString())).flatten().toSet()
                }) {
                    Text("Click to increment")
                }
                MyComposable(callback = { set = it })
            }
        }
    }
}

// A, B, C composables; A contiene B che contiene C; il callback è passato da B a C e aggiorna B

@Composable
fun MyComposable(
    callback: (Set<String>) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val myNumber = 134
    Column(
        modifier = Modifier.background(Color.Yellow)
    ) {
        Text("B", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = { callback(setOf(myNumber.toString())) }) {
            Text("Other Composable")
        }
        Text(text, style = MaterialTheme.typography.headlineLarge)
        Button(onClick = { text = if (text.isEmpty()) "tralala" else "" }) {
            Text("Recompose Just me")
        }
        C(
            callback = {
                text = it
            },
            callbackA = callback
        )
    }
}

@Composable
fun C(
    callback: (String) -> Unit,
    callbackA: (Set<String>) -> Unit
) {
    Column(
        modifier = Modifier.background(Color.Blue)
    ) {
        var showAlert by remember { mutableStateOf(false) }
        Text("C", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = { callback("Chiamato da C") }) {
            Text("CALLBACK C")
        }
        Button(onClick = { callbackA(setOf("gino", "pino")) }) {
            Text("I am A's callback")
        }
        Button(onClick = { showAlert = true }) {
            Text("Show alert")
        }
        if (showAlert) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                text = {
                    Button(onClick = { callbackA(setOf("From Alert")) }) {
                        Text("Activate Callback A")
                    }
                },
                confirmButton = {
                    Button(onClick = { showAlert = false }) {
                        Text("Close Dialog")
                    }
                }
            )
        }
    }
}

