@file:OptIn(
    ExperimentalFoundationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class
)

package com.example.messagecenter.presentation.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow

import com.example.messagecenter.data.model.MessagePreview

import com.example.messagecenter.utils.timestampToString

@Composable
fun Avatar(messagePreview: MessagePreview){
    Image(
        painter = painterResource(messagePreview.senderAvatar),
        contentDescription = null,
        modifier = Modifier
            .size(55.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MessagePreviewCell(messagePreview: MessagePreview, modifier: Modifier = Modifier) {
    val timestampString = timestampToString(messagePreview.timestamp)
    Row(
        modifier = modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(messagePreview = messagePreview)
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalContext.current.getString(messagePreview.senderName),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant

                )
                Text(
                    text = timestampString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = LocalContext.current.getString(messagePreview.previewText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
                Text("消息",
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Image(imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        },
        modifier = modifier.padding(8.dp)
    )
}




