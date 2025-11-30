package com.example.messagecenter.data.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

import com.example.messagecenter.data.repository.MessageRepository
import com.example.messagecenter.data.repository.MessageEntity

data class ConversationUiState(
    val data: List<MessageEntity> = emptyList(),
    val hasMore: Boolean = false
)

class ConversationViewModel(
    private val messageRepository: MessageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<ConversationUiState>(
        ConversationUiState()
    )
    private val conversationId: Int = checkNotNull(savedStateHandle["contactId"])
    private var conversationSize = messageRepository.getMessageCount(conversationId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    private val pageSize = 20

    private val loadSize = MutableStateFlow(20)
    private val refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<ConversationUiState> = combine(
        loadSize.flatMapLatest { limit ->
            messageRepository.getMessages(conversationId)
        },
        conversationSize,
        refreshTrigger
    ) { messages, totalCount, _ ->
        if (messages.isEmpty() && totalCount == 0) {
            ConversationUiState(data = emptyList(),hasMore = false)
        } else {
            Log.d("ConversationViewModel", "loadSize ${loadSize.value} messages")
            val hasMore = messages.size < totalCount
            ConversationUiState(messages, hasMore)
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ConversationUiState()
    ) as StateFlow<ConversationUiState>


    fun refreshConversation() {
        viewModelScope.launch{
            Log.d("ConversationViewModel", "refreshConversation")
            loadSize.value = pageSize
            refreshTrigger.value += 1
        }
    }

    fun loadMoreConversation() {
        Log.d("ConversationViewModel", "loadMore: loadSize=${loadSize.value}, total=${conversationSize.value}")
        if (loadSize.value < conversationSize.value) {
            loadSize.value += pageSize
        }
    }

}
