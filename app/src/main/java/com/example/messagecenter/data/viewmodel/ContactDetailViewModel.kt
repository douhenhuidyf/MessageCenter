package com.example.messagecenter.data.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactDetailUiState (
    val contactId: Int,
    val contactName: String,
    val contactSureName: String?,
    val contactAvatar: String,
    val isFromSystem : Boolean
)

class ContactDetailViewModel(
    private val contactRepository: ContactRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<ContactDetailUiState>(ContactDetailUiState( 
        contactId = 0,
        contactName = "",
        contactSureName = null,
        contactAvatar = "",
        isFromSystem = false
    ))
    private val contactId: Int = checkNotNull(savedStateHandle["contactId"])

    val uiState: StateFlow<ContactDetailUiState> = _uiState.asStateFlow()

    init {
        loadContactDetail()
    }

    fun loadContactDetail() {
        viewModelScope.launch {
            contactRepository.getContact(contactId).collect{contactEntity ->
                _uiState.value = ContactDetailUiState(
                    contactId = contactEntity.contactId,
                    contactName = contactEntity.contactName,
                    contactSureName = contactEntity.contactSureName,
                    contactAvatar = contactEntity.contactAvatar,
                    isFromSystem = contactEntity.isFromSystem
                )
            }
        }
    }

    fun updateSureName(newSureName: String) {
        viewModelScope.launch {
            contactRepository.updateSureName(contactId, newSureName)
             loadContactDetail()
        }
    }
}