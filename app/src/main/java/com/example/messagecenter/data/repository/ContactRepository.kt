package com.example.messagecenter.data.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


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
    val isMuted: Boolean = false,
    val unReadNum: Int = 0,
    val isPinned: Boolean = false,
)

@Dao
interface ContactEntityDao {
    @Query("SELECT COUNT(*) FROM contacts")
    fun getCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getContactCount(): Int

    @Update
    suspend fun update(contactEntity: ContactEntity)

    @Query("SELECT * FROM contacts WHERE contactId = :contactId LIMIT 1")
    suspend fun getContactById(contactId: Int): ContactEntity?

    @Query("SELECT * FROM contacts WHERE contactId IN (:contactIds)")
    suspend fun getContactsByIds(contactIds: List<Int>): List<ContactEntity>

    @Transaction
    suspend fun insertContacts(contacts: List<ContactEntity>) {
        val groupedContacts = contacts.groupBy { it.contactId }

        for ((contactId, list) in groupedContacts) {
            val latestContactData = list.maxBy { it.timestamp }
            val totalNewUnReadCount = list.sumOf { it.unReadNum }
            val existing = getContactById(contactId)
        
            if (existing != null) {
                val contactToUpdate = latestContactData.copy(
                    id = existing.id,
                    contactSureName = existing.contactSureName,
                    unReadNum = existing.unReadNum + totalNewUnReadCount
                )
                update(contactToUpdate)
            } else {
                val contactToInsert = latestContactData.copy(
                    unReadNum = totalNewUnReadCount
                )
                insert(contactToInsert)
            }
        }
    }

    @Transaction
    suspend fun updateContactFromMessage(messageEntity: MessageEntity){
        val contactId = messageEntity.conversationId
        val existingContact = getContactById(contactId)
        if (existingContact != null) {
            val contactToUpdate = existingContact.copy(
                previewText = messageEntity.messageText,
                timestamp = messageEntity.timestamp
            )
            update(contactToUpdate)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactEntity: ContactEntity)

    @Query("DELETE FROM contacts WHERE contactId = :contactId")
    suspend fun deleteContact(contactId: Int)

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()

    @Query("SELECT * FROM contacts ORDER BY isPinned DESC, timestamp DESC LIMIT :limit")
    fun getContactsStream(limit: Int): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE contactId = :contactId LIMIT 1")
    fun getContact(contactId: Int): Flow<ContactEntity>

    @Query("UPDATE contacts SET unReadNum = 0 WHERE contactId = :contactId")
    suspend fun markAsRead(contactId: Int)

    @Query("UPDATE contacts SET contactSureName = :newSureName WHERE contactId = :contactId")
    suspend fun updateSureName(contactId: Int, newSureName: String)

    @Query("UPDATE contacts SET isMuted = :isMuted WHERE contactId = :contactId")
    suspend fun updateIsMuted(contactId: Int, isMuted: Boolean)

    @Query("UPDATE contacts SET isPinned = :isPinned WHERE contactId = :contactId")
    suspend fun updateIsPinned(contactId: Int, isPinned: Boolean)

    @Query("SELECT * FROM contacts WHERE contactName LIKE '%' || :query || '%' OR contactSureName LIKE '%' || :query || '%' ORDER BY isPinned DESC, timestamp DESC LIMIT :limit")
    suspend fun searchContacts(query: String, limit: Int): List<ContactEntity>

    @Query("SELECT COALESCE(SUM(unReadNum), 0) FROM contacts")
    fun getTotalUnreadCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(unReadNum), 0) FROM contacts WHERE isFromSystem = 1")
    fun getSystemUnreadCount(): Flow<Int>
    
    @Query("SELECT COUNT(messages.id) FROM messages INNER JOIN contacts ON messages.conversationId = contacts.contactId WHERE contacts.isFromSystem = 1")
    fun getSystemMessageCount(): Flow<Int>

}

