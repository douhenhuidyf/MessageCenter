package com.example.messagecenter.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.messagecenter.R
import kotlinx.coroutines.delay

import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.viewmodel.ContactDetailViewModel
import com.example.messagecenter.ui.component.Avatar
import com.example.messagecenter.ui.component.ContactSettingCell
import com.example.messagecenter.ui.theme.MessageCenterTheme
import com.example.messagecenter.ui.component.TextBar
import com.example.messagecenter.utils.BaseDialog
import com.example.messagecenter.utils.PopPosition

@Composable
fun ContactScreen(
    navController: NavController,
    viewModel : ContactDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showMoreSettings by rememberSaveable{ mutableStateOf(false) }
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
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
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
                ContactSettingCell(icon = Icons.Default.Favorite, text = stringResource(R.string.follow), onClick = {})
                ContactSettingCell(icon = if (uiState.isMuted) Icons.Default.Lock else Icons.Default.Notifications, text = stringResource(R.string.mute), onClick = { viewModel.updateIsMuted(!uiState.isMuted) })
                ContactSettingCell(
                    icon = Icons.Default.Settings,
                    text = stringResource(R.string.more_settings),
                    onClick = {
                        showMoreSettings = true
                    }
                )
            }
        }

        if (showMoreSettings) {
            SettingsDialog(
                isPinned = uiState.isPinned,
                isMuted = uiState.isMuted,
                onPinChange = { viewModel.updateIsPinned(it) },
                onMuteChange = { viewModel.updateIsMuted(it) },
                onEditSureNameClick = {
                    showMoreSettings = false
                    editedName = showName
                    showSureNameEdit = true
                },
                onDismiss = { showMoreSettings = false }
            )
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
fun SettingsDialog(
    isPinned: Boolean,
    isMuted: Boolean,
    onPinChange: (Boolean) -> Unit,
    onMuteChange: (Boolean) -> Unit,
    onEditSureNameClick: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        position = PopPosition.BOTTOM
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                BaseSettingSell(
                    text = stringResource(R.string.edit_sure_name),
                    icon = Icons.Default.Edit,
                    type = "nav",
                    conClick = onEditSureNameClick
                )
                HorizontalDivider()
                SwitchSettingSell(
                    text = stringResource(R.string.pin_chat),
                    icon = Icons.Default.Star,
                    checked = isPinned,
                    onFalse = { onPinChange(false) },
                    onTrue = { onPinChange(true) }
                )
                HorizontalDivider()
                SwitchSettingSell(
                    text = stringResource(R.string.mute),
                    icon = Icons.Default.Notifications,
                    checked = isMuted,
                    onTrue = { onMuteChange(true) },
                    onFalse = { onMuteChange(false) }
                )
            }
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
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        delay(100)
        keyboardController?.show()
    }

    Dialog(onDismissRequest = onEditCancel){
        Card(
            modifier = Modifier
                .height(150.dp)
                .width(250.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.edit_sure_name_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextBar(
                    inputText = editedName,
                    onValueChange = {
                        onNameChange(it)
                    },
                    promptText = "",
                    imeAction = ImeAction.Done,
                    focusRequester = focusRequester,
                    modifier = Modifier.width(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    TextButton(
                        onClick = { onEditCancel() },
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                    }
                    TextButton(
                        onClick = { onEditConfirm() },
                    ) {
                        Text(
                            text= stringResource(R.string.confirm),
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