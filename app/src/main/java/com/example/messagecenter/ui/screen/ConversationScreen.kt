package com.example.messagecenter.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ConversationScreen(
    contactId:Int,
    contactName: String,
    contactSureName: String?,
    contactAvatar: String,
    navController: NavController,
    modifier: Modifier = Modifier
){
    Log.d("ConversationScreen", "已进对话页: $contactName - $contactId")
    val contactName = if (contactSureName.isNullOrEmpty()) {
        contactName
    } else {
        contactSureName
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            navController.popBackStack()
                        }
                    )
                )
                Text(
                    text = contactName,
                    style = MaterialTheme.typography.displayMedium
                )
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            Log.d("ConversationScreen", "进入联系人信息页: $contactName - $contactId")
                            val route = "contact/${contactId}"
                            navController.navigate(route)
                        }
                    )
                )
            }
        }
    ) {

    }
}