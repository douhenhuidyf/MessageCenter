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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.messagecenter.ui.screen.ContactScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

import com.example.messagecenter.ui.theme.MessageCenterTheme
import com.example.messagecenter.ui.screen.MessageScreen
import com.example.messagecenter.ui.screen.ConversationScreen

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
    val navController = rememberNavController()
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = selectedDestination == 0,
                    onClick = {
                        navController.navigate(route = Destination.HOME.route)
                        selectedDestination = 0
                    },
                    icon = {
                        Icon(
                            Destination.HOME.icon,
                            contentDescription = Destination.HOME.contentDescription
                        )
                    },
                    label = { Text(Destination.HOME.label) }
                )
                NavigationBarItem(
                    selected = selectedDestination == 1,
                    onClick = {
                        navController.navigate(route = Destination.MESSAGE.route)
                        selectedDestination = 1
                    },
                    icon = {
                        Icon(
                            Destination.MESSAGE.icon,
                            contentDescription = Destination.MESSAGE.contentDescription
                        )
                    },
                    label = { Text(Destination.MESSAGE.label) }
                )
                NavigationBarItem(
                    selected = selectedDestination == 2,
                    onClick = {
                        navController.navigate(route = Destination.PROFILE.route)
                        selectedDestination = 2
                    },
                    icon = {
                        Icon(
                            Destination.PROFILE.icon,
                            contentDescription = Destination.PROFILE.contentDescription
                        )
                    },
                    label = { Text(Destination.PROFILE.label) }
                )
            }
        }
    ) { contentPadding ->
        AppNavHost(navController, startDestination, modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding()))
    }
}

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    HOME("home", "Home", Icons.Default.Home, "home"),
    MESSAGE("message_preview", "Message", Icons.Default.Email, "message_preview"),
    PROFILE("profile", "Profile", Icons.Default.Person, "profile"),
    CONVERSATION("conversation/{contactId}/{contactName}/{contactAvatar}?contactSureName={contactSureName}", "Conversation", Icons.Default.Person, "conversation"),
    CONTACT("contact/{contactId}", "Contact",  Icons.Default.Person, "contact")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        composable(Destination.HOME.route) {
            HomeScreen(modifier)
        }
        composable(Destination.MESSAGE.route) {
            MessageScreen(navController, modifier)
        }
        composable(Destination.PROFILE.route) {
            ProfileScreen(modifier)
        }
        composable(
            Destination.CONVERSATION.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.IntType },
                navArgument("contactName") { type = NavType.StringType },
                navArgument("contactSureName") {
                    type = NavType.StringType
                    defaultValue = "" },
                navArgument("contactAvatar") { type = NavType.StringType }
            )
        ) {
            val argument = requireNotNull(it.arguments)
            val contactId = argument.getInt("contactId")
            val contactName = argument.getString("contactName", "")
            val contactSureName = argument.getString("contactSureName")
            var contactAvatar = argument.getString("contactAvatar", "")
            contactAvatar = URLDecoder.decode(contactAvatar, StandardCharsets.UTF_8.toString())
            ConversationScreen(contactId, contactName, contactSureName, contactAvatar, navController)
        }
        composable(
            Destination.CONTACT.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.IntType },
            )
        ) {
            val argument = requireNotNull(it.arguments)
            val contactId = argument.getInt("contactId")
            ContactScreen(contactId,  navController)
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home screen", textAlign = TextAlign.Center)
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile screen", textAlign = TextAlign.Center)
    }
}