@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3AdaptiveApi::class
)

package com.example.messagecenter.ui.component

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.messagecenter.R
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.utils.timestampToString

@Composable
fun Avatar(avatarPath: String,  modifier: Modifier = Modifier, size: Dp = 55.dp){
    if (avatarPath.isEmpty()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.avatar)
                .crossfade(true)
                .size(100, 100)
                .build(),
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        return
    }
    else{
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(File(avatarPath))
                .crossfade(true)
                .size(100, 100)
                .build(),
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MessagePreviewCell(
    markAsRead: (Int) -> Unit,
    deleteContact: (Int) -> Unit,
    navController: NavController,
    contactEntity: ContactEntity,
    modifier: Modifier = Modifier,
) {
    val timestampString = timestampToString(contactEntity.timestamp)
    val encodedAvatar = URLEncoder.encode(contactEntity.contactAvatar, StandardCharsets.UTF_8.toString())
    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    var route =
                        "conversation/${contactEntity.contactId}/${contactEntity.contactName}/$encodedAvatar"
                    if (!contactEntity.contactSureName.isNullOrEmpty()) {
                        route += "?contactSureName=${contactEntity.contactSureName}"
                    }
                    Log.d(
                        "MessagePreviewCell",
                        "跳转到对话页: ${contactEntity.contactName} - ${contactEntity.contactId} "
                    )
                    navController.navigate(route)
                    markAsRead(contactEntity.contactId)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(avatarPath = contactEntity.contactAvatar)
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val contactName = if (contactEntity.contactSureName.isNullOrEmpty()) {
                    contactEntity.contactName
                } else {
                    contactEntity.contactSureName
                }

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = contactName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (contactEntity.isFromSystem) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                Text(
                    text = timestampString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (contactEntity.unReadNum > 0) {
                    Text(
                        text = "[${contactEntity.unReadNum}条]",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = contactEntity.previewText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null
                    )
            }

        }
    }
    HorizontalDivider(
        modifier = Modifier
            .padding(start = 65.dp)
            .alpha(0.75f)
    )
}

@Composable
fun MessagePageTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(imageVector = Icons.Default.Menu,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Text("消息",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Image(imageVector = Icons.Default.Search,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
    }
}




