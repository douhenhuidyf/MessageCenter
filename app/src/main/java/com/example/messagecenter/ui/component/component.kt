@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3AdaptiveApi::class
)

package com.example.messagecenter.ui.component

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.messagecenter.R
import java.io.File

import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.ui.theme.MessageCenterTheme
import com.example.messagecenter.utils.timestampToString

@Composable
fun Avatar(avatarPath: String,  modifier: Modifier = Modifier, size: Dp = 55.dp){
    if (avatarPath.isEmpty()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.default_avatar)
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
    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    val route = "conversation/${contactEntity.contactId}"
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
                        Surface(
                            modifier = Modifier.padding(start = 4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RectangleShape,
                        ){
                            Text(
                                text = "系统信息",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                            )
                        }
                    }
                }
                Text(
                    text = timestampString,
                    style = MaterialTheme.typography.bodySmall,
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = contactEntity.previewText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                )
//                Icon(
//                    imageVector = Icons.Default.Star,
//                    contentDescription = null
//                    )
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
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    Toast.makeText(
                        context,
                        "功能暂未实现",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        )
        Text("消息",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Image(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    Toast.makeText(
                        context,
                        "功能暂未实现",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MessagePreviewCellPreview(){
    val navController = rememberNavController()
    val contactEntity = ContactEntity(
        id = 1,
        contactId = 1,
        contactName = "张三",
        contactSureName = "张三丰",
        contactAvatar = "",
        isFromSystem = true,
        previewText = "11111111111111111111111111",
        timestamp = 159999999,
        unReadNum = 10
    )
    MessageCenterTheme{
        MessagePreviewCell(
            {},
            {},
            navController,
            contactEntity,
        )
    }
}




