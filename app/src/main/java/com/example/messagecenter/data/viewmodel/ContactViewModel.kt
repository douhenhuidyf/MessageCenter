package com.example.messagecenter.data.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.ContactRepository
import com.example.messagecenter.data.repository.MessageEntity
import com.example.messagecenter.data.repository.MessageRepository

@Serializable
data class MockMessage(
    val conversationId: Int,
    val contactName: String,
    val isFromSystem: Boolean,
    val senderId: Int,
    val receiverId: Int,
    val messageText: String,
    val timestampOffset: Long,
)

data class GrowthStats(
    val totalUnread: Int = 0,
    val totalMessages: Int = 0,
    val systemMessagesReceived: Int = 0,
    val systemMessagesRead: Int = 0
) {
    val ctr: Float
        get() = if (totalMessages == 0) 0f else (totalMessages - totalUnread).toFloat() / totalMessages

    val recallRate: Float
        get() = if (systemMessagesReceived == 0) 0f else systemMessagesRead.toFloat() / systemMessagesReceived
}

sealed class MessageUiState {
    object Loading : MessageUiState()
    data class Success(val contacts: List<ContactEntity>, val hasMore: Boolean) : MessageUiState()
    data class Error(val exception: Throwable) : MessageUiState()
}

class ContactViewModel(
    private val contactRepository: ContactRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MessageUiState>(MessageUiState.Loading)
    val newMessageFlow = contactRepository.newMessageFlow

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
            Log.d("ContactViewModel", "loadSize ${loadSize.value} contacts")
            val hasMore = contacts.size < totalCount
            MessageUiState.Success(contacts, hasMore)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MessageUiState.Loading
        )

    val growthStats: StateFlow<GrowthStats> = combine(
        contactRepository.getTotalUnreadCountFlow(),
        messageRepository.getTotalMessageCountFlow(),
        contactRepository.getSystemUnreadCountFlow(),
        contactRepository.getSystemMessageCountFlow()
    ) { totalUnread, totalMessages, systemUnread, systemTotal ->
        GrowthStats(
            totalUnread = totalUnread,
            totalMessages = totalMessages,
            systemMessagesReceived = systemTotal,
            systemMessagesRead = (systemTotal - systemUnread).coerceAtLeast(0)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GrowthStats()
    )

    fun refreshContact() {
        viewModelScope.launch {
            Log.d("ContactViewModel", "refreshContact")
            loadSize.value = pageSize
            refreshTrigger.value += 1
        }
    }

    fun loadMoreContact() {
        Log.d(
            "ContactViewModel",
            "loadMore: loadSize=${loadSize.value}, total=${contactSize.value}"
        )
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

    fun deleteAllContacts() {
        viewModelScope.launch {
            contactRepository.deleteAllContacts()
            loadSize.value = 20
            refreshContact()
        }
    }

    fun deleteAllMessages() {
        viewModelScope.launch {
            messageRepository.deleteAllMessages()
            Log.d("ConversationViewModel", "delete all messages")
            refreshContact()
        }
    }

    fun setViewingStatus(contactId: Int, isViewing: Boolean) {
        if (isViewing) {
            viewModelScope.launch {
                contactRepository.setCurrentViewingId(contactId)
                contactRepository.markAsRead(contactId)
            }
        } else {
            contactRepository.setCurrentViewingId(null)
        }
    }

    fun insertMockData(context: Context) {
        viewModelScope.launch {
            val nowTimestamp = System.currentTimeMillis()
            val jsonString =
                context.assets.open("mock_data/mock_messages.json").bufferedReader()
                    .use { it.readText() }
            val mockMessages = Json.decodeFromString<List<MockMessage>>(jsonString)
            val messages = mockMessages.map { message ->
                MessageEntity(
                    conversationId = message.conversationId,
                    senderId = message.senderId,
                    receiverId = message.receiverId,
                    messageText = message.messageText,
                    timestamp = nowTimestamp - message.timestampOffset,
                )
            }
            messageRepository.insertMessages(messages)

            val imageStorageManager: ImageStorageManager = ImageStorageManager(context)
            val contacts = mockMessages.map { message ->
                val imagePath = try {
                    val inputStream =
                        context.assets.open("mock_data/mock_avatars/avatar_${message.conversationId - 1}.jpg")
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageStorageManager.saveContactImage(bitmap, message.conversationId)
                } catch (e: Exception) {
                    Log.e(
                        "AppDataContainer",
                        "Failed to load avatar for ${message.conversationId}",
                        e
                    )
                    ""
                }
                ContactEntity(
                    contactId = message.conversationId,
                    contactName = message.contactName,
                    contactSureName = "",
                    contactAvatar = imagePath,
                    isFromSystem = message.isFromSystem,
                    previewText = message.messageText,
                    timestamp = nowTimestamp - message.timestampOffset,
                    unReadNum = 0
                )
            }
            contactRepository.insertContacts(contacts)
            contactRepository.deleteContact(90)
            contactRepository.deleteContact(91)
            contactRepository.deleteContact(92)
            contactRepository.deleteContact(93)
        }
    }
}


class ImageStorageManager(private val context: Context) {

    private fun getImageStorageDir(): File {
        return File(context.filesDir, "contact_images").apply {
            if (!exists()) mkdirs()
        }
    }

    fun saveContactImage(bitmap: Bitmap, contactId: Int): String {
        val imageFile = File(getImageStorageDir(), "contact_$contactId.jpg")

        FileOutputStream(imageFile).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return imageFile.absolutePath
    }
}