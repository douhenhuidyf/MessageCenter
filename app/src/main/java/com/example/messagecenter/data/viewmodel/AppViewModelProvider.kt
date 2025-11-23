package com.example.messagecenter.data.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
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

    }
}

fun CreationExtras.messageCenterApplication(): MessageCenterApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MessageCenterApplication)