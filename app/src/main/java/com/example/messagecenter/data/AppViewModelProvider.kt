package com.example.messagecenter.data

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.viewmodel.ContactDetailViewModel
import com.example.messagecenter.data.viewmodel.ConversationViewModel
import com.example.messagecenter.MessageCenterApplication
import com.example.messagecenter.data.viewmodel.SearchViewModel
import com.example.messagecenter.data.viewmodel.SettingsViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ContactViewModel(
                messageCenterApplication().container.contactRepository,
                messageCenterApplication().container.messageRepository
            )
        }
        initializer {
            ContactDetailViewModel(
                messageCenterApplication().container.contactRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        initializer {
            ConversationViewModel(
                messageCenterApplication().container.messageRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        initializer {
            SearchViewModel(
                messageCenterApplication().container.contactRepository,
                messageCenterApplication().container.messageRepository
            )
        }
        initializer {
            SettingsViewModel(
                messageCenterApplication().container.preferencesRepository
            )
        }
    }
}

fun CreationExtras.messageCenterApplication(): MessageCenterApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MessageCenterApplication)