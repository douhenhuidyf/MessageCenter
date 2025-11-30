package com.example.messagecenter.ui.screen

import android.app.Notification
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.viewmodel.SearchViewModel
import com.example.messagecenter.ui.component.MessagePreviewCell
import com.example.messagecenter.ui.theme.MessageCenterTheme

@Composable
fun SearchScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var editedName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var searched by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    placeholder = { Text(text = "搜索消息") },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    trailingIcon = {
                        if (editedName.isNotEmpty()) {
                            IconButton(onClick = { editedName = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear text",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    keyboardActions = KeyboardActions(onSearch = {})
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "搜索",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            viewModel.search(editedName)
                            searched = true
                        }
                    )
                )

            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (uiState.contactResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "联系人",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .padding(start = 18.dp)

                        )
                    }
                    items(uiState.contactResults) { contact ->
                        MessagePreviewCell(
                            markAsRead = {},
                            deleteContact = {},
                            navController = navController,
                            contactEntity = contact
                        )
                    }
                }
                if (uiState.messageResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "消息记录",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 16.dp, start = 18.dp)
                        )
                    }
                    items(uiState.messageResults) { contactWithMessage ->
                        MessagePreviewCell(
                            markAsRead = { },
                            deleteContact = { },
                            navController = navController,
                            contactEntity = contactWithMessage
                        )
                    }
                }
            }

            if (searched && !uiState.isLoading && uiState.contactResults.isEmpty() && uiState.messageResults.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "未找到相关结果",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MessageCenterTheme {
        SearchScreen(navController = rememberNavController())
    }
}