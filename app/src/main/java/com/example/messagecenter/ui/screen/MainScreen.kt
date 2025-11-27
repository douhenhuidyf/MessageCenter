package com.example.messagecenter.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController

import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.navigation.Destination


@Composable
fun MainScreen(
    contactViewModel: ContactViewModel,
    navController: NavController
) {
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = selectedDestination == 0,
                    onClick = { selectedDestination = 0 },
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
                    onClick = { selectedDestination = 1 },
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
                    onClick = { selectedDestination = 2 },
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
        Box(modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding())) {
            when (selectedDestination) {
                0 -> HomeScreen()
                1 -> MessageScreen(navController, viewModel = contactViewModel)
                2 -> ProfileScreen()
            }
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