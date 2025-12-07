package com.example.messagecenter.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.viewmodel.ContactDetailViewModel
import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.viewmodel.ConversationViewModel

import com.example.messagecenter.ui.component.ConversationBottomBar
import com.example.messagecenter.ui.component.ConversationCell
import com.example.messagecenter.ui.component.ConversationTopBar
import com.example.messagecenter.workers.MessageResponseWorker

@Composable
fun ConversationScreen(
    contactId:Int,
    navController: NavController,
    contactViewModel: ContactViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contactDetailViewModel : ContactDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    conversationViewModel: ConversationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val contactUiState by contactDetailViewModel.uiState.collectAsState()
    Log.d("ConversationScreen", "已进对话页: $contactUiState.contactName - $contactId")
    val contactName = if (contactUiState.contactSureName.isNullOrEmpty()) {
        contactUiState.contactName
    } else {
        contactUiState.contactSureName ?: ""
    }
    val contactAvatar = contactUiState.contactAvatar

    val conversationUiState by conversationViewModel.uiState.collectAsState()
    val conversations = conversationUiState.data
    val hasMore = conversationUiState.hasMore

    val listState = rememberLazyListState()

    var editeText by remember { mutableStateOf("") }
    val workManager = WorkManager.getInstance(LocalContext.current)

    DisposableEffect(contactId) {
        contactViewModel.setViewingStatus(contactId, true)
        onDispose {
            contactViewModel.setViewingStatus(contactId, false)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            ConversationTopBar(
                contactName = contactName,
                contactId = contactId,
                navController = navController
            )
        },
        bottomBar = {
            ConversationBottomBar(
                editeText,
                onValueChange = { editeText = it },
                onSend = {
                    if (editeText.isNotEmpty()) {
                        val inputData = workDataOf(
                            MessageResponseWorker.KEY_CONVERSATION_ID to contactId,
                            MessageResponseWorker.KEY_SENDER_ID to 0,
                            MessageResponseWorker.KEY_RECEIVER_ID to contactId,
                            MessageResponseWorker.KEY_MESSAGE_TEXT to editeText
                        )
                        editeText = ""
                        val request = OneTimeWorkRequestBuilder<MessageResponseWorker>()
                            .setInputData(inputData)
                            .addTag(MessageResponseWorker.WORK_NAME)
                            .build()
                        workManager.enqueue(request)
                    }
                },
               modifier = Modifier.imePadding()
            )
        }
    ){ innerPadding ->
        LaunchedEffect(listState, conversations) {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo
            }.collect { visibleItems ->
                if (hasMore) {
                    val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: 0
                    val totalItems = conversations.size
                    if (lastVisibleIndex >= totalItems - 10) {
                        Log.d(
                            "MessagePreviewFlow",
                            "Loading more conversations, lastVisibleIndex: $lastVisibleIndex, totalItems: $totalItems"
                        )
                        conversationViewModel.loadMoreConversation()
                    }
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .consumeWindowInsets(innerPadding),
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(conversations) { message ->
                val isMe = message.senderId == 0
                ConversationCell(
                    messageEntity = message,
                    contactAvatar = contactAvatar,
                    isMe = isMe
                )
            }
        }

    }
}
