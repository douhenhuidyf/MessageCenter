package com.example.messagecenter.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MessagePreview(
    val id: String,
    val senderName: Int,
//    val senderSureName: String?,
    val senderAvatar: String,
//    val isFromSystem : Boolean,
    val previewText: Int,
    val timestamp: Long,
//    val isRead: Boolean = false,
//    val isMuted: Boolean = false,
//    val unReadNum: Int = 0,
//    val hasPinned: Boolean = false,

)

