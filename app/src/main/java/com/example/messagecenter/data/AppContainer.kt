package com.example.messagecenter.data

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import com.example.messagecenter.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.ContactRepository
import com.example.messagecenter.data.repository.ContactsDatabase
import com.example.messagecenter.data.repository.MessageRepository
import com.example.messagecenter.data.repository.MessagesDatabase



interface AppContainer {
    val contactRepository: ContactRepository
    val messageRepository: MessageRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val contactRepository: ContactRepository = ContactRepository(ContactsDatabase.getDatabase(context).contactEntityDao())
    override val messageRepository: MessageRepository = MessageRepository( MessagesDatabase.getDatabase(context).messageEntityDao())
}


