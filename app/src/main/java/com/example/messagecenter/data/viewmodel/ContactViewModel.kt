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

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.ContactRepository
import com.example.messagecenter.data.repository.MessageEntity
import com.example.messagecenter.data.repository.MessageRepository
import java.io.File
import java.io.FileOutputStream

/*
TODO:
1.delete message
*/

@Serializable
data class MockContact(
    val contactId: Int,
    val contactName: String,
    val contactSureName: String?,
    val isFromSystem: Boolean,
    val previewText: String,
    val timestamp: Long,
    val unReadNum: Int,
)
@Serializable
data class MockMessage(
    val conversationId : Int,
    val senderName: String,
    val receiverName: String,
    val messageText: String,
    val timestampOffset: Long,
)

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
            Log.d("ContactViewModel", "loadSize ${loadSize.value} contacts")
            val hasMore = contacts.size < totalCount
            MessageUiState.Success(contacts, hasMore)
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

    fun deleteAllContacts() {
        viewModelScope.launch {
            contactRepository.deleteAllContacts()
        }
    }

    fun insertMockContact(context: Context) {
        viewModelScope.launch {
            val imageStorageManager: ImageStorageManager = ImageStorageManager(context)
            val jsonString = context.assets.open("mock_data/mock_contacts.json").bufferedReader().use { it.readText() }
            val mockContacts = Json.decodeFromString<List<MockContact>>(jsonString)
            val contacts = mockContacts.map { mock ->
                val imagePath = try {
                    val inputStream = context.assets.open("mock_data/mock_avatars/avatar_${mock.contactId}.jpg")
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageStorageManager.saveContactImage(bitmap, mock.contactId)
                } catch (e: Exception) {
                    Log.e("AppDataContainer", "Failed to load avatar for ${mock.contactId}", e)
                    ""
                }
                ContactEntity(
                    contactId = mock.contactId,
                    contactName = mock.contactName,
                    contactSureName = mock.contactSureName ?: "",
                    contactAvatar = imagePath,
                    isFromSystem = mock.isFromSystem,
                    previewText = mock.previewText,
                    timestamp = System.currentTimeMillis() - mock.timestamp,
                    unReadNum = mock.unReadNum
                )
            }
            contactRepository.insertContacts(contacts)
        }
    }
    
    fun insertMockConversation(context: Context) {
        viewModelScope.launch {
            val jsonString = context.assets.open("mock_data/mock_messages.json").bufferedReader().use { it.readText() }
            val mockMessages = Json.decodeFromString<List<MockMessage>>(jsonString)
            val messages = mockMessages.map { mock ->
                MessageEntity(
                    conversationId = mock.conversationId,
                    senderName = mock.senderName,
                    receiverName = mock.receiverName,
                    messageText = mock.messageText,
                    timestamp = System.currentTimeMillis() - mock.timestampOffset,
                )
            }
            messageRepository.insertMessages(messages)
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