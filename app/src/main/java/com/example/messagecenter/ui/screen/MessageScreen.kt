@file:OptIn(
    ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class
)

package com.example.messagecenter.ui.screen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.viewmodel.MessageUiState
import com.example.messagecenter.data.viewmodel.AppViewModelProvider
import com.example.messagecenter.ui.component.MessagePageTopBar
import com.example.messagecenter.ui.component.MessagePreviewCell

@Composable
fun MessageScreen(modifier: Modifier = Modifier, viewModel : ContactViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { MessagePageTopBar() },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when(uiState) {
            is MessageUiState.Loading -> {}
            is MessageUiState.Success -> {
                val messagePreviewState = uiState as MessageUiState.Success
                MessagePreviewFlow(
                    modifier = Modifier,
                    contentPadding=innerPadding,
                    contacts = messagePreviewState.contacts,
                    hasMore = messagePreviewState.hasMore,
                    loadMore = { viewModel.loadMoreContact() },
                    refresh = { viewModel.refreshContact() }
                )
            }
            is MessageUiState.Error -> {
                val errorState = uiState as MessageUiState.Error
                Text("Error: ${errorState.exception.message}")
            }
        }
    }
}

@Composable
fun MessagePreviewFlow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    contacts: List<ContactEntity>,
    hasMore: Boolean,
    loadMore: () -> Unit,
    refresh: () -> Unit
){
    var refreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = {
            scope.launch {
                refreshing = true
                refresh()
                delay(750)
                refreshing = false
            }
        },
        modifier = modifier
            .fillMaxSize(),
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = contentPadding.calculateTopPadding()),
                isRefreshing = refreshing,
                state = state
            )
        }
    ) {
        LaunchedEffect(listState, contacts) {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo
            }.collect { visibleItems ->
                if (hasMore) {
                    val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: 0
                    val totalItems = contacts.size
                    if (lastVisibleIndex >= totalItems - 5) {
                        Log.d(
                            "MessagePreviewFlow",
                            "Loading more contacts, lastVisibleIndex: $lastVisibleIndex, totalItems: $totalItems"
                        )
                        loadMore()
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = contentPadding,
            state = listState
        ) {
            items(contacts, key = { it.contactId }) { contact ->
                MessagePreviewCell(contactEntity = contact)
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun MessageScreenPreview() {

    val contact = ContactEntity(
        id = 0,
        contactId = 1,
        contactName = "Test Contact",
        contactSureName = "Test Sure Name",
        contactAvatar = "",
        isFromSystem = true,
        previewText = "Test Preview Text1111111 1",
        timestamp = System.currentTimeMillis()-100000000,
        isRead = false,
        unReadNum = 191
    )
    MessagePreviewCell(contactEntity = contact)
}
