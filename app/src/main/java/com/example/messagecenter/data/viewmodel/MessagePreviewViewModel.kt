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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/*
1.spilt page data loading
2.ui state management

TODO:
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
    
    private var contactSize = 0
    private val pageSize = 20

    private var isLoading = false
    private val loadSize = MutableStateFlow(20)

    init{
        viewModelScope.launch{
            contactSize = contactRepository.getContactCount()
        }
    }

    val uiState: StateFlow<MessageUiState> = loadSize
        .flatMapLatest { limit ->
            contactRepository.getContactsStream(limit)
        }
        .map { contacts ->
            val hasMore = contacts.size < contactSize
            MessageUiState.Success(contacts, hasMore = hasMore)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MessageUiState.Loading
        )

    fun refreshContact() {
        viewModelScope.launch{
            contactSize = contactRepository.getContactCount()
            loadSize.value = pageSize
        }
    }

    fun loadMoreContact() {
        if (loadSize.value < contactSize) {
            loadSize.value += pageSize
        }
    }

    fun markAsRead(contactId: Int) {
        viewModelScope.launch {
            contactRepository.markAsRead(contactId)
        }
    }

    fun deleteContact(contactId: Int) {
        viewModelScope.launch {
            contactRepository.deleteContact(contactId)
            contactSize = contactRepository.getContactCount()
        }
    }
}
