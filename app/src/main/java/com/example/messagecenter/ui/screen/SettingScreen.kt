package com.example.messagecenter.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.viewmodel.SettingsViewModel
import com.example.messagecenter.ui.theme.MessageCenterTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun SettingScreen(
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contactViewModel: ContactViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current

    val enableReceiving by settingsViewModel.enableReceiving.collectAsState(initial = false)

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp),
                ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "设置",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Center) 
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .padding(horizontal = 8.dp)
                .padding(top = 16.dp),
        ) {
            Text(
                text = "用户设置",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    BaseSettingSell("设置", Icons.Default.Settings, "nav",{})
                    HorizontalDivider()
                    BaseSettingSell("设置", Icons.Default.Settings, "action",{})
                }
            }
            Text(
                text = "数据看板",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    BaseSettingSell("个人数据", Icons.Default.AccountCircle, "nav",{})
                    HorizontalDivider()
                    BaseSettingSell("后台记录", Icons.Default.Build, "nav",{})
                }
            }

            Text(
                text = "测试设置",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    val context = LocalContext.current
                    BaseSettingSell("清空记录", Icons.Default.Delete, "action", {
                        Toast.makeText(context, "已删除全部消息", Toast.LENGTH_SHORT).show()
                        contactViewModel.deleteAllContacts()
                    })
                    HorizontalDivider()
                    BaseSettingSell("注入记录", Icons.Default.Edit, "action", {
                        Toast.makeText(context, "已注入全部消息", Toast.LENGTH_SHORT).show()
                        contactViewModel.insertMockContact(context)
                        contactViewModel.insertMockConversation(context)
                    })
                    HorizontalDivider()
                    SwitchSettingSell("接受新消息", Icons.Default.Email,
                        checked = enableReceiving,
                        onFalse = { settingsViewModel.toggleEnableReceiving(false) },
                        onTrue = { settingsViewModel.toggleEnableReceiving(true) })
                }
            }
        }
    }
}



@Composable
fun BaseSettingSell(
    text: String,
    icon: ImageVector,
    type: String,
    conClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    conClick()
                }),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        if (type == "nav"){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
        else if (type == "action"){
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null
            )
        }
    }
}

@Composable
fun  SwitchSettingSell(
    text: String,
    icon: ImageVector,
    checked: Boolean,
    onFalse: () -> Unit,
    onTrue: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = {
                if (it){
                    onTrue()
                }
                else{
                    onFalse()
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )
    }
}



//@Preview(showBackground = true)
//@Composable
//fun BaseSettingSellPreview(){
//    MessageCenterTheme {
//        BaseSettingSell("设置", Icons.Default.Settings, "nav",{})
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun SwitchSettingSellPreview(){
//    MessageCenterTheme {
//        SwitchSettingSell("接受新消息", Icons.Default.Settings, {},{})
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun SettingScreenPreview(){
//    MessageCenterTheme {
//        SettingScreen()
//    }
//}