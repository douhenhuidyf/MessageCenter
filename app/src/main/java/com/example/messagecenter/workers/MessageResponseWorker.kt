package com.example.messagecenter.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.messagecenter.MessageCenterApplication
import com.example.messagecenter.data.repository.MessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MessageResponseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    companion object {
        const val WORK_NAME = "message_response_work"

        const val KEY_CONVERSATION_ID = "conversation_id"
        const val KEY_SENDER_ID = "sender_id"
        const val KEY_RECEIVER_ID = "receiver_id"
        const val KEY_MESSAGE_TEXT = "message_text"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val appContainer = (applicationContext as MessageCenterApplication).container
                val messageRepository = appContainer.messageRepository
                val contactRepository = appContainer.contactRepository

                val conversationId = inputData.getInt(KEY_CONVERSATION_ID, -1)
                val senderId = inputData.getInt(KEY_SENDER_ID, -1)
                val receiverId = inputData.getInt(KEY_RECEIVER_ID, -1)
                val messageText = inputData.getString(KEY_MESSAGE_TEXT) ?: ""
                 if (conversationId == -1 || senderId == -1 || receiverId == -1) {
                    return@withContext Result.failure()
                }

                 val messageEntity = MessageEntity(
                     conversationId = conversationId,
                     senderId = senderId,
                     receiverId = receiverId,
                     messageText = messageText,
                     timestamp = System.currentTimeMillis()
                 )
                messageRepository.insertMessage(messageEntity)
                contactRepository.updateContactFromMessage(messageEntity)

                delay(1000 + Random.nextInt(2000).toLong())
                val responseMessageEntity = MessageEntity(
                    conversationId = conversationId,
                    senderId = receiverId,
                    receiverId = senderId,
                    messageText = "收到~",
                    timestamp = System.currentTimeMillis()
                )
                 messageRepository.insertMessage(responseMessageEntity)
                 contactRepository.updateContactFromMessage(responseMessageEntity)

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }

    }
}