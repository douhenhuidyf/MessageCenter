package com.example.messagecenter.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


import com.example.messagecenter.data.viewmodel.AppViewModelProvider
import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.ui.screen.ContactScreen
import com.example.messagecenter.ui.screen.ConversationScreen
import com.example.messagecenter.ui.screen.MainScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val contactViewModel: ContactViewModel = viewModel(factory = AppViewModelProvider.Factory)

    NavHost(
        navController,
        startDestination = "main_screen"
    ) {
        composable("main_screen") {
            MainScreen(contactViewModel, navController)
        }
        composable(
            Destination.CONVERSATION.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.IntType }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            val argument = requireNotNull(it.arguments)
            val contactId = argument.getInt("contactId")
            ConversationScreen(contactId, navController)
        }
        composable(
            Destination.CONTACT.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.IntType },
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) {
            val argument = requireNotNull(it.arguments)
            ContactScreen( navController)
        }
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
    CONVERSATION("conversation/{contactId}", "Conversation", Icons.Default.Person, "conversation"),
    CONTACT("contact/{contactId}", "Contact",  Icons.Default.Person, "contact")
}