package com.example.messagecenter.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.messagecenter.ui.theme.MessageCenterTheme

@Composable
fun SettingScreen(
    contactViewModel: ContactViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = Color(0xFFF2F2F5),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),

                ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null
                )
                Text(
                    text = "设置",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    SettingSell("设置", Icons.Default.Settings, {})
                    HorizontalDivider()
                    SettingSell("设置", Icons.Default.Settings, {})
                }
            }
            Text(
                text = "数据看板",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    SettingSell("个人数据", Icons.Default.AccountCircle, {})
                    HorizontalDivider()
                    SettingSell("后台记录", Icons.Default.Build, {})
                }
            }

            Text(
                text = "测试设置",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    val context = LocalContext.current
                    SettingSell("清空记录", Icons.Default.Delete, {
                        Toast.makeText(context, "已删除全部消息", Toast.LENGTH_SHORT).show()
                        contactViewModel.deleteAllContacts()

                    })
                    HorizontalDivider()
                    SettingSell("注入记录", Icons.Default.Edit, {
                        Toast.makeText(context, "已注入全部消息", Toast.LENGTH_SHORT).show()
                        contactViewModel.insertMockContact(context)
                    })
                }
            }
        }
    }
}



@Composable
fun SettingSell(
    text: String,
    icon: ImageVector,
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingSellPreview(){
    MessageCenterTheme {
        SettingSell("设置", Icons.Default.Settings, {})
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview(){
    MessageCenterTheme {
        SettingScreen()
    }
}