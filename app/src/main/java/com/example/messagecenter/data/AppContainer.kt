package com.example.messagecenter.data

import android.content.Context

import com.example.messagecenter.data.repository.ContactRepository
import com.example.messagecenter.data.repository.ContactsDatabase
import com.example.messagecenter.data.repository.MessageRepository
import com.example.messagecenter.data.repository.MessagesDatabase
import com.example.messagecenter.data.repository.PreferencesRepository


interface AppContainer {
    val contactRepository: ContactRepository
    val messageRepository: MessageRepository
    val preferencesRepository: PreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val contactRepository: ContactRepository = ContactRepository(ContactsDatabase.getDatabase(context).contactEntityDao())
    override val messageRepository: MessageRepository = MessageRepository( MessagesDatabase.getDatabase(context).messageEntityDao())
    override val preferencesRepository: PreferencesRepository = PreferencesRepository(context)
}


