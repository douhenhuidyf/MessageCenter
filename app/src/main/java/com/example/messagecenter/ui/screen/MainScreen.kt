package com.example.messagecenter.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.messagecenter.data.repository.ContactEntity

import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.viewmodel.MessageUiState
import com.example.messagecenter.data.viewmodel.SettingsViewModel
import com.example.messagecenter.navigation.Destination


@Composable
fun MainScreen(
    contactViewModel: ContactViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }

     val uiState by contactViewModel.uiState.collectAsState()

     var availableContacts by remember { mutableStateOf<List<ContactEntity>>(emptyList()) }
     var availableHasMore by remember { mutableStateOf(false) }

     if (uiState is MessageUiState.Success) {
         val successState = uiState as MessageUiState.Success
         availableContacts = successState.contacts
         availableHasMore = successState.hasMore
     }
    else if (uiState is MessageUiState.Error && selectedDestination == 1) {
        val errorState = uiState as MessageUiState.Error
        Toast.makeText(
            LocalContext.current,
            "Error: ${errorState.exception.message}",
            Toast.LENGTH_SHORT
        ).show()
    }

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
                1 -> MessageScreen(navController, Modifier, availableContacts, availableHasMore, contactViewModel)
                2 -> SettingScreen(settingsViewModel)
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
        Text(
            "Home screen",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
