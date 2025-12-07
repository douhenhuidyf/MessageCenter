package com.example.messagecenter.ui.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import com.example.messagecenter.data.repository.MessageEntity
import com.example.messagecenter.ui.theme.MessageCenterTheme
import com.example.messagecenter.R

@Composable
fun ConversationTopBar(
    contactName: String,
    contactId: Int,
    navController: NavController,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(bottom = 8.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    navController.popBackStack()
                }
            ),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = contactName,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    Log.d("ConversationScreen", "进入联系人信息页: $contactName - $contactId")
                    val route = "contact/${contactId}"
                    navController.navigate(route)
                }
            ),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


@Composable
fun ConversationBottomBar(
    editText: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
){
    val focusRequester = remember { FocusRequester() }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {

                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextBar(
                inputText = editText,
                singleLine = false,
                onValueChange = {
                    onValueChange(it)
                },
                imeAction = ImeAction.Default,
                focusRequester = focusRequester,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 25.dp, max = 75.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Default.Send,
                contentDescription = "发送",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        onSend()
                    }
                )
            )
        }
    }
}


@Composable
fun ConversationCell(
    messageEntity: MessageEntity,
    contactAvatar: String,
    isMe: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var displayTime by remember { mutableStateOf(false) }

    val rewardText = stringResource(R.string.received_reward)
    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
        ) {
            if (!isMe) {
                Avatar(
                    avatarPath = contactAvatar,
                    size = 40.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(
                horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
            ) {
                Surface(
                    color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.widthIn(max = screenWidth * 0.69f),
                    onClick = { displayTime = !displayTime }
                ) {

                    when (messageEntity.msgType) {
                        MessageEntity.TYPE_IMAGE -> {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(messageEntity.extraData)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Image Message",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp)
                                ,
                                contentScale = ContentScale.Crop
                            )
                        }

                        MessageEntity.TYPE_OPERATION -> {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = messageEntity.messageText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        Toast.makeText(context, rewardText, Toast.LENGTH_SHORT)
                                            .show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Text(
                                        text = messageEntity.extraData ?: stringResource(R.string.view_detail),
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        else -> {
                            Text(
                                text = messageEntity.messageText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                if (displayTime) {
                    val calendar = Calendar.getInstance()
                    val currentYear = calendar.get(Calendar.YEAR)
                    calendar.timeInMillis = messageEntity.timestamp
                    val timeStampYear = calendar.get(Calendar.YEAR)

                    val dateFormat = when (timeStampYear == currentYear) {
                        true -> SimpleDateFormat("MM-dd HH:mm")
                        false -> SimpleDateFormat("yyyy-MM-dd HH:mm")
                    }
                    val timeString = dateFormat.format(Date(messageEntity.timestamp))
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (isMe) {
                Spacer(modifier = Modifier.width(8.dp))
                Avatar(avatarPath = "", size = 40.dp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun ConversationCellPreview() {
    MessageCenterTheme() {
        ConversationCell(
            messageEntity = MessageEntity(
                conversationId = 1,
                senderId = 1,
                receiverId = 0,
                messageText = "你好hhh111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111",
                timestamp = System.currentTimeMillis() - 1000 * 60
            ),
            contactAvatar = "",
            isMe = false
        )
    }
}


@Preview(device = Devices.PIXEL_4)
@Composable
fun ConversationBottomBarPreview(){
    MessageCenterTheme{
        ConversationBottomBar(
            editText = "这是一条测试消息",
            onValueChange = {},
            onSend = {}
        )
    }
}
