package com.example.messagecenter.data.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import android.content.Context
import androidx.room.Database
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.messagecenter.data.model.MessagePreview

@Entity(tableName = "contacts")
data class  ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val contactName: Int,
    val contactSureName: String?,
    val contactAvatar: String,
//    val isFromSystem : Boolean,
    val previewText: Int,
    val timestamp: Long,
//    val isRead: Boolean = false,
//    val isMuted: Boolean = false,
//    val unReadNum: Int = 0,
//    val hasPinned: Boolean = false,
)

@Dao
interface ContactEntityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contactEntity: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Update
    suspend fun update(contactEntity: ContactEntity)

    @Query("DELETE FROM contacts WHERE id = contactId")
    suspend fun delete(contactId: Int)

    @Query("SELECT * FROM contacts ORDER BY timestamp DESC LIMIT startNum OFFSET groupNum")
    fun getMoreContacts(startNum : Int, groupNum : Int): Flow<List<ContactEntity>>
}

@Database(entities = [ContactEntity::class], version = 1, exportSchema = false)
abstract class ContactsDatabase : RoomDatabase() {
    abstract fun ContactEntityDao(): ContactEntityDao

    companion object {
        @Volatile
        private var Instance: ContactsDatabase? = null

        fun getDatabase(context: Context): ContactsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ContactsDatabase::class.java, "contacts_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}



class MessageRepository private constructor(
    private val contactEntityDao: ContactEntityDao,
) {
    suspend fun insertContact(messagePreview: MessagePreview) {
        contactEntityDao.insert(toContactEntity(messagePreview))
    }

    suspend fun insertContacts(contacts: List<MessagePreview>) {
        contactEntityDao.insertContacts(contacts.map { toContactEntity(it) })
    }

    suspend fun updateContact(messagePreview: MessagePreview) {
        contactEntityDao.update(toContactEntity(messagePreview))
    }

    suspend fun deleteContact(contactId: Int) {
        contactEntityDao.delete(contactId)
    }
    suspend fun getMoreContacts(startNum : Int, groupNum : Int) : Flow<List<ContactEntity>> {
        return contactEntityDao.getMoreContacts(startNum, groupNum)
    }

    private fun toContactEntity(messagePreview: MessagePreview): ContactEntity {
        return ContactEntity(
            id = messagePreview.id,
            contactName = messagePreview.contactName,
            contactSureName = messagePreview.contactSureName,
            contactAvatar = messagePreview.contactAvatar,
            previewText = messagePreview.previewText,
            timestamp = messagePreview.timestamp
        )
    }
    private fun toMessagePreview(contactEntity: ContactEntity): MessagePreview {
        return MessagePreview(
            id = contactEntity.id,
            contactName = contactEntity.contactName,
            contactSureName = contactEntity.contactSureName,
            contactAvatar = contactEntity.contactAvatar,
            previewText = contactEntity.previewText,
            timestamp = contactEntity.timestamp
        )
    }
    // 标记消息为已读
//    suspend fun markAsRead(messageId: String) {
//        messageDao.markAsRead(messageId)
//        // 可以在这里同步到服务器
//        // messageService.markAsRead(messageId)
//    }
//
//    // 删除消息
//    suspend fun deleteMessage(messageId: String) {
//        messageDao.delete(messageId)
//        // 可以在这里同步到服务器
//        // messageService.deleteMessage(messageId)
//    }
}