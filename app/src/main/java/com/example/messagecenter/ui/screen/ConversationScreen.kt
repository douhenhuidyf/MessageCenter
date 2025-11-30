package com.example.messagecenter.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.MessageEntity
import com.example.messagecenter.data.viewmodel.ContactDetailViewModel
import com.example.messagecenter.data.viewmodel.ConversationViewModel
import com.example.messagecenter.data.viewmodel.MockMessage
import com.example.messagecenter.ui.component.Avatar
import com.example.messagecenter.ui.component.MessagePreviewCell
import com.example.messagecenter.ui.theme.MessageCenterTheme

@Composable
fun ConversationScreen(
    contactId:Int,
    navController: NavController,
    contactViewModel : ContactDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    conversationViewModel: ConversationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
){
    val contactUiState by contactViewModel.uiState.collectAsState()
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
    
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            navController.popBackStack()
                        }
                    ),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = contactName,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            Log.d("ConversationScreen", "进入联系人信息页: $contactName - $contactId")
                            val route = "contact/${contactId}"
                            navController.navigate(route)
                        }
                    ),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
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
                .padding(horizontal = 8.dp)
                .padding(top = innerPadding.calculateTopPadding()),
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(conversations) { message ->
                val isMe = message.senderName != contactName
                ConversationCell(
                    messageEntity = message,
                    contactAvatar = contactAvatar,
                    isMe = isMe
                )
            }
        }

    }
}

@Composable
fun ConversationCell(
    messageEntity: MessageEntity,
    contactAvatar: String,
    isMe: Boolean,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            Avatar(
                avatarPath = contactAvatar,
                size = 40.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.widthIn(max = screenWidth * 0.75f)
        ) {
            Text(
                text = messageEntity.messageText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                color = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        if (isMe) {
            Spacer(modifier = Modifier.width(8.dp))
            Avatar(avatarPath = "", size = 30.dp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ConversationCellPreview() {
    MessageCenterTheme() {
        ConversationCell(
            messageEntity = MessageEntity(
                conversationId = 1,
                senderName = "张三",
                receiverName = "李四",
                messageText = "你好hhh111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111",
                timestamp = System.currentTimeMillis()
            ),
            contactAvatar = "",
            isMe = false
        )
    }
}
