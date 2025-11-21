@file:OptIn(
    ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class
)

package com.example.messagecenter

import android.annotation.SuppressLint
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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

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
    var selectIndex by rememberSaveable { mutableStateOf(0) }
    val lableList = AppDestinations.entries.map { it.label }
    val labels = AppDestinations.entries.map { it.icon }
    Scaffold(
        bottomBar = {
            NavigationBar{
                lableList.forEachIndexed { index, string ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = labels[index],
                                contentDescription = null
                            )
                        },
                        label = { Text(string) },
                        selected = selectIndex == index,
                        onClick = { selectIndex = index }
                    )
                }
            }
        },
    ) {
        when (selectIndex) {
            0 -> Text("HOME Screen", style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(it))
            1 -> MessagePage()
            2 -> Text("PROFILE Screen", style = MaterialTheme.typography.displayMedium)
            else -> Text("HOME Screen", style = MaterialTheme.typography.displayMedium)
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


@Preview
@Composable
fun MessageCenterAppPreview() {
    MessageCenterTheme{
        MessageCenterApp()
    }
}