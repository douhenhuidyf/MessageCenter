package com.example.messagecenter.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.messagecenter.R
import com.example.messagecenter.ui.component.Avatar
import com.example.messagecenter.ui.theme.MessageCenterTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class PopPosition {
    TOP,
    BOTTOM
}

@Composable
fun BaseDialog(
    onDismissRequest: () -> Unit,
    position: PopPosition = PopPosition.BOTTOM,
    offsetPercent: Int = 100,
    content: @Composable ColumnScope.() -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    fun triggerDismiss() {
        visibleState.targetState = false
    }

    if (!visibleState.targetState && !visibleState.currentState) {
        onDismissRequest()
    }

    Dialog(
        onDismissRequest = { triggerDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = when (position) {
                PopPosition.TOP -> Alignment.TopCenter
                PopPosition.BOTTOM -> Alignment.BottomCenter
            }
        ) {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { triggerDismiss() })
                        }
                )
            }
            AnimatedVisibility(
                visibleState = visibleState,
                enter = when (position) {
                    PopPosition.TOP -> slideInVertically(initialOffsetY = { -it * offsetPercent / 100 })
                    PopPosition.BOTTOM -> slideInVertically(initialOffsetY = { it * offsetPercent / 100 })
                },
                exit = when (position) {
                    PopPosition.TOP -> slideOutVertically(targetOffsetY = { -it * offsetPercent / 100 })
                    PopPosition.BOTTOM -> slideOutVertically(targetOffsetY = { it * offsetPercent / 100 })
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) { detectTapGestures { } },
                    content = content
                )
            }
        }
    }
}

@Composable
fun NoNetworkDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
){
    BaseDialog(
        onDismissRequest = onDismiss,
        position = PopPosition.BOTTOM
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_network),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_network_tip1),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.no_network_point1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = stringResource(R.string.no_network_point2),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.no_network_tip2),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.no_network_point3),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Start)
                )
                TextButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .width(100.dp),
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationBanner(
    visible: Boolean,
    avatarPath: String,
    contactName: String,
    previewText: String,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(visible) {
        if (visible) {
            offsetY.snapTo(0f)
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .statusBarsPadding()
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (offsetY.value < -50) {
                            onDismiss()
                        } else {
                            scope.launch {
                            offsetY.animateTo(0f)
                        }
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            offsetY.animateTo(0f)
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        val target = offsetY.value + dragAmount
                        if (target <= 0) {
                            scope.launch {
                                offsetY.snapTo(target)
                            }
                        }
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    avatarPath = avatarPath,
                    size = 40.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = contactName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = previewText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


@Composable
fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    var selectedIdx by remember { mutableStateOf(selectedIndex) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
                },
        text = {
            Column {
                options.forEachIndexed { index, text ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedIdx = index }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (index == selectedIdx),
                            onClick = { selectedIdx = index }
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSelect(selectedIdx) }) {
                Text(
                    text = stringResource(R.string.confirm),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}


@Preview(device = "id:pixel_6")
@Composable
fun NoNetworkDialogPreview(){
    MessageCenterTheme {
        NoNetworkDialog(
            showDialog = true,
            onDismiss = {}
        )
    }
}

@Preview(device = "id:pixel_6")
@Composable
fun NewMessageDialogPreview(){
    MessageCenterTheme {
        NotificationBanner(
            visible = true,
            avatarPath = "",
            contactName = "张三",
            previewText = "这是一条预览文本",
            onClick = {},
            onDismiss = {}
        )
    }
}

@Preview(device = "id:pixel_6")
@Composable
fun SelectionDialogPreview(){
    MessageCenterTheme {
        SelectionDialog(
            title = "选择语言",
            options = listOf("中文", "英文"),
            selectedIndex = 0,
            onDismiss = {},
            onSelect = {}
        )
    }
}
