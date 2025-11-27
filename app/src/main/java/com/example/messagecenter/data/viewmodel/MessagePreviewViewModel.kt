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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/*
TODO:
1.delete message
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
    
    private var contactSize = contactRepository.getContactCountFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    private val pageSize = 20

    private val loadSize = MutableStateFlow(20)
    private val refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<MessageUiState> = combine(
        loadSize.flatMapLatest { limit ->
            contactRepository.getContactsStream(limit)
        },
        contactSize,
        refreshTrigger
    ) { contacts, totalCount, _ ->
        if (contacts.isEmpty() && totalCount == 0) {
            MessageUiState.Error(Exception("错误:无数据"))
        } else {
            val hasMore = contacts.size < totalCount
            MessageUiState.Success(contacts, hasMore = hasMore)
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MessageUiState.Loading
    )

    fun refreshContact() {
        viewModelScope.launch{
            Log.d("ContactViewModel", "refreshContact")
            loadSize.value = pageSize
            refreshTrigger.value += 1
        }
    }

    fun loadMoreContact() {
        Log.d("ContactViewModel", "loadMore: loadSize=${loadSize.value}, total=${contactSize.value}")
        if (loadSize.value < contactSize.value) {
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
        }
    }
}
