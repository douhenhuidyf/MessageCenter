@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3AdaptiveApi::class
)

package com.example.messagecenter.ui.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.io.File

import com.example.messagecenter.R
import com.example.messagecenter.data.repository.ContactEntity
import com.example.messagecenter.ui.theme.MessageCenterTheme
import com.example.messagecenter.utils.getFirstKeywordIndices
import com.example.messagecenter.utils.timestampToString

@Composable
fun Avatar(
    avatarPath: String,
    modifier: Modifier = Modifier,
    size: Dp = 55.dp
){
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
    highlightText: String,
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (contactEntity.isFromSystem) {
                        Surface(
                            modifier = Modifier.padding(start = 4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp),
                        ){
                            Text(
                                text = stringResource(R.string.system_message),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                            )
                        }
                    }
                    if (contactEntity.isPinned) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Yellow
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timestampString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (contactEntity.unReadNum > 0) {
                    val color = when(contactEntity.isMuted){
                        true -> MaterialTheme.colorScheme.onPrimaryContainer
                        false -> Color.Red
                    }
                    Text(
                        text = "[${contactEntity.unReadNum} ${stringResource(R.string.messages)}]",
                        style = MaterialTheme.typography.bodyMedium,
                        color = color
                    )
                }

                if (highlightText.isNotEmpty()) {
                    HighlightFirstKeywordText(
                        allText = contactEntity.previewText,
                        keyword = highlightText,
                        maxDisplayLength = 25
                    )
                }
                else{
                    Text(
                        text = contactEntity.previewText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
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
fun MessagePageTopBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
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
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
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
        Text(
            text = stringResource(R.string.message_record),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Image(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    navController.navigate("search")
                }
            )
        )
    }
}

@Composable
fun TextBar(
    leftIcon: ImageVector? = null,
    inputText: String,
    imeAction: ImeAction,
    textFieldColor: Color = MaterialTheme.colorScheme.background,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
    promptText: String = "",
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
){
    BasicTextField(
        modifier = modifier
            .focusRequester(focusRequester),
        value = inputText,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        onValueChange = {
            onValueChange(it)
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = textFieldColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (leftIcon != null) {
                            Icon(
                                imageVector = leftIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp)
                                .weight(1f)
                        ) {
                            if (inputText.isEmpty())
                                Text(
                                    text = promptText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            innerTextField()
                        }
                        if (inputText.isNotEmpty())
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            onValueChange("")
                                        }
                                    )
                            )
                    }
                }
            }
        }
    )
}

@Composable
fun HighlightFirstKeywordText(
    allText: String,
    keyword: String,
    maxDisplayLength: Int = 25,
    defaultTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    normalTextColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    highlightColor: Color = Color.Blue,
    isBold: Boolean = true,
    isUnderline: Boolean = false
) {
    val indices = getFirstKeywordIndices(allText, keyword)
    val keywordStart = indices.getOrNull(0) ?: 0
    val keywordEnd = indices.getOrNull(1) ?: 0
    val keywordLength = keywordEnd - keywordStart
    val totalTextLength = allText.length

    val displayText = if (totalTextLength <= maxDisplayLength) {
        allText
    } else {
        val remainingLength = maxDisplayLength - keywordLength
        val leftLength = remainingLength / 2
        val rightLength = remainingLength - leftLength

        val leftStart = if (keywordStart > leftLength) {
            keywordStart - leftLength
        } else {
            0
        }
        val rightEnd = if ((totalTextLength - keywordEnd) > rightLength) {
            keywordEnd + rightLength
        } else {
            totalTextLength
        }

        buildString {
            if (leftStart > 0) append("...")
            append(allText.substring(leftStart, rightEnd))
            if (rightEnd < totalTextLength) append("...")
        }
    }

    val (displayKeywordStart, displayKeywordEnd) = run {
        if (displayText.contains(keyword)) {
            val start = displayText.indexOf(keyword)
            start to start + keywordLength
        } else {
            0 to 0
        }
    }

    val annotatedText = buildAnnotatedString {
        val defaultSpanStyle = defaultTextStyle.toSpanStyle().copy(
            color = normalTextColor
        )
        addStyle(
            style = defaultSpanStyle,
            start = 0,
            end = displayText.length
        )

        append(displayText)
        if (displayKeywordStart < displayKeywordEnd) {
            val highlightSpanStyle = defaultSpanStyle.copy(
                color = highlightColor,
                fontWeight = if (isBold) FontWeight.Bold else defaultSpanStyle.fontWeight,
                textDecoration = if (isUnderline) TextDecoration.Underline else defaultSpanStyle.textDecoration
            )
            addStyle(
                style = highlightSpanStyle,
                start = displayKeywordStart,
                end = displayKeywordEnd
            )
        }
    }

    Text(
        text = annotatedText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}


@Preview(showBackground = true)
@Composable
fun MessagePreviewCellPreview(){
    val navController = rememberNavController()
    val contactEntity = ContactEntity(
        id = 1,
        contactId = 1,
        contactName = "张三",
        contactSureName = "张三丰11111111111111111111111111",
        contactAvatar = "",
        isFromSystem = true,
        previewText = "21111111111111111111111111",
        timestamp = 159999999,
        unReadNum = 10
    )
    MessageCenterTheme{
        MessagePreviewCell(
            {},
            {},
            navController,
            contactEntity,
            "21"
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHighlightFirstKeywordText() {
    MessageCenterTheme {
        HighlightFirstKeywordText(
            allText = "测试高亮测试高亮测试高亮测试高12亮测试高亮测试高亮测试高亮测试高亮",
            keyword = "高12亮",
        )
    }
}

