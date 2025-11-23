package com.example.messagecenter.data.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase


@Entity(tableName = "contacts")
data class  ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val contactId: Int,
    val contactName: String,
    val contactSureName: String?,
    val contactAvatar: String,
    val isFromSystem : Boolean,
    val previewText: String,
    val timestamp: Long,
    val isRead: Boolean = false,
//    val isMuted: Boolean = false,
    val unReadNum: Int = 0,
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

    @Query("DELETE FROM contacts WHERE contactId = :contactId")
    suspend fun delete(contactId: Int)

    @Query("SELECT * FROM contacts ORDER BY timestamp DESC LIMIT :groupNum OFFSET :startNum")
    suspend fun getMoreContacts(startNum : Int, groupNum : Int): List<ContactEntity>
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



class ContactRepository(
    private val contactEntityDao: ContactEntityDao,
) {
    suspend fun insertContact(contactEntity: ContactEntity) {
        contactEntityDao.insert(contactEntity)
    }

    suspend fun insertContacts(contacts: List<ContactEntity>) {
        contactEntityDao.insertContacts(contacts)
    }

    suspend fun updateContact(contactEntity: ContactEntity) {
        contactEntityDao.update(contactEntity)
    }

    suspend fun deleteContact(contactId: Int) {
        contactEntityDao.delete(contactId)
    }
    suspend fun getMoreContacts(startNum: Int, groupNum: Int): Result<List<ContactEntity>> {
        return try {
            val contacts = contactEntityDao.getMoreContacts(startNum, groupNum)
            Log.d("ContactRepository", "Fetched ${contacts.size} contacts")
            Result.success(contacts)
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error fetching contacts: ${e.message}")
            Result.failure(e)
        }
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