package com.application.controller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.application.controller.ui.theme.MDP_ControllerAppTheme
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_maze)
        setContent {
            MDP_ControllerAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)

                    )
                }
            }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    // Function to display GIF
    @Composable
    fun GifImage() {
        Image(
            painter = rememberAsyncImagePainter(model = R.drawable.catgif),
            contentDescription = "GIF Animation",
            modifier = Modifier
                .size(100.dp) // Adjust size as needed
        )
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MDP_ControllerAppTheme {
            Greeting("Android")
        }
    }

