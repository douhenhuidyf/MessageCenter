package com.example.messagecenter.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import com.example.messagecenter.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.ContactRepository
import com.example.messagecenter.data.repository.ContactsDatabase


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

interface AppContainer {
    val contactRepository: ContactRepository
    val imageStorageManager: ImageStorageManager
    fun initializeSampleData()
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val contactRepository: ContactRepository = ContactRepository(ContactsDatabase.getDatabase(context).contactEntityDao())
    override val imageStorageManager: ImageStorageManager = ImageStorageManager(context)
    override fun initializeSampleData() {

        CoroutineScope(Dispatchers.IO).launch {
            val count = contactRepository.getContactCount()
            if (count == 0){
                CoroutineScope(Dispatchers.IO).launch {
                    val jsonString = context.assets.open("mock_data/mock_messages.json").bufferedReader().use { it.readText() }
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

    fun loadContactImage(imagePath: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(imagePath)
        } catch (e: Exception) {
            null
        }
    }

    fun deleteContactImage(imagePath: String) {
        try {
            File(imagePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}