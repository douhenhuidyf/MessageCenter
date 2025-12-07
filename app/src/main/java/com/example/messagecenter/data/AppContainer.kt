package com.example.messagecenter.data

import android.content.Context

import com.example.messagecenter.data.repository.ContactRepository
import com.example.messagecenter.data.repository.MessageRepository
import com.example.messagecenter.data.repository.PreferencesRepository
import com.example.messagecenter.data.AppDatabase


interface AppContainer {
    val contactRepository: ContactRepository
    val messageRepository: MessageRepository
    val preferencesRepository: PreferencesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val appDatabase = AppDatabase.getDatabase(context)

    override val contactRepository: ContactRepository = ContactRepository(appDatabase.contactDao())
    override val messageRepository: MessageRepository = MessageRepository( appDatabase.messageDao())
    override val preferencesRepository: PreferencesRepository = PreferencesRepository(context)
}


