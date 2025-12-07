package com.example.messagecenter.data.repository

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "messages")
data class  MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val conversationId : Int,
    val senderId: Int,
    val receiverId: Int,
    val messageText: String,
    val timestamp: Long,
    val msgType: Int = TYPE_TEXT,
    val extraData: String? = null
){
    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_IMAGE = 1
        const val TYPE_OPERATION = 2
    }
}

@Dao
interface MessageEntityDao{
    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
    fun getMessageCount(conversationId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(messageEntity: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC")
    fun getMessages(conversationId: Int): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: Int)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    @Query("SELECT * FROM messages WHERE messageText LIKE '%' || :query || '%' ORDER BY timestamp DESC LIMIT :limit")
    suspend fun searchMessages(query: String, limit: Int): List<MessageEntity>

    @Query("SELECT COUNT(*) FROM messages")
    fun getTotalMessageCount(): Flow<Int>
}


class MessageRepository(
    private val messageEntityDao: MessageEntityDao,
){
    fun getMessageCount(conversationId: Int): Flow<Int> {
        return messageEntityDao.getMessageCount(conversationId)
    }

    suspend fun insertMessage(messageEntity: MessageEntity) {
        messageEntityDao.insertMessage(messageEntity)
    }

    suspend fun insertMessages(messages: List<MessageEntity>) {
        messageEntityDao.insertMessages(messages)
    }

    fun getMessages(conversationId: Int): Flow<List<MessageEntity>> {
        return messageEntityDao.getMessages(conversationId)
    }

    suspend fun deleteConversation(conversationId: Int) {
        messageEntityDao.deleteConversation(conversationId)
    }

    suspend fun deleteAllMessages() {
        messageEntityDao.deleteAllMessages()
    }

    suspend fun searchMessages(query: String, limit: Int): List<MessageEntity> {
        return messageEntityDao.searchMessages(query, limit)
    }

    fun getTotalMessageCountFlow(): Flow<Int> {
        return messageEntityDao.getTotalMessageCount()
    }
}