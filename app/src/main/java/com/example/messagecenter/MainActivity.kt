@file:OptIn(
    ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class
)

package com.example.messagecenter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

import com.example.messagecenter.ui.theme.MessageCenterTheme

import com.example.messagecenter.presentation.page.MessagePage

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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun MessageCenterApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    MessageCenterTheme {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                it.icon,
                                contentDescription = it.label
                            )
                        },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
            MainScreen(currentDestination)
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("主页", Icons.Default.Home),
    MESSAGE("消息", Icons.Default.Email),
    PROFILE("我的", Icons.Default.AccountBox),
}

@Composable
fun MainScreen(currentDestination: AppDestinations) {

    when (currentDestination) {
        AppDestinations.HOME -> {
            Text("Home Screen", style = MaterialTheme.typography.displayMedium)
        }
        AppDestinations.MESSAGE -> {
            MessagePage()
        }
        AppDestinations.PROFILE -> {
            Text("PROFILE Screen", style = MaterialTheme.typography.displayMedium)
        }
    }
}


@Preview
@Composable
fun MessageCenterAppPreview() {
    MessageCenterTheme{
        MessageCenterApp()
    }
}