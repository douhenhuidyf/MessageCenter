package com.example.messagecenter.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

import com.example.messagecenter.MessageCenterApplication
import com.example.messagecenter.data.repository.MessageEntity
import com.example.messagecenter.data.repository.NewMessage
import java.io.File

@Serializable
data class incomeMessage(
    val contactName: String,
    val senderId: Int,
    val receiverId: Int,
    val messageText: String,
    val msgType: Int,
    val extraData : String? = null
)

class MessageDispatchWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val WORK_NAME = "message_dispatch_work"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString =
                    applicationContext.assets.open("received_data/income_messages.json")
                        .bufferedReader().use { it.readText() }
                val incomeMessages = Json.decodeFromString<List<incomeMessage>>(jsonString)

                val appContainer = (applicationContext as MessageCenterApplication).container
                val messageRepository = appContainer.messageRepository
                val contactRepository = appContainer.contactRepository
                val preferencesRepository = appContainer.preferencesRepository

                while (true) {
                    val currentIndex = preferencesRepository.receivedMessageId.first()
                    if (currentIndex >= incomeMessages.size) {
                        break
                    }

                    val currentRawMsg = incomeMessages[currentIndex]
                    val newMessage = MessageEntity(
                        conversationId = currentRawMsg.senderId,
                        senderId = currentRawMsg.senderId,
                        receiverId = currentRawMsg.receiverId,
                        messageText = currentRawMsg.messageText,
                        timestamp = System.currentTimeMillis(),
                        msgType = currentRawMsg.msgType,
                        extraData = currentRawMsg.extraData,
                    )

                    val imagesDir = File(applicationContext.filesDir, "contact_images")
                    val avatarFile = File(imagesDir, "contact_${newMessage.conversationId}.jpg")
                    val contactAvatarPath = avatarFile.absolutePath
                    val isFromSystem = when(currentRawMsg.contactName) {
                        "每日签到" -> true
                        "热门推荐" -> true
                        "在线商城" -> true
                        else -> false
                    }

                    messageRepository.insertMessage(newMessage)
                    contactRepository.receiveIncomingMessage(
                        messageEntity = newMessage,
                        senderName = currentRawMsg.contactName,
                        senderAvatar = contactAvatarPath,
                        isFromSystem = isFromSystem
                    )

                    preferencesRepository.saveReceivedMessageIdPreference(currentIndex + 1)
                    delay(5000)
                }
                    Result.success()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.failure()
                }
            }
        }
}