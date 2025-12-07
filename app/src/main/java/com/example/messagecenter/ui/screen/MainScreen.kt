package com.example.messagecenter.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.messagecenter.R
import kotlinx.coroutines.launch

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.viewmodel.MessageUiState
import com.example.messagecenter.data.viewmodel.SettingsViewModel
import com.example.messagecenter.navigation.Destination
import com.example.messagecenter.utils.NetworkConnectivityObserver
import com.example.messagecenter.utils.NoNetworkDialog


@Composable
fun MainScreen(
    contactViewModel: ContactViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val uiState by contactViewModel.uiState.collectAsState()
    var currentContacts by remember { mutableStateOf<List<ContactEntity>>(emptyList()) }
    var currentHasMore by remember { mutableStateOf(false) }

    val isNetworkAvailable by produceState(initialValue = true) {
        NetworkConnectivityObserver(context).observe().collect { value = it }
    }
    var showNoNetworkDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isNetworkAvailable) {
        if (!isNetworkAvailable) {
            showNoNetworkDialog = true
        }
    }
    LaunchedEffect(uiState, selectedDestination) {
        val state = uiState
        if (state is MessageUiState.Success) {
            if (state.contacts.isNotEmpty()) {
                if (state.contacts != currentContacts && listState.firstVisibleItemIndex == 0) {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
                Log.d("uistate", "非空的")
                currentContacts = state.contacts
                currentHasMore = state.hasMore
            } else {
                if (selectedDestination == 1) {
                    Toast.makeText(
                        context,
                        "当前没有消息记录",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                currentContacts = emptyList()
                currentHasMore = false
            }
        }
    }

    if (uiState is MessageUiState.Error && selectedDestination == 1) {
        val errorState = uiState as MessageUiState.Error
        Toast.makeText(
            context,
            "${errorState.exception.message}",
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
                    label = { Text(stringResource(Destination.HOME.label)) }
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
                    label = { Text(stringResource(Destination.MESSAGE.label)) }
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
                    label = { Text(stringResource(Destination.PROFILE.label)) }
                )
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding())) {
            when (selectedDestination) {
                0 -> HomeScreen()
                1 -> {
                    MessageScreen(
                        listState,
                        navController,
                        Modifier,
                        currentContacts,
                        currentHasMore,
                        isNetworkAvailable = isNetworkAvailable,
                        onShowNoNetworkDialog = { showNoNetworkDialog = true },
                        contactViewModel
                    )
                }
                2 -> SettingScreen(settingsViewModel, contactViewModel)
            }
            if(showNoNetworkDialog){
                NoNetworkDialog(
                    showNoNetworkDialog,
                    onDismiss = { showNoNetworkDialog = false }
                )
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
        var showDialog by remember { mutableStateOf(false) }
        Text(
            text = stringResource(R.string.home_screen),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
