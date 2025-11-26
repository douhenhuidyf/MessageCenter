package com.example.messagecenter


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

import com.example.messagecenter.ui.theme.MessageCenterTheme
import com.example.messagecenter.navigation.AppNavHost
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MessageCenterTheme {
                MessageCenterApp()
            }
        }
    }
}


@Composable
fun MessageCenterApp(){
    AppNavHost()
}