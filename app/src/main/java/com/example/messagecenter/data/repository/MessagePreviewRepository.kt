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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
//    val isMuted: Boolean = false,
    val unReadNum: Int = 0,
//    val hasPinned: Boolean = false,
)

@Dao
interface ContactEntityDao {
    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contactEntity: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Query("DELETE FROM contacts WHERE contactId = :contactId")
    suspend fun deleteContact(contactId: Int)

    @Query("SELECT * FROM contacts ORDER BY timestamp DESC LIMIT :limit")
    fun getContactsStream(limit: Int): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE contactId = :contactId LIMIT 1")
    fun getContact(contactId: Int): Flow<ContactEntity>

    @Query("UPDATE contacts SET unReadNum = 0 WHERE contactId = :contactId")
    suspend fun markAsRead(contactId: Int)

    @Query("UPDATE contacts SET contactSureName = :newSureName WHERE contactId = :contactId")
    suspend fun updateSureName(contactId: Int, newSureName: String) 

}

@Database(entities = [ContactEntity::class], version = 1, exportSchema = false)
abstract class ContactsDatabase : RoomDatabase() {
    abstract fun contactEntityDao(): ContactEntityDao

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
    suspend fun getContactCount(): Int = withContext(Dispatchers.IO) {
        contactEntityDao.getCount()
    }

    suspend fun insertContact(contactEntity: ContactEntity) {
        contactEntityDao.insert(contactEntity)
    }

    suspend fun insertContacts(contacts: List<ContactEntity>) {
        contactEntityDao.insertContacts(contacts)
    }

    fun getContactsStream(limit: Int): Flow<List<ContactEntity>> {
        return contactEntityDao.getContactsStream(limit)
    }

    suspend fun getContact(contactId: Int): Flow<ContactEntity> {
        return contactEntityDao.getContact(contactId)
    }

    suspend fun markAsRead(contactId: Int) {
        contactEntityDao.markAsRead(contactId)
    }

    suspend fun deleteContact(contactId: Int) {
        contactEntityDao.deleteContact(contactId)
    }

    suspend fun updateSureName(contactId: Int, newSureName: String) {
        contactEntityDao.updateSureName(contactId, newSureName)
    }
}