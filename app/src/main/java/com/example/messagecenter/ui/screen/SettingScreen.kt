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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.messagecenter.R
import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.viewmodel.ContactViewModel
import com.example.messagecenter.data.viewmodel.SettingsViewModel
import com.example.messagecenter.ui.component.GrowthDashboardDialog
import com.example.messagecenter.utils.SelectionDialog
import com.example.messagecenter.workers.MessageDispatchWorker

@Composable
fun SettingScreen(
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contactViewModel: ContactViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)

    val themeMode by settingsViewModel.themeMode.collectAsState(initial = 0)
    val languageCode by settingsViewModel.languageCode.collectAsState(initial = "")
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showGrowthDashboard by remember { mutableStateOf(false) }
    val growthStats by contactViewModel.growthStats.collectAsState()

    val enableDevMode by settingsViewModel.enableDevMode.collectAsState(initial = false)
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
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .align(Alignment.CenterStart),
//                    tint = MaterialTheme.colorScheme.onPrimaryContainer
//                )
                Text(
                    text = stringResource(R.string.settings),
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
                text = stringResource(R.string.user_settings),
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
                    BaseSettingSell(
                        text = stringResource(R.string.language),
                        icon = Icons.Default.Person,
                        type = "nav",
                        conClick = { showLanguageDialog = true }
                    )
                    HorizontalDivider()
                    BaseSettingSell(
                        text = stringResource(R.string.theme_mode),
                        icon = Icons.Default.Settings, 
                        type = "nav",
                        conClick = { showThemeDialog = true }
                    )
                }
            }
            Text(
                text = stringResource(R.string.data_board),
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
                    BaseSettingSell(
                        text = stringResource(R.string.background_records),
                        icon = Icons.Default.AccountCircle,
                        type = "nav",
                        conClick = { showGrowthDashboard = true }
                    )
                }
            }

//          if (enableDevMode) {
                Text(
                    text = stringResource(R.string.test_settings),
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
                        BaseSettingSell(
                            text = stringResource(R.string.clear_records),
                            icon = Icons.Default.Delete,
                            type = "action",
                            conClick = {
                                Toast.makeText(context, "已删除全部消息", Toast.LENGTH_SHORT).show()
                                contactViewModel.deleteAllContacts()
                                contactViewModel.deleteAllMessages()
                        })
                        HorizontalDivider()
                        BaseSettingSell(
                            text = stringResource(R.string.inject_records),
                            icon = Icons.Default.Edit,
                            type = "action",
                            conClick = {
                                Toast.makeText(context, "已注入全部消息", Toast.LENGTH_SHORT).show()
                                contactViewModel.insertMockData(context)
                        })
                        HorizontalDivider()
                        SwitchSettingSell(
                            text = stringResource(R.string.accept_new_messages),
                            icon = Icons.Default.Email,
                            checked = enableReceiving,
                            onFalse = {
                                settingsViewModel.toggleEnableReceiving(false)
                                workManager.cancelUniqueWork(MessageDispatchWorker.WORK_NAME)
                                    },
                            onTrue = {
                                settingsViewModel.toggleEnableReceiving(true)
                                val request = OneTimeWorkRequestBuilder<MessageDispatchWorker>()
                                    .addTag(MessageDispatchWorker.WORK_NAME)
                                    .build()

                                workManager.enqueueUniqueWork(
                                    MessageDispatchWorker.WORK_NAME,
                                    ExistingWorkPolicy.KEEP,
                                    request
                                )
                            })
                    }   
                }
            // }
        }
        if (showThemeDialog) {
            SelectionDialog(
                title = stringResource(R.string.choose_theme_mode),
                options = listOf(
                    stringResource(R.string.follow_system),
                    stringResource(R.string.light_mode),
                    stringResource(R.string.dark_mode)
                ),
                selectedIndex = themeMode,
                onDismiss = { showThemeDialog = false },
                onSelect = { index ->
                    settingsViewModel.setThemeMode(index)
                    showThemeDialog = false
                }
            )
        }

        if (showLanguageDialog) {
            val languages = listOf("" to stringResource(R.string.follow_system), "zh" to stringResource(R.string.simplified_chinese), "en" to stringResource(R.string.english))
            val currentIndex = languages.indexOfFirst { it.first == languageCode }.coerceAtLeast(0)
            
            SelectionDialog(
                title = stringResource(R.string.choose_language),
                options = languages.map { it.second },
                selectedIndex = currentIndex,
                onDismiss = { showLanguageDialog = false },
                onSelect = { index ->
                    val selectedCode = languages[index].first
                    settingsViewModel.setLanguageCode(selectedCode)
                    showLanguageDialog = false
                }
            )
        }
        if (showGrowthDashboard) {
            GrowthDashboardDialog(
                stats = growthStats,
                onDismiss = { showGrowthDashboard = false }
            )
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