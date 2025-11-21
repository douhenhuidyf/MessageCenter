package com.example.messagecenter.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MessagePreview(
    val id: Int,
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

