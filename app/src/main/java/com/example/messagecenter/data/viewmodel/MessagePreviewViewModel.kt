package com.example.messagecenter.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.ContactRepository

/*
1.spilt page data loading
2.ui state management

TODO:
1.refresh page
2.delete message
 */


sealed class MessageUiState {
    object Loading : MessageUiState()
    data class Success(val contacts: List<ContactEntity>, val hasMore: Boolean) : MessageUiState()
    data class Error(val exception: Throwable) : MessageUiState()
}

class ContactViewModel(
    private val contactRepository: ContactRepository
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

            val result = contactRepository.getMoreContacts((currentPage - 1) * pageSize, pageSize)

            result.onSuccess { contacts ->
                val hasMore = contacts.size == pageSize
                if (refresh || currentPage == 1) {
                    _uiState.value = MessageUiState.Success(contacts, hasMore)
                } else {
                    val currentContacts = when (val state = _uiState.value) {
                        is MessageUiState.Success -> state.contacts
                        else -> emptyList()
                    }
                    _uiState.value = MessageUiState.Success(currentContacts + contacts, hasMore)
                }
                if (hasMore) {
                    currentPage++
                }
            }.onFailure { exception ->
                _uiState.value = MessageUiState.Error(exception)
            }
            isLoading = false
        }
    }

    fun refreshContact() {
        loadMessages(refresh = true)
    }

    fun loadMoreContact() {
        val currentState = _uiState.value
        if (currentState is MessageUiState.Success && currentState.hasMore) {
            loadMessages()
        }
    }
}
