package com.example.messagecenter.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.viewmodel.ContactViewModel

import com.example.messagecenter.ui.component.Avatar
import com.example.messagecenter.ui.component.ContactSettingCell
import com.example.messagecenter.ui.theme.MessageCenterTheme
import com.example.messagecenter.data.viewmodel.ContactDetailViewModel

@Composable
fun ContactScreen(
    navController: NavController,
    viewModel : ContactDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showSureNameEdit by rememberSaveable{ mutableStateOf(false) }
    val showName = if (uiState.contactSureName.isNullOrEmpty()){
        uiState.contactName
    }
    else{
        uiState.contactSureName ?: ""
    }
    var editedName by rememberSaveable { mutableStateOf(showName) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            }
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Avatar(avatarPath = uiState.contactAvatar, size = 90.dp)
            Text(
                text = showName + ">",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                ContactSettingCell(icon = Icons.Default.Settings, text = "关注", onClick = {})
                ContactSettingCell(icon = Icons.Default.Notifications, text = "免打扰", onClick = {})
                ContactSettingCell(
                    icon = Icons.Default.Settings,
                    text = "更多设置",
                    onClick = {
                        editedName = showName
                        showSureNameEdit = true

                    }
                )
            }
        }
        if (showSureNameEdit) {
            EditSureNameDialog(
                editedName = editedName,
                onNameChange = { editedName = it },
                onEditConfirm = {
                    showSureNameEdit = false
                    viewModel.updateSureName(editedName)
                },
                onEditCancel = {
                    editedName = showName
                    showSureNameEdit = false
                }
            )
        }
    }
}

@Composable
fun EditSureNameDialog(
    editedName: String,
    onNameChange: (String) -> Unit,
    onEditConfirm: () -> Unit,
    onEditCancel: () -> Unit,
    modifier: Modifier = Modifier
){
    val contactSureNameState = rememberTextFieldState(initialText = "备注")
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onEditCancel){
        Card(
            modifier = Modifier
                .height(150.dp)
                .width(350.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "请输入备注",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = editedName,
                    onValueChange = onNameChange,
                    modifier = Modifier
                        .width(250.dp)
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
                            IconButton(onClick = { onNameChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear text",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    TextButton(
                        onClick = { onEditCancel() },
                    ) {
                        Text(
                            text = "取消",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                    }
                    TextButton(
                        onClick = { onEditConfirm() },
                    ) {
                        Text(
                            text="确认",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }

}


//@Composable
//fun SettingsBar(){
//    val sheetState = rememberModalBottomSheetState()
//    var showSheet by remember(mutableStateOf(false))
//
//    Button(onClick = { showSheet = true }) {
//        Text("显示底部弹窗")
//    }
//
//    if (showSheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showSheet = false },
//            sheetState = sheetState
//        ) {
//            // 内容区
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text("底部弹窗内容")
//            }
//        }
//    }
//}


@Preview(showBackground = true)
@Composable
fun EditSureNameDialogPreview(){
    MessageCenterTheme{
        EditSureNameDialog(
            editedName = "张三",
            onNameChange = {},
            onEditConfirm = {},
            onEditCancel = {}
        )}
}

//@Preview(
//    showBackground = true,
//    showSystemUi = true,
//    device = Devices.PIXEL_4
//)
//@Composable
//fun ContactScreenPreview() {
//    MessageCenterTheme{
//        ContactScreen()
//    }
//
//}

//@Preview(
//    showBackground = true,
//    showSystemUi = true,
//    device = Devices.PIXEL_4
//)
//@Composable
//fun ContactScreenPreview() {
//    MessageCenterTheme{
//        val NavController = rememberNavController()
//        ContactScreen(1,  NavController)
//    }
//
//}