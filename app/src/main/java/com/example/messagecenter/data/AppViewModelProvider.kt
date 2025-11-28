package com.example.messagecenter.data

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.AppDataContainer
import com.example.messagecenter.MessageCenterApplication


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ContactViewModel(
                messageCenterApplication().container.contactRepository
            )
        }
        initializer {
            _root_ide_package_.com.example.messagecenter.data.viewmodel.ContactDetailViewModel(
                messageCenterApplication().container.contactRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
    }
}

fun CreationExtras.messageCenterApplication(): MessageCenterApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MessageCenterApplication)