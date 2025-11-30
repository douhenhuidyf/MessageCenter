package com.example.messagecenter.data.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.ContactRepository
import com.example.messagecenter.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val contactResults: List<ContactEntity> = emptyList(),
    val messageResults: List<ContactEntity> = emptyList(),
    val isLoading: Boolean = false
)


class SearchViewModel(
    private val contactRepository: ContactRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.update { SearchUiState() }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val contacts = contactRepository.searchContacts(query, 20)

            val messages = messageRepository.searchMessages(query, 20)

            val messageResults = if (messages.isNotEmpty()) {
                val contactIds = messages.map { it.conversationId }.distinct()
                val relatedContacts = contactRepository.getContactsByIds(contactIds)
                val contactMap = relatedContacts.associateBy { it.contactId }

                messages.mapNotNull { msg ->
                    val contact = contactMap[msg.conversationId]
                    if (contact != null) {
                        contact.copy(
                            previewText = msg.messageText,
                            timestamp = msg.timestamp,
                            unReadNum = 0
                        )
                    } else {
                        null
                    }
                }
            } else {
                emptyList()
            }

            _uiState.update {
                it.copy(
                    contactResults = contacts,
                    messageResults = messageResults,
                    isLoading = false
                )
            }
        }
    }
}