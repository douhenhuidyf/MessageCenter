package com.example.messagecenter.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.messagecenter.R
import kotlinx.coroutines.delay


import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.repository.NewMessage
import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.viewmodel.SettingsViewModel
import com.example.messagecenter.ui.screen.ContactScreen
import com.example.messagecenter.ui.screen.ConversationScreen
import com.example.messagecenter.ui.screen.MainScreen
import com.example.messagecenter.ui.screen.SearchScreen
import com.example.messagecenter.utils.NotificationBanner


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val contactViewModel: ContactViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)

    var showBanner by remember { mutableStateOf(false) }
    var newMessageData by remember { mutableStateOf<NewMessage?>(null) }

    LaunchedEffect(Unit) {
        contactViewModel.newMessageFlow.collect { event ->
            newMessageData = event
            showBanner = true
            delay(2500)
            showBanner = false
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController,
            startDestination = Destination.MAIN.route
        ) {
            composable(
                Destination.MAIN.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(600)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(600)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(600)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(600)
                    )
                }
            ) {
                MainScreen(contactViewModel, settingsViewModel, navController)
            }
            composable(
                Destination.CONVERSATION.route,
                arguments = listOf(
                    navArgument("contactId") { type = NavType.IntType }
                ),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(600)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(600)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(600)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(600)
                    )
                }
            ) {
                val argument = requireNotNull(it.arguments)
                val contactId = argument.getInt("contactId")
                ConversationScreen(contactId, navController, contactViewModel)
            }
            composable(
                Destination.CONTACT.route,
                arguments = listOf(
                    navArgument("contactId") { type = NavType.IntType },
                ),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(600)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(600)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(600)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(600)
                    )
                }
            ) {
                val argument = requireNotNull(it.arguments)
                ContactScreen(navController)
            }
            composable(
                Destination.SEARCH.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(600)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(600)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(600)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(600)
                    )
                }
            ) {
                SearchScreen(navController)
            }
        }
        NotificationBanner(
            visible = showBanner && "message" !in navController.currentDestination?.route ?: "",
            avatarPath = newMessageData?.avatarPath ?: "",
            contactName = newMessageData?.contactName ?: "",
            previewText = newMessageData?.previewText ?: "",
            onDismiss = { showBanner = false },
            onClick = {
                showBanner = false
                navController.navigate("conversation/${newMessageData?.contactId}")
            }
        )
    }
}

enum class Destination(
    val route: String,
    val label: Int,
    val icon: ImageVector,
    val contentDescription: String
) {
    MAIN("main_screen", R.string.home_screen, Icons.Default.Home, "main"),
    HOME("home", R.string.home_screen, Icons.Default.Home, "home"),
    MESSAGE("message_preview", R.string.message_record, Icons.Default.Email, "message_preview"),
    PROFILE("profile", R.string.profile, Icons.Default.Person, "profile"),
    CONVERSATION("conversation/{contactId}", R.string.conversation, Icons.Default.Person, "conversation"),
    CONTACT("contact/{contactId}", R.string.contact,  Icons.Default.Person, "contact"),
    SEARCH("search", R.string.search, Icons.Default.Search, "search")
}