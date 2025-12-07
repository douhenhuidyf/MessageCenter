@file:OptIn(
    ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class
)

package com.example.messagecenter.ui.screen

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.ui.component.MessagePageTopBar
import com.example.messagecenter.ui.component.MessagePreviewCell

@Composable
fun MessageScreen(
    listState: LazyListState,
    navController: NavController,
    modifier: Modifier = Modifier,
    availableContacts: List<ContactEntity>,
    availableHasMore: Boolean,
    isNetworkAvailable: Boolean = true,
    onShowNoNetworkDialog: () -> Unit = {},
    viewModel: ContactViewModel
) {
    Scaffold(
        topBar = { MessagePageTopBar(navController) },
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MessagePreviewFlow(
                navController = navController,
                modifier = Modifier,
                contentPadding = innerPadding,
                contacts = availableContacts,
                hasMore = availableHasMore,
                listState = listState,
                loadMore = viewModel::loadMoreContact,
                refresh = viewModel::refreshContact,
                markAsRead = viewModel::markAsRead,
                deleteContact = viewModel::deleteContact,
                isNetworkAvailable = isNetworkAvailable,
                onShowNoNetworkDialog = onShowNoNetworkDialog
            )
        }
    }
}

@Composable
fun MessagePreviewFlow(
    navController: NavController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    contacts: List<ContactEntity>,
    hasMore: Boolean,
    listState: LazyListState,
    loadMore: () -> Unit,
    refresh: () -> Unit,
    markAsRead:  (Int) -> Unit,
    deleteContact: (Int) -> Unit,
    isNetworkAvailable: Boolean = true,
    onShowNoNetworkDialog: () -> Unit = {}
){
    var refreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = {
            if (!isNetworkAvailable) {
                refreshing = false
                onShowNoNetworkDialog()
            } else {
                scope.launch {
                    refreshing = true
                    refresh()
                    delay(350)
                    refreshing = false
                }
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
                    if (lastVisibleIndex >= totalItems - 10) {
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
                .padding(horizontal = 8.dp)
                .padding(top = contentPadding.calculateTopPadding()),
            state = listState
        ) {
            items(contacts, key = { it.contactId }) { contact ->
                MessagePreviewCell(
                    markAsRead = { markAsRead(contact.contactId) },
                    deleteContact = { deleteContact(contact.contactId) },
                    navController = navController,
                    contactEntity = contact,
                    highlightText = ""
                )
            }
        }
    }
}




//@Preview(showBackground = true)
//@Composable
//fun MessageScreenPreview() {
//    MessageCenterTheme() {val contact = ContactEntity(
//        id = 0,
//        contactId = 1,
//        contactName = "Test Contact",
//        contactSureName = "Test Sure Name",
//        contactAvatar = "",
//        isFromSystem = true,
//        previewText = "Test Preview Text1111111 1",
//        timestamp = System.currentTimeMillis()-100000000,
//        isRead = false,
//        unReadNum = 191
//    )
//        MessagePreviewCell(navController = navController, contactEntity = contact) }
//
//}
