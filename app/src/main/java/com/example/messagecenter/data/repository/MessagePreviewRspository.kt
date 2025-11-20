package com.example.messagecenter.data.repository

class MessageRepository(
    private val messageDao: MessageDao, // 本地数据库DAO
    private val messageService: MessageService // 远程API服务
) {

    suspend fun loadMessages(page: Int, pageSize: Int): Result<List<Message>> {
        return try {
            // 这里可以实现缓存逻辑，比如先从本地数据库加载，再从网络刷新
            // 为了简化，我们直接从网络获取
            val response = messageService.getMessages(page, pageSize)
            if (response.isSuccessful) {
                val messages = response.body() ?: emptyList()
                // 将网络数据存入本地数据库
                messageDao.insertAll(messages)
                Result.success(messages)
            } else {
                Result.failure(Exception("Failed to load messages from network"))
            }
        } catch (e: Exception) {
            // 网络请求失败，可以尝试从本地数据库加载缓存
            val cachedMessages = messageDao.getMessages(page, pageSize)
            if (cachedMessages.isNotEmpty()) {
                Result.success(cachedMessages)
            } else {
                Result.failure(e)
            }
        }
    }

    // 标记消息为已读
    suspend fun markAsRead(messageId: String) {
        messageDao.markAsRead(messageId)
        // 可以在这里同步到服务器
        // messageService.markAsRead(messageId)
    }

    // 删除消息
    suspend fun deleteMessage(messageId: String) {
        messageDao.delete(messageId)
        // 可以在这里同步到服务器
        // messageService.deleteMessage(messageId)
    }
}