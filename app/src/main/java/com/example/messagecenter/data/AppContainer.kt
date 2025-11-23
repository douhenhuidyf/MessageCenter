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

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.data.repository.ContactRepository
import com.example.messagecenter.data.repository.ContactsDatabase


interface AppContainer {
    val contactRepository: ContactRepository
    val imageStorageManager: ImageStorageManager
    fun initializeSampleData()
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val contactRepository: ContactRepository = ContactRepository(ContactsDatabase.getDatabase(context).ContactEntityDao())
    override val imageStorageManager: ImageStorageManager = ImageStorageManager(context)
    override fun initializeSampleData() {

        CoroutineScope(Dispatchers.IO).launch {
            val result = contactRepository.getMoreContacts(0, 1)

            result.onSuccess { contacts ->
                if (contacts.isEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val sampleContacts = MutableList(100) { index ->
                            ContactEntity(
                                contactId = index,
                                contactName = when (index % 3) {
                                    0 -> "刘梓晗 $index"
                                    1 -> "杨蕴涵 $index"
                                    else -> "约翰逊 $index"
                                },
                                contactSureName = if (index % 3 == 2) "Johnson" else "",
                                contactAvatar = "",
                                isFromSystem = index % 3 == 2,
                                previewText = "这是第 $index 条测试消息...",
                                timestamp = System.currentTimeMillis() - (index * 600000),
                                isRead = index % 3 != 2,
                                unReadNum = if (index % 3 != 2) index else 0
                            )
                        }
                        Log.d("AppDataContainer", "Inserting ${sampleContacts.size} sample contacts")
                        val updatedContacts = sampleContacts.map { contact ->
                            val dummyBitmap =
                                BitmapFactory.decodeResource(context.resources, R.drawable.avatar)

                            val imagePath =
                                imageStorageManager.saveContactImage(dummyBitmap, contact.contactId)
                            contact.copy(contactAvatar = imagePath)
                        }

                        contactRepository.insertContacts(updatedContacts)
                    }
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