data class NewMessage(
    val contactId: Int,
    val contactName: String,
    val avatarPath: String,
    val previewText: String
)

class ContactRepository(
    private val contactEntityDao: ContactEntityDao,
) {
    private val _newMessageFlow = MutableSharedFlow<NewMessage>()
    val newMessageFlow = _newMessageFlow.asSharedFlow()

    @Volatile
    private var currentViewingContactId: Int? = null

    suspend fun emitNewMessage(event: NewMessage) {
        _newMessageFlow.emit(event)
    }

    fun setCurrentViewingId(id: Int?) {
        currentViewingContactId = id
    }

    fun getContactCountFlow(): Flow<Int> = contactEntityDao.getCount()

    suspend fun update(contactEntity: ContactEntity) {
        contactEntityDao.update(contactEntity)
    }

    suspend fun insert(contactEntity: ContactEntity){
        contactEntityDao.insert(contactEntity)
    }

    suspend fun getContactById(contactId: Int): ContactEntity? {
        return contactEntityDao.getContactById(contactId)
    }

    suspend fun getContactsByIds(contactIds: List<Int>): List<ContactEntity> {
        return contactEntityDao.getContactsByIds(contactIds)
    }

    suspend fun insertContacts(contacts: List<ContactEntity>) {
        contactEntityDao.insertContacts(contacts)
    }

    suspend fun updateContactFromMessage(messageEntity: MessageEntity){
        contactEntityDao.updateContactFromMessage(messageEntity)
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

    suspend fun deleteAllContacts() {
        contactEntityDao.deleteAllContacts()
    }

    suspend fun updateSureName(contactId: Int, newSureName: String) {
        contactEntityDao.updateSureName(contactId, newSureName)
    }

    suspend fun updateIsMuted(contactId: Int, isMuted: Boolean) {
        contactEntityDao.updateIsMuted(contactId, isMuted)
    }

    suspend fun updateIsPinned(contactId: Int, isPinned: Boolean) {
        contactEntityDao.updateIsPinned(contactId, isPinned)
    }

    suspend fun searchContacts(query: String, limit: Int): List<ContactEntity> {
        return contactEntityDao.searchContacts(query, limit)
    }

    suspend fun receiveIncomingMessage(
        messageEntity: MessageEntity,
        senderName: String,
        senderAvatar: String,
        isFromSystem: Boolean = false,
    ) {
        val contactId = messageEntity.conversationId
        val existingContact = getContactById(contactId)
        val isViewing = currentViewingContactId == contactId
        val isMuted = existingContact?.isMuted ?: false

        if (existingContact != null) {
            val newUnReadCount = if (isViewing) 0 else existingContact.unReadNum + 1

            val contactToUpdate = existingContact.copy(
                previewText = messageEntity.messageText,
                timestamp = messageEntity.timestamp,
                unReadNum = newUnReadCount
            )
            update(contactToUpdate)
        } else {
            Log.d("receive", "$senderName $senderAvatar")
            val newContact = ContactEntity(
                contactId = contactId,
                contactName = senderName,
                contactSureName = null,
                contactAvatar = senderAvatar,
                isFromSystem = isFromSystem,
                previewText = messageEntity.messageText,
                timestamp = messageEntity.timestamp,
                unReadNum = if (isViewing) 0 else 1
            )
            insert(newContact)
        }

        if (!isViewing && !isMuted) {
            emitNewMessage(
                NewMessage(
                    contactId = contactId,
                    contactName = senderName,
                    avatarPath = senderAvatar,
                    previewText = messageEntity.messageText
                )
            )
        }
    }
    fun getTotalUnreadCountFlow() = contactEntityDao.getTotalUnreadCount()

    fun getSystemUnreadCountFlow() = contactEntityDao.getSystemUnreadCount()
    
    fun getSystemMessageCountFlow() = contactEntityDao.getSystemMessageCount()

}