package com.example.messagecenter.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


import com.example.messagecenter.data.model.MessagePreview
import com.example.messagecenter.data.repository.MessagePreviewRepository

/*
1.spilt page data loading
2.ui state management

TODO:
1.refresh page
2.delete message
 */


sealed class MessageUiState {
    object Loading : MessageUiState()
    data class Success(val messages: List<MessagePreview>, val hasMore: Boolean) : MessageUiState()
    data class Error(val exception: Throwable) : MessageUiState()
}

class MessagePreviewViewModel(
    private val messagePreviewRepository: MessagePreviewRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MessageUiState>(MessageUiState.Loading)

    val uiState: StateFlow<MessageUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private val pageSize = 30
    private var isLoading = false

    init {
        loadMessages()
    }

    fun loadMessages(refresh : Boolean = false) {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true

            if (refresh) {
                currentPage = 1
                _uiState.value = MessageUiState.Loading
            }

            val result = messagePreviewRepository.getMessagePreviews(currentPage, pageSize)
            isLoading = false
            result.onSuccess { messages ->
                val hasMore = messages.size == pageSize
                if (refresh || currentPage == 1) {
                    _uiState.value = MessageUiState.Success(messages, hasMore)
                } else {
                    val currentMessages = when (val state = _uiState.value) {
                        is MessageUiState.Success -> state.messages
                        else -> emptyList()
                    }
                    _uiState.value = MessageUiState.Success(currentMessages + messages, hasMore)
                }
                if (hasMore) {
                    currentPage++
                }
            }.onFailure { exception ->
                _uiState.value = MessageUiState.Error(exception)
            }
        }
    }

    fun refreshMessagePreview() {
        loadMessages(refresh = true)
    }

    fun loadMoreMessagePreview() {
        val currentState = _uiState.value
        if (currentState is MessageUiState.Success && currentState.hasMore) {
            loadMessages()
        }
    }
}